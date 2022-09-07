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

import java.io.PrintStream;
import java.util.Optional;

import com.complexible.stardog.db.ConnectableFactory;
import com.complexible.stardog.db.ConnectableMetadata;
import com.complexible.stardog.index.Index;
import com.complexible.stardog.util.backup.Backup;

/**
 * Example connectable factory. This factory is a singleton and used to create one connectable for each database instance.
 *
 * @author  Evren Sirin
 */
final class ExampleConnectableFactory implements ConnectableFactory<ExampleConnectable> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ExampleConnectable> create(final ConnectableMetadata theMetadata, final Index theIndex) throws Exception {
		return Optional.of(new ExampleConnectable(theMetadata));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ExampleConnectable> restore(final ConnectableMetadata theMetadata, final Index theIndex,
	                                            final Backup theBackup, final PrintStream theStream) throws Exception {
		return Optional.of(new ExampleConnectable(theMetadata));
	}
}
