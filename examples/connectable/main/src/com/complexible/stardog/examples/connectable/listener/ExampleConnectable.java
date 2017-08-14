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

package com.complexible.stardog.examples.connectable.listener;

import com.complexible.stardog.db.Connectable;
import com.complexible.stardog.db.ConnectableConnection;
import com.complexible.stardog.db.ConnectionContext;
import com.google.common.base.Preconditions;

/**
 * Connectable implementation for the example. Sole responsibility for this class is to create a (sub)connection when the
 * user connects to a database.
 *
 * @author  Evren Sirin
 */
final class ExampleConnectable implements Connectable {
	private boolean mClosed = false;

	private boolean mInitialized = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() throws Exception {
		Preconditions.checkState(!mClosed, "Already closed");

		if (!mInitialized) {
			mInitialized = true;
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
		// retrieve the database name from the connnection context and pass it to the connection
		String aDbName = theContext.require(ConnectionContext.NAME, String.class);
		return new ExampleConnectableConnection(aDbName);
	}
}
