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

import java.util.Optional;
import java.util.Set;

import com.complexible.common.base.Options;
import com.complexible.stardog.db.ConnectableConnection;
import com.complexible.stardog.plan.optimizer.OptimizationPipeline;
import com.complexible.tx.api.BaseResourceTransaction;
import com.complexible.tx.api.FatalException;
import com.complexible.tx.api.HeuristicRollbackException;
import com.complexible.tx.api.IllegalTransactionStateException;
import com.complexible.tx.api.PrepareResult;
import com.complexible.tx.api.ResourceTransaction;
import com.complexible.tx.api.ResourceTransactionException;
import com.complexible.tx.api.Transaction;
import com.complexible.tx.api.logging.recovery.DefaultRecoveryContext;
import com.complexible.tx.api.logging.recovery.RecoveryContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author  Evren Sirin
 */
final class MaterializationConnectableConnection implements ConnectableConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(MaterializationConnectableConnection.class);

	private final String mDb;

	private boolean mClosed = false;

	MaterializationConnectableConnection(final String theDb) {
		mDb = theDb;
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

		System.out.println("Transaction started for database " + mDb);

		// registering a child transaction object which will handle all transactional events, e.g. commits and rollbacks
		theTransaction.join(new BaseResourceTransaction(ResourceTransaction.META_TRANSACTION) {
			@Override
			protected PrepareResult prepare() throws ResourceTransactionException {
				return PrepareResult.success();
			}

			@Override
			protected void commit() throws ResourceTransactionException {
			}

			@Override
			protected void rollback() throws ResourceTransactionException {
			}
		});
	}

}
