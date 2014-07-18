/*
 * Copyright (c) 2010-2014 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import com.complexible.common.protocols.server.Server;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.jena.SDJenaFactory;
import com.complexible.stardog.protocols.snarl.SNARLProtocolConstants;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * <p>Example of how to use the Jena integration with stardog</p>
 *
 * @author  Michael Grove
 * @since   0.3.3
 * @version 2.0
 */
public class JenaExample {
	// Using Stardog with the [Jena](http://jena.apache.org) API
	// -------------------
	// In this example we'll show how to use the Stardog Jena API bindings.
	public static void main(String[] args) throws Exception {
		// Creating a Server
		// -----------------
		// You'll need a server to connect to, obviously.  For the example, lets create an embedded server.
		Server aServer = Stardog
			                 .buildServer()
			                 .bind(SNARLProtocolConstants.EMBEDDED_ADDRESS)
			                 .start();

		try {
			// Next we'll establish a admin connection to Stardog so we can create a database to use for the example
			AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
			                                                               .credentials("admin", "admin")
			                                                               .connect();

			try {
				// If the database already exists, we'll drop it and create a fresh copy
				if (aAdminConnection.list().contains("testJena")) {
					aAdminConnection.drop("testJena");
				}

				aAdminConnection.createMemory("testJena");
			}
			finally {
				aAdminConnection.close();
			}

			// Now we open a Connection our new database
			Connection aConn = ConnectionConfiguration
				                   .to("testJena")
				                   .credentials("admin", "admin")
				                   .connect();

			// Then we obtain a Jena `Model` for the specified stardog database which is backed by our `Connection`
			Model aModel = SDJenaFactory.createModel(aConn);
			try {

				// Start a transaction before adding the data.  This is not required, but it is faster to group the entire add into a single transaction rather
				// than rely on the auto commit of the underlying stardog connection.
				aModel.begin();

				// Read data into the model.  note, this will add statement at a time.  Bulk loading needs to be performed directly with the BulkUpdateHandler provided
				// by the underlying graph, or read in files in RDF/XML format, which uses the bulk loader natively.  Alternatively, you can load data into the stardog
				// database using it's native API via the command line client.
				aModel.getReader("N3").read(aModel, new FileInputStream("data/sp2b_10k.n3"), "");

				// When you're done adding, you need to commit the changes
				aModel.commit();

				// Query that we will run against the data we just loaded
				String aQueryString = "select * where { ?s ?p ?o. filter(?s = <http://localhost/publications/articles/Journal1/1940/Article1>).}";

				// Create a query...
				Query aQuery = QueryFactory.create(aQueryString);

				// ... and run it
				QueryExecution aExec = QueryExecutionFactory.create(aQuery, aModel);

				try {
					// Now print the results
					ResultSetFormatter.out(aExec.execSelect(), aModel);
				}
				finally {
					// Always close the execution
					aExec.close();
				}
			}
			finally {
				// close the model to free up the connection to the stardog database
				aModel.close();
			}
		}
		finally {
			// You must stop the server when you're done
			aServer.stop();
		}
	}
}
