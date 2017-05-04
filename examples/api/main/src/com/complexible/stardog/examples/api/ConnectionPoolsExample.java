/*
 * Copyright (c) 2010-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.stardog.examples.api;

import java.util.concurrent.TimeUnit;

import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.ConnectionPool;
import com.complexible.stardog.api.ConnectionPoolConfig;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;

/**
 * <p>A simple example to show how to setup and use ConnectionPools with Stardog</p>
 *
 * @author Michael Grove
 * @version 4.0
 * @since 0.5.1
 */
public class ConnectionPoolsExample {

	// Using Connection Pools
	// -------------------
	// In this example, we illustrate the configuration and use of the SNARL [ConnectionPool](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/ConnectionPool.html)
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

		try {
			// Second create a temporary database to use (if there is one already, drop it first)
			try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer().credentials("admin", "admin").connect()) {
				if (aAdminConnection.list().contains("testConnectionPool")) {
					aAdminConnection.drop("testConnectionPool");
				}
				aAdminConnection.createMemory("testConnectionPool");
			}

			// Pools are based around a [ConnectionConfiguration](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/ConnectionConfiguration.html).
			// This configuration tells the pool how to create the new connections as they are needed.
			ConnectionConfiguration aConnConfig = ConnectionConfiguration
				                                      .to("testConnectionPool")
				                                      .credentials("admin", "admin");

			// Now we want to create the [configuration for our pool](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/ConnectionPoolConfig.html).
			// We start by providing the `ConnectionConfiguration` we just created, that's the basis of the pool.  Then
			// we can configure some aspects of the pool such as expiration time and maximum size.
			ConnectionPoolConfig aConfig = ConnectionPoolConfig
				                               .using(aConnConfig)
				                               .minPool(10)
				                               .maxPool(1000)
				                               .expiration(1, TimeUnit.HOURS)
				                               .blockAtCapacity(1, TimeUnit.MINUTES);

			// Once we have a valid configuration, we can actually create the `ConnectionPool`
			ConnectionPool aPool = aConfig.create();

			// Which we can use to get our Connections from this point forward
			Connection aConn = aPool.obtain();

			// And after we've done our work with the connection, instead of closing it, I want to return it to the pool instead.
			aPool.release(aConn);

			// When you're done with the pool, shut it down.  This will release all pooled connections.
			aPool.shutdown();
		}
		finally {
			// always shut down the instance when you are done with it
			aStardog.shutdown();
		}
	}
}
