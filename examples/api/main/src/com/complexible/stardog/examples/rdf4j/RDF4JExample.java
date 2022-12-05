
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

package com.complexible.stardog.examples.rdf4j;

import java.io.File;

import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.examples.TestServer;
import com.complexible.stardog.rdf4j.StardogRepository;
import com.stardog.common.rdf4j.repository.RepositoryConnections;

import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 * <p>A basic example of using Stardog via the RDF4J API</p>
 *
 * @author Michael Grove
 * @version 2.0
 * @since 0.4
 */
public class RDF4JExample {

	// Using Stardog with the [RDF4J](http://rdf4j.org) API
	// -------------------
	// In this example we'll show how to use the bindings for the RDF4J API to use Stardog as a drop in replacement
	// for an existing `Repository` based application.
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the http server.
		TestServer aStardog = new TestServer();

		try {
			// Using AdminConnection
			// ---------------------
			// Now that the server is running, we want to create a connection to the DBMS itself so we can do
			// some administrative stuff, namely, creating a new database to use for the purpose of this example.
			// We need to create a connection to perform administrative actions, so we can use the `AdminConnectionConfiguration`
			// utility class for opening the connection.
			//
			// Most operations supported by the DBMS require specific permissions, so either an admin account
			// is required, or a user who has been granted the ability to perform the actions.  You can learn
			// more about this in the [Security chapter](http://docs.stardog.com/security).

			try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toServer(aStardog.getServerURL()).credentials("admin", "admin").connect()) {
				// With our admin connection, we're able to see if the database for this example already exists, and
				// if it does, we want to drop it and re-create so that we can run the example from a clean database.
				if (aAdminConnection.list().contains("testRDF4J")) {
					aAdminConnection.drop("testRDF4J");
				}

				// Convenience function for creating a non-persistent in-memory database with all the default settings.
				aAdminConnection.disk("testRDF4J").create();

				// Create a RDF4J `Repository` from a Stardog `ConnectionConfiguration`.  The configuration will be used
				// when creating new `RepositoryConnection` objects
				Repository aRepo = new StardogRepository(ConnectionConfiguration
					                                         .to("testRDF4J")
					                                         .server(aStardog.getServerURL())
					                                         .credentials("admin", "admin"));

				// You must always initialize a `Repository`
				aRepo.initialize();

				try {
					// Let's open a connection to the database, add some data, then query it
					try (RepositoryConnection aRepoConn = aRepo.getConnection()) {
						// First add some data to the connection so we can query it.
						RepositoryConnections.add(aRepoConn, new File("data/sp2b.ttl"));

						// Now we can query the data we just loaded into the database
						TupleQuery aQuery = aRepoConn.prepareTupleQuery(QueryLanguage.SPARQL, "select * where { ?s ?p ?o. filter(?s = <http://localhost/publications/articles/Journal1/1940/Article1>).}");

						try (TupleQueryResult aResults = aQuery.evaluate()) {
							// Print the results to the console
							QueryResultIO.writeTuple(aResults, TupleQueryResultFormat.TSV, System.out);
						}
					}
				}
				finally {
					// Make sure you shut down the repository as well as closing the repository connection as this is what
					// releases the internal Stardog connections and closes the connection pool
					aRepo.shutDown();
				}

				aAdminConnection.drop("testRDF4J");
			}
		}
		finally {
			aStardog.shutdown();
		}

	}
}
