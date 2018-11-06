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

import java.nio.file.Paths;

import com.complexible.stardog.Stardog;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.api.reasoning.ReasoningConnection;
import com.stardog.stark.IRI;
import com.stardog.stark.Values;
import com.stardog.stark.io.RDFFormats;
import com.stardog.stark.query.SelectQueryResult;

/**
 * <p>A small example program illustrating how to access Stardog's reasoning capabilities.</p>
 *
 * @author Michael Grove
 * @version 4.0
 * @since 0.4.5
 */
public class ReasoningExample {

	// Using Reasoning in Stardog
	// --------------------------
	// In this example we'll walk through a simple example using the SNARL API to access Stardog's
	// reasoning capabilities.
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

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
			try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
			                                                                    .credentials("admin", "admin")
			                                                                    .connect()) {
				// With our admin connection, we're able to see if the database for this example already exists, and
				// if it does, we want to drop it and re-create so that we can run the example from clean database.
				if (aAdminConnection.list().contains("reasoningExampleTest")) {
					aAdminConnection.drop("reasoningExampleTest");
				}

				// create a disk database
				aAdminConnection.disk("reasoningExampleTest").create();


				// Using reasoning via SNARL
				// -------------------------
				// Now that we've created our database for the example, lets open a connection to it.  For that we use the
				// [ConnectionConfiguration](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/ConnectionConfiguration.html)
				// to configure and open a new connection to a database.
				//
				// We'll use the configuration to specify which database we want to connect to as well as our login information,
				// then we can obtain a new connection.  This is also where you specify whether you would like the connection
				// to use reasoning.  Please note that reasoning is *per connection* there's no requirement to specify the type of
				// reasoning you want to use when you create a database.

				try (ReasoningConnection aReasoningConn = ConnectionConfiguration
					                                          .to("reasoningExampleTest")
					                                          .credentials("admin", "admin")
					                                          .reasoning(true)
					                                          .connect()
					                                          .as(ReasoningConnection.class);
				     // and obtain a non-reasoning connection to the database for comparison
				     Connection aConn = ConnectionConfiguration
					                        .to("reasoningExampleTest")
					                        .credentials("admin", "admin")
					                        .connect()) {

					// Now lets add lubm1 and the lubm ontology to the database.
					// We can use either the reasoning connection or the base connection for addition, results will be same
					aReasoningConn.begin();

					aReasoningConn.add().io()
					              .format(RDFFormats.RDFXML)
					              .file(Paths.get("data/University0_0.owl"))
					              .file(Paths.get("data/lubmSchema.owl"));

					aReasoningConn.commit();

					// So let's print out how many of some different types there are...
					System.out.println("The default results...");
					printCounts(aConn);

					// Let's do the same thing with the reasoning connection
					// and print the same set of counts, but this time, with reasoning so we can see the difference
					System.out.println("\nResults with reasoning...");
					printCounts(aReasoningConn);
				}
				finally {
					if (aAdminConnection.list().contains("reasoningExampleTest")) {
						aAdminConnection.drop("reasoningExampleTest");
					}
				}
			}
		}
		finally {
			aStardog.shutdown();
		}
	}

	private static void printCounts(final Connection theConn) throws StardogException {
		IRI PERSON = Values.iri("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Person");
		IRI STUDENT = Values.iri("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Student");
		IRI GRAD_STUDENT = Values.iri("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#GraduateStudent");
		IRI PROFESSOR = Values.iri("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Professor");
		IRI FULL_PROFESSOR = Values.iri("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#FullProfessor");

		SelectQuery aQuery = theConn.select("SELECT ?x WHERE {\n" +
		                                    "?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type\n" +
		                                    "}");

		aQuery.parameter("type", PERSON);
		SelectQueryResult aResult = aQuery.execute();
		System.out.println("Number of Persons: " + count(aResult));

		aQuery.parameter("type", STUDENT);
		aResult = aQuery.execute();
		System.out.println("Number of Students: " + count(aResult));

		aQuery.parameter("type", GRAD_STUDENT);
		aResult = aQuery.execute();
		System.out.println("Number of Grad Students: " + count(aResult));

		aQuery.parameter("type", PROFESSOR);
		aResult = aQuery.execute();
		System.out.println("Number of Professors: " + count(aResult));

		aQuery.parameter("type", FULL_PROFESSOR);
		aResult = aQuery.execute();
		System.out.println("Number of Full Professors: " + count(aResult));
	}

	private static int count(final SelectQueryResult theResult) {
		try {
			int count = 0;
			while (theResult.hasNext()) {
				count++;
				theResult.next();
			}

			return count;
		}
		finally {
			theResult.close();
		}
	}
}
