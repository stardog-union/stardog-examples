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

import com.complexible.stardog.AbstractStardogModule;
import com.complexible.stardog.db.ConnectableFactory;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

/**
 * Guice module to register the index access factory.
 *
 * @author  Pavel Klinov
 */
public final class IndexAccessModule extends AbstractStardogModule {
	@Override
	protected void configure() {
		Multibinder.newSetBinder(binder(), ConnectableFactory.class)
		           .addBinding()
		           .to(IndexAccessConnectableFactory.class)
		           .in(Singleton.class);
	}
}
