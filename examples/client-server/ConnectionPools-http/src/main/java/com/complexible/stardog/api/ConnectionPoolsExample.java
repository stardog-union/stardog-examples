// Copyright (c) 2010 - 2012 -- Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.examples.api;

import java.util.concurrent.TimeUnit;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionPool;
import com.complexible.stardog.api.ConnectionPoolConfig;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;

/**
 * <p>A simple example to show how to setup and use ConnectionPools with Stardog</p>
 *
 * @author  Michael Grove
 * @since   0.5.1
 * @version 2.0
 */
public class ConnectionPoolsExample {

	public static void main(String[] args) throws Exception {
		// Specify the server URL
		String aServerUrl = "http://localhost:5820";

		// First create a temporary database to use (if there is one already, drop it first)
		AdminConnection aAdminConnection = AdminConnectionConfiguration.toServer(aServerUrl).credentials("admin", "admin").connect();
		if (aAdminConnection.list().contains("testConnectionPool")) {
			aAdminConnection.drop("testConnectionPool");
		}
		aAdminConnection.createMemory("testConnectionPool");
		aAdminConnection.close();

		// Now, we need a configuration object for our connections, this is all the information about
		// the database that we want to connect to.
		ConnectionConfiguration aConnConfig = ConnectionConfiguration
			                                      .to("testConnectionPool")
			                                      .server(aServerUrl)
			                                      .credentials("admin", "admin");

		// We want to create a pool over these objects.  See the javadoc for ConnectionPoolConfig for
		// more information on the options and information on the defaults.
		ConnectionPoolConfig aConfig = ConnectionPoolConfig
			                               .using(aConnConfig)							// use my connection configuration to spawn new connections
			                               .minPool(10)								// the number of objects to start my pool with
			                               .maxPool(1000)								// the maximum number of objects that can be in the pool (leased or idle)
			                               .expiration(1, TimeUnit.HOURS)				// Connections can expire after being idle for 1 hr.
			                               .blockAtCapacity(1, TimeUnit.MINUTES);		// I want obtain to block for at most 1 min while trying to obtain a connection.

		// now i can create my actual connection pool
		ConnectionPool aPool = aConfig.create();

		// if I want a connection object...
		Connection aConn = aPool.obtain();

		// now I can feel free to use the connection object as usual...

		// and when I'm done with it, instead of closing the connection, I want to return it to the pool instead.
		aPool.release(aConn);

		// and when I'm done with the pool, shut it down!
		aPool.shutdown();
	}
}
