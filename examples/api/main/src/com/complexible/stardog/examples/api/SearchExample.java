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

package com.complexible.stardog.examples.api;

import java.nio.file.Paths;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.api.search.SearchConnection;
import com.complexible.stardog.examples.TestServer;
import com.complexible.stardog.search.SearchOptions;
import com.stardog.stark.Literal;
import com.stardog.stark.io.RDFFormats;
import com.stardog.stark.query.BindingSet;
import com.stardog.stark.query.SelectQueryResult;

/**
 * A short example illustrating the use of the <a href="https://docs.stardog.com/query-stardog/full-text-search">full text search capabilities</a> in Stardog.
 */
public class SearchExample {
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the http server.
		TestServer aStardog = new TestServer();

		try {
			// Open an `AdminConnection` to Stardog so that we can setup the database for the example
			try (AdminConnection dbms = AdminConnectionConfiguration.toServer(aStardog.getServerURL())
			                                                        .credentials("admin", "admin")
			                                                        .connect()) {
				// If our example database exists, drop it and create it anew
				if (dbms.list().contains("waldoTest")) {
					dbms.drop("waldoTest");
				}

				// Create a disk database with full-text index
				dbms.disk("waldoTest")
				    .set(SearchOptions.SEARCHABLE, true)
				    .create();

				// Obtain a `Connection` to the database we just created
				try (Connection aConn = ConnectionConfiguration
					                        .to("waldoTest")
					                        .server(aStardog.getServerURL())
					                        .credentials("admin", "admin")
					                        .connect()) {
					// To start, lets add some data into the database so that it can be queried and searched
					aConn.begin();
					aConn
						.add().io()
						.format(RDFFormats.RDFXML)
						.file(Paths.get("data/catalog.rdf"));

					aConn.commit();

					// The Stardog full-text search index no different than the RDF index, which means you can query it
					// via SPARQL, even combining your search over the full-text index with BGPs which query your RDF
					// letting you query *both* indexes at the same time.  The SPARQL syntax is based on the LARQ
					// syntax in Jena.  Here you will see the SPARQL query that is equivalent to the search we just
					// did via `Searcher`, which we can see when we print the results.
					String aQuery = "SELECT DISTINCT ?s ?score WHERE {\n" +
					                "\t?s ?p ?l.\n" +
					                "\t( ?l ?score ) <" + SearchConnection.MATCH_PREDICATE + "> ( 'mac' 0.5 50 ).\n" +
					                "}";

					SelectQuery query = aConn.select(aQuery);

					try (SelectQueryResult aResult = query.execute()) {
						System.out.println("Query results: ");
						while (aResult.hasNext()) {
							BindingSet result = aResult.next();
							result.value("s").ifPresent(s -> System.out.println(s + result.literal("score").map(score -> " with a score of: " + Literal.doubleValue(score)).orElse("")));
						}
					}
				}
				finally {
					if (dbms.list().contains("waldoTest")) {
						dbms.drop("waldoTest");
					}
				}
			}
		}
		finally {
			aStardog.shutdown();
		}
	}
}
