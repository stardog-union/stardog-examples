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

package com.complexible.stardog.examples.connectable.materialization;


import java.util.Properties;

import com.complexible.stardog.db.Connectable;
import com.complexible.stardog.db.ConnectableConnection;
import com.complexible.stardog.db.ConnectionContext;
import com.complexible.stardog.index.Index;
import com.complexible.stardog.index.IndexConnection;
import com.complexible.stardog.index.IndexReader;
import com.complexible.stardog.reasoning.ConnectableReasoner;
import com.complexible.stardog.reasoning.ConnectableReasonerConnection;
import com.complexible.stardog.reasoning.blackout.BlackoutConnection;
import com.google.common.base.Preconditions;

/**
 * Connectable implementation for accessing the reasoner and options, and providing this information to GraphScale.
 *
 * @author Pavel Klinov
 */
final class MaterializationConnectable implements Connectable {

	private boolean mClosed = false;

	private boolean mInitialized = false;

	private final Index mIndex;

	private final String mDB;

	private final ConnectableReasoner mConnectableReasoner;

	private final Properties mProperties;

	MaterializationConnectable(final String theDB, final Index theIndex, final ConnectableReasoner theConnectableReasoner, final Properties theProperties) {
		mIndex = theIndex;
		mDB = theDB;
		mConnectableReasoner = theConnectableReasoner;
		mProperties = theProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() throws Exception {
		Preconditions.checkState(!mClosed, "Already closed");

		if (!mInitialized) {
			mInitialized = true;
			mConnectableReasoner.initialize();

			try (IndexConnection aConnection = mIndex.openConnection();
			     IndexReader aReader = aConnection.getReader();
			     ConnectableReasonerConnection aReasonerConn = mConnectableReasoner.openConnection(ConnectionContext
				                                                                                       .builder()
				                                                                                       .put(ConnectionContext.NAME, mDB)
				                                                                                       .put(IndexConnection.class, aConnection)
				                                                                                       .put(ConnectionContext.REASONING_ENABLED, true)
				                                                                                       .build())) {
				// TODO create an instance of the system responsible for materialization
				Materializer aMaterializer = new DummyMaterializer();

				aMaterializer.initialize(((BlackoutConnection)aReasonerConn.getReasonerConnection()).getKB().getAxioms(),
				                         mProperties);

				aMaterializer.materialize(aReader, theBatch -> { /* write this to the index */ });
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
		String aDbName = theContext.require(ConnectionContext.NAME, String.class);

		return new MaterializationConnectableConnection(aDbName);
	}
}
