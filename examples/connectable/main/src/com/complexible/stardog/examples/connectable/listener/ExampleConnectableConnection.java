/*
 * Copyright (c) 2010-2018 Stardog Union. <https://stardog.com>
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;

import com.complexible.common.base.Change;
import com.complexible.common.base.Options;
import com.complexible.common.rdf.StatementSource;
import com.complexible.common.rdf.StatementSources;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.db.ConnectableConnection;
import com.complexible.stardog.db.tx.IndexChange;
import com.complexible.stardog.db.tx.TxFormats;
import com.complexible.stardog.plan.optimizer.OptimizationPipeline;
import com.complexible.tx.api.BaseResourceTransaction;
import com.complexible.tx.api.FatalException;
import com.complexible.tx.api.HeuristicRollbackException;
import com.complexible.tx.api.IllegalTransactionStateException;
import com.complexible.tx.api.PrepareResult;
import com.complexible.tx.api.ResourceTransaction;
import com.complexible.tx.api.ResourceTransactionException;
import com.complexible.tx.api.Transaction;
import com.complexible.tx.api.TransactionData;
import com.complexible.tx.api.logging.recovery.DefaultRecoveryContext;
import com.complexible.tx.api.logging.recovery.RecoveryContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.stardog.stark.io.RDFFormats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener connection implementation. This class receives RDF statements data when it's transactionally added to or removed from
 * a databases and prints it to the standard output. Any user-specific processing functionality can be implemented here.
 *
 * @author  Evren Sirin
 */
final class ExampleConnectableConnection implements ConnectableConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConnectableConnection.class);

	/**
	 * Each connection should declare what kind of changes (additions or removals) it will track so the server can optimize
	 * the callbacks accordingly. If a certain change type is not included in the {@link #getTrackedIndexChanges()} result
	 * the connection is not guaranteed to receive the data associated with that change.
	 */
	private static final EnumSet<IndexChange> TRACKED_CHANGES = EnumSet.allOf(IndexChange.class);

	private final String mDb;

	private final BooleanSupplier isEnabled;

	private boolean mClosed = false;

	// we will buffer all changes until commit because the transaction will have no effect unless it is committed
	private final List<Change> mChanges = Lists.newArrayList();

	ExampleConnectableConnection(final String theDb, final BooleanSupplier theEnabled) throws FileNotFoundException {
		mDb = theDb;
		isEnabled = theEnabled;
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
		       : Optional.<T>empty();
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

	@Override
	public EnumSet<IndexChange> getTrackedIndexChanges() {
		return TRACKED_CHANGES;
	}

	@Override
	public void apply(final TransactionData<Change<IndexChange, StatementSource>, TxFormats.RDF> theData) throws StardogException {
		mChanges.add(theData.getData());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void join(final Transaction theTransaction) throws FatalException, HeuristicRollbackException,
	                                                          IllegalTransactionStateException,
	                                                          ResourceTransactionException {
		Preconditions.checkState(isOpen(), "Cannot use a closed connection");

		// since the database option is defined to be `writableWhileOnline` we need to check the value at every transaction
		// since the user can modify the value at any time. if the option was defined as only `writable` then we could do
		// this check in the constructor or way earlier in `ExampleConnectableFactory` and not create the connectable for
		// this database. when a database is offlined and then onlined all the connectables are recreated so options
		// that are not `writableWhileOnline` can be checked only once.
		if (!isEnabled.getAsBoolean()) {
			return;
		}

		System.out.println("Transaction started for database " + mDb);

		// registering a child transaction object which will handle all transactional events, e.g. commits and rollbacks
		theTransaction.join(new BaseResourceTransaction(ResourceTransaction.META_TRANSACTION) {
			@Override
			protected PrepareResult prepare() throws ResourceTransactionException {
				return PrepareResult.success();
			}

			@Override
			protected void commit() throws ResourceTransactionException {
				processIndexChange(mChanges);
			}

			@Override
			protected void rollback() throws ResourceTransactionException {
				mChanges.clear();
			}
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

			// first just print info about the transaction. the output will go to the stardog.log file
			System.out.println("Database: " + mDb + " Change: " + aChangeType + " Addition: " + isAdd);


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
				StatementSources.write(aStatements, RDFFormats.NQUADS, System.out);
			}
			catch (IOException e) {
				LOGGER.warn("Error while writing transaction data", e);
			}
		}
	}

}
