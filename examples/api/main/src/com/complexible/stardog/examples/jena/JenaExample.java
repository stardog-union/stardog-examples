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

package com.complexible.stardog.examples.jena;

import java.io.FileInputStream;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.examples.TestServer;
import com.complexible.stardog.jena.SDJenaFactory;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;


/**
 * <p>Example of how to use the Jena integration with stardog</p>
 *
 * @author Michael Grove
 * @version 4.0
 * @since 0.3.3
 */
public class JenaExample {

	// Using Stardog with the [Jena](http://jena.apache.org) API
	// -------------------
	// In this example we'll show how to use the Stardog Jena API bindings.
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the http server.
		TestServer aStardog = new TestServer();

		try {
			// Next we'll establish a admin connection to Stardog so we can create a database to use for the example
			try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toServer(aStardog.getServerURL())
			                                                                    .credentials("admin", "admin")
			                                                                    .connect()) {
				// If the database already exists, we'll drop it and create a fresh copy
				if (aAdminConnection.list().contains("testJena")) {
					aAdminConnection.drop("testJena");
				}

				aAdminConnection.disk("testJena").create();

				// Now we open a Connection our new database
				try (Connection aConn = ConnectionConfiguration
					                        .to("testJena")
					                        .server(aStardog.getServerURL())
					                        .credentials("admin", "admin")
					                        .connect()) {

					// Then we obtain a Jena `Model` for the specified stardog database which is backed by our `Connection`
					Model aModel = SDJenaFactory.createModel(aConn);

					// Start a transaction before adding the data.  This is not required, but it is faster to group the entire add into a single transaction rather
					// than rely on the auto commit of the underlying stardog connection.
					aModel.begin();

					// Read data into the model.  note, this will add statement at a time.  Bulk loading needs to be performed directly with the BulkUpdateHandler provided
					// by the underlying graph, or read in files in RDF/XML format, which uses the bulk loader natively.  Alternatively, you can load data into the stardog
					// database using it's native API via the command line client.
					aModel.getReader("TURTLE").read(aModel, new FileInputStream("data/sp2b.ttl"), "");

					// When you're done adding, you need to commit the changes
					aModel.commit();

					// Query that we will run against the data we just loaded
					String aQueryString = "select * where { ?s ?p ?o. filter(?s = <http://localhost/publications/articles/Journal1/1940/Article1>).}";

					// Create a query...
					Query aQuery = QueryFactory.create(aQueryString);

					// ... and run it
					try (QueryExecution aExec = QueryExecutionFactory.create(aQuery, aModel)) {
						// Now print the results
						ResultSetFormatter.out(aExec.execSelect(), aModel);
					}
				}
				finally {
					if (aAdminConnection.list().contains("testJena")) {
						aAdminConnection.drop("testJena");
					}
				}
			}
		}
		finally {
			aStardog.shutdown();
		}
	}
}
