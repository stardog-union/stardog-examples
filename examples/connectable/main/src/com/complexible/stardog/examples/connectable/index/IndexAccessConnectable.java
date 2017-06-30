/*
 * Copyright (c) 2010-2016 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.complexible.stardog.examples.connectable.index;


import java.util.Arrays;

import com.carrotsearch.hppc.LongHashSet;
import com.carrotsearch.hppc.LongSet;
import com.complexible.common.collect.BufferLists;
import com.complexible.common.collect.SkippingIterator;
import com.complexible.common.rdf.impl.MemoryStatementSource;
import com.complexible.stardog.db.Connectable;
import com.complexible.stardog.db.ConnectableConnection;
import com.complexible.stardog.db.ConnectionContext;
import com.complexible.stardog.index.ID;
import com.complexible.stardog.index.Index;
import com.complexible.stardog.index.IndexConnection;
import com.complexible.stardog.index.IndexOrder;
import com.complexible.stardog.index.IndexOrders;
import com.complexible.stardog.index.IndexReader;
import com.complexible.stardog.index.IndexWriter;
import com.complexible.stardog.index.Quad;
import com.complexible.stardog.index.Quads;
import com.complexible.stardog.index.dictionary.MappingDictionary;
import com.complexible.stardog.index.disk.compression.QuadTransformer;
import com.complexible.stardog.index.disk.compression.QuadTransformers;
import com.google.common.base.Preconditions;
import org.openrdf.model.Statement;

/**
 * Connectable implementation for accessing index. Sole responsibility for this class is to create a (sub)connection when the
 * user connects to a database.
 *
 * @author  Pavel Klinov
 */
final class IndexAccessConnectable implements Connectable {
	private boolean mClosed = false;

	private boolean mInitialized = false;

	private final Index mIndex;

	private final String mDB;

	IndexAccessConnectable(final String theDB, final Index theIndex) {
		mIndex = theIndex;
		mDB = theDB;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() throws Exception {
		Preconditions.checkState(!mClosed, "Already closed");

		if (!mInitialized) {
			mInitialized = true;

			try (IndexConnection aConnection = mIndex.openConnection(); IndexReader aReader = aConnection.getReader()) {
				System.out.println("Printing quads for first 2 predicates in the SPOC index for " + mDB + ":");

				printFirstNPredicates(IndexOrders.<Quad>selectIterator(aReader, IndexOrder.SPOC).iterator(),
				                      aReader.getMappings(),
				                      2);

				System.out.println("Printing inbound edges using the OSPC index for " + mDB + ":");

				// there's usually no OPSC index for similar iteration over inbound edges so we'd need to use OSPC index
				// which is sorted (for every object) by first subjects and then predicates.
				// Alternatively you can use POSC index if the number of relevant predicates is low.
				printAllInboundPredicates(IndexOrders.<Quad>selectIterator(aReader, IndexOrder.OSPC).iterator(),
				                          aReader.getMappings(),
				                          QuadTransformers.getTransformer(IndexOrder.OSPC));

				// if you want to write some data back to the database
				try (IndexWriter aWriter = aConnection.getWriter()) {
					aWriter.begin();
					// low-level writing of quads.
					// you can put your materialization into a dedicated named graph so you always know how to find it
					// (in case of incremental updates, for example)
					aWriter.add().values(BufferLists.fixed(new Quad[] { /* your quads here */}));
					// alternatively you can write RDF statements (not yet encoded as numbers)
					aWriter.add(new MemoryStatementSource(Arrays.asList(new Statement[] { /* your RDF statements here */})));

					aWriter.commit();
				}
			}
		}
	}

	private void printAllInboundPredicates(final SkippingIterator<Quad> theIndexIterator, final MappingDictionary theMappings, final QuadTransformer theTransformer) {
		// we print each inbound predicate only once for each object.
		LongSet aPredicates = new LongHashSet();
		long aCurrentObject = 0;

		while (theIndexIterator.hasNext()) {
			Quad aNextQuad = theIndexIterator.next();

			if (theTransformer.getX(aNextQuad) > aCurrentObject) {
				aPredicates.clear();
				aCurrentObject = theTransformer.getX(aNextQuad);
			}

			if (aPredicates.add(theTransformer.getY(aNextQuad))) {
				System.out.println(String.format(" --%s--> %s",
				                                 theMappings.getValue(aNextQuad.getY()).stringValue(),
				                                 theMappings.getValue(aNextQuad.getX()).stringValue()));
			}
		}
	}

	private void printFirstNPredicates(final SkippingIterator<Quad> theIndexIterator, final MappingDictionary theMappings, final int theN) {
		int aPredicateCounter = 0;
		long aCurrentX = 0;
		long aCurrentPredicate = 0;
		Quad aNextQuad = null;

		for (;;) {
			if (aNextQuad == null) {
				if (theIndexIterator.hasNext()) {
					aNextQuad = theIndexIterator.next();
				}
				else {
					break;
				}
			}

			if (aNextQuad.getX() > aCurrentX) {
				aPredicateCounter = 1;
				aCurrentX = aNextQuad.getX();
				aCurrentPredicate = aNextQuad.getY();
			}
			else if (aNextQuad.getY() > aCurrentPredicate) {
				aPredicateCounter++;
			}

			if (aPredicateCounter <= theN) {
				// here's how you lookup RDF terms by ID in the dictionary
				System.out.println(String.format("%s  --%s--> %s",
				                                 theMappings.getValue(aNextQuad.getX()).stringValue(),
				                                 theMappings.getValue(aNextQuad.getY()).stringValue(),
				                                 theMappings.getValue(aNextQuad.getZ()).stringValue()));
				aNextQuad = null;
			}
			else {
				// this is how you create quads. this one will be used as the target for skipping
				Quad aSkipTarget = Quads.create(aCurrentX + 1, ID.ANY, ID.ANY);
				// move on to the next subject or object
				// will return null if there's no next subject (after that each hasNext on the iterator must return false)
				aNextQuad = theIndexIterator.skipTo(aSkipTarget);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		if (!mClosed) {
			mClosed = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectableConnection openConnection(final ConnectionContext theContext) throws Exception {
		return new IndexAccessConnectableConnection(mIndex);
	}
}
