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

import com.complexible.stardog.AbstractStardogModule;
import com.complexible.stardog.db.ConnectableFactory;
import com.complexible.stardog.metadata.MetaProperties;

import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

/**
 * Guice module to register the example connectable factory.
 *
 * @author  Evren Sirin
 */
public final class ExampleModule extends AbstractStardogModule {
	@Override
	protected void configure() {
		// database options need to be registered at the beginning
		MetaProperties.register(ExampleConnectableOption.class);

		Multibinder.newSetBinder(binder(), ConnectableFactory.class)
		           .addBinding()
		           .to(ExampleConnectableFactory.class)
		           .in(Singleton.class);
	}
}
