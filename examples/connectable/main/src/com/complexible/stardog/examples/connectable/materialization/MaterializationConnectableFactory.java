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

import java.io.PrintStream;
import java.util.Optional;
import java.util.Properties;

import com.complexible.common.inject.OptionalConstructorParam;
import com.complexible.stardog.db.ConnectableFactory;
import com.complexible.stardog.db.ConnectableMetadata;
import com.complexible.stardog.db.DatabaseOptions;
import com.complexible.stardog.index.Index;
import com.complexible.stardog.metadata.Metadata;
import com.complexible.stardog.reasoning.ConnectableReasonerFactory;
import com.complexible.stardog.util.backup.Backup;
import com.google.inject.Inject;

/**
 * This factory is a singleton and used to create one connectable for each database instance.
 *
 * @author  Pavel Klinov
 */
final class MaterializationConnectableFactory implements ConnectableFactory<MaterializationConnectable> {

	private final ConnectableReasonerFactory mConnectableReasonerFactory;

	@Inject
	public MaterializationConnectableFactory(final OptionalConstructorParam<ConnectableReasonerFactory> theReasonerFactory) {
		mConnectableReasonerFactory = theReasonerFactory.orNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<MaterializationConnectable> create(final ConnectableMetadata theMetadata, final Index theIndex) throws Exception {
		if (theMetadata.get().is(MaterializationOptions.MATERIALIZATION_ENABLED)) {
			// extract materialization properties here
			Properties aMaterializationProps = getMaterializationProperties(theMetadata.get());

			return Optional.of(new MaterializationConnectable(theMetadata.get(DatabaseOptions.NAME),
			                                                  theIndex, mConnectableReasonerFactory == null
			                                                            ?
			                                                            null
			                                                            : mConnectableReasonerFactory.create(theMetadata, theIndex).orElse(null),
			                                                  aMaterializationProps));
		}

		return Optional.empty();
	}

	private Properties getMaterializationProperties(final Metadata theMetaProperties) {
		Properties aProps = new Properties();

		theMetaProperties.iterator().forEachRemaining(theProp -> {
			if (theProp.getName().startsWith(MaterializationOptions.PREFIX)) {
				aProps.setProperty(theProp.getName().replaceFirst(MaterializationOptions.PREFIX, ""), theMetaProperties.get(theProp).toString());
			}
		});

		return aProps;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<MaterializationConnectable> restore(final ConnectableMetadata theMetadata, final Index theIndex,
	                                                final Backup theBackup, final PrintStream theStream) throws Exception {
		return Optional.of(new MaterializationConnectable(theMetadata.get(DatabaseOptions.NAME), theIndex, mConnectableReasonerFactory == null
		                                                                                                   ?
		                                                                                                   null
		                                                                                                   : mConnectableReasonerFactory.create(theMetadata, theIndex).orElse(null),
		                                                  getMaterializationProperties(theMetadata.get())));
	}
}
