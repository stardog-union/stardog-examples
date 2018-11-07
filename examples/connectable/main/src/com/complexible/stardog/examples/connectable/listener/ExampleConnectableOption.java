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

import com.complexible.stardog.metadata.ConfigProperty;
import com.complexible.stardog.metadata.MetaPropertyProvider;

import static com.complexible.stardog.metadata.MetaProperty.config;

/**
 * Example class for defining a new database option. If the custom connectable requires some configuration parameters they can be defined
 * in this class, set by users using the regular admin functionality and read by the connectable at runtime.
 *
 * @author  Evren Sirin
 */
public final class ExampleConnectableOption implements MetaPropertyProvider {
	public static final ConfigProperty<Boolean> ENABLE_LOGGING = config("example.connectable.enabled", true)
			                                                           .database()
			                                                           .creatable()
			                                                           .readable()
			                                                           .writableWhileOnline()
			                                                           .build();
}
