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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.complexible.common.base.Change;
import com.complexible.common.base.Options;
import com.complexible.common.rdf.StatementSource;
import com.complexible.common.rdf.StatementSources;
import com.complexible.stardog.db.ConnectableConnection;
import com.complexible.stardog.db.tx.IndexChange;
import com.complexible.stardog.index.Index;
import com.complexible.stardog.plan.optimizer.OptimizationPipeline;
import com.complexible.tx.api.FatalException;
import com.complexible.tx.api.HeuristicRollbackException;
import com.complexible.tx.api.IllegalTransactionStateException;
import com.complexible.tx.api.ResourceTransactionException;
import com.complexible.tx.api.Transaction;
import com.complexible.tx.api.Transactions;
import com.complexible.tx.api.event.TransactionCommitEvent;
import com.complexible.tx.api.event.TransactionDataEvent;
import com.complexible.tx.api.logging.recovery.DefaultRecoveryContext;
import com.complexible.tx.api.logging.recovery.RecoveryContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Index access connection implementation. This class registers transaction listeners for the connection so that triples/quads added/removed during a transaction
 * can be seen and processed as needed. This specific implementation simply writes the triples to the log file but any user-specific functionality can be
 * implemented here.
 *
 * @author  Evren Sirin
 */
final class IndexAccessConnectableConnection implements ConnectableConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexAccessConnectableConnection.class);

	private final Index mIndex;

	private boolean mClosed = false;

	IndexAccessConnectableConnection(final Index theIndex) {
		mIndex = theIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> as(final Class<T> theType) {
		Preconditions.checkState(isOpen(), "Cannot use a closed connection");

		return theType.isInstance(this)
		       ? Optional.of((T) this)
		       : Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOpen() {
		return !mClosed;
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
	public void optimize(final OptimizationPipeline.OptimizationPipelineBuilder theOptimizationPipeline) {
		// no optimizations are needed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Options getConnectionProperties() {
		Preconditions.checkState(isOpen(), "Cannot use a closed connection");

		// no specific connection properties
		return Options.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RecoveryContext createRecoveryContext() {
		Preconditions.checkState(isOpen(), "Cannot use a closed connection");
		// no recovery required, use default
		return DefaultRecoveryContext.builder().build();
	}

	@Override
	public Set<String> getQueryRewritings() {
		// no query rewritings are performed
		return ImmutableSet.of();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void join(final Transaction theTransaction) throws FatalException, HeuristicRollbackException,
	                                                          IllegalTransactionStateException,
	                                                          ResourceTransactionException {
		Preconditions.checkState(isOpen(), "Cannot use a closed connection");

		// we will buffer all changes until commit because the transaction will have no effect unless it is committed
		final List<Change> aChanges = Lists.newArrayList();

		// attach a listener for data events. every time triples are added/removed directly or via a SPARQL update
		// query an associated event will be fired
		Transactions.listenFor(theTransaction, TransactionDataEvent.class, theEvent -> {
			Object aTxData = theEvent.getData().getData();
			// only process index changes
			if (aTxData instanceof Change && ((Change) aTxData).getChangeType() instanceof IndexChange) {
				aChanges.add((Change) aTxData);
			}
		});

		Transactions.listenFor(theTransaction, TransactionCommitEvent.class, theEvent -> {
			processIndexChange(aChanges);
		});
	}

	/**
	 * The function where index changes are processed. This is the only place that needs to be modified in this example to
	 * implement a custom behavior.
	 *
	 * @param theChanges list of index changes performed during this trasnaction
	 */
	private void processIndexChange(final List<Change> theChanges) {
		for (Change aChange : theChanges) {
			IndexChange aChangeType = (IndexChange) aChange.getChangeType();
			// there is one change type for additions but three different change types for removals: Remove, ClearContext, ClearAll
			boolean isAdd = aChangeType == IndexChange.Add;
			// for all change types there is an associated statement source with the exact triples that are being added/removed
			// NOTE: there is no guarantee that these changes actually modified the database. it is possible that this is a removal
			// but the removed triples did not exist in the database. or the triples in an addition already existed in the database.
			StatementSource aStatements = (StatementSource) aChange.getChange();

			// one can iterate over the statements manually
			// try (StatementIterator i = aStatements.statements()) {
			//	while (i.hasNext()) {
			//		Statement aStatement = i.next();
			//		System.out.println(aStatement);
			//	}
			//}

			// or write the contents of the statement source in an RDF format
			// NOTE: it is possible statements are associated with a named graph so it might
			// be necessary to use a named graph aware RDF format here
			try {
				StatementSources.write(aStatements, RDFFormat.NQUADS, System.out);
			}
			catch (IOException e) {
				LOGGER.warn("Error while writing transaction data", e);
			}
		}
	}

}
