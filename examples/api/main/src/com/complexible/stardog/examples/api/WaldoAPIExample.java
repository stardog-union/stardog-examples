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

import com.complexible.common.base.CloseableIterator;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.api.search.SearchConnection;
import com.complexible.stardog.api.search.SearchResult;
import com.complexible.stardog.api.search.SearchResults;
import com.complexible.stardog.api.search.Searcher;
import com.complexible.stardog.search.SearchOptions;
import com.stardog.stark.Literal;
import com.stardog.stark.io.RDFFormats;
import com.stardog.stark.query.BindingSet;
import com.stardog.stark.query.SelectQueryResult;

/**
 * <p>Simple example </p>
 *
 * @author Michael Grove
 * @version 6.0
 * @since 0.6.5
 */
public class WaldoAPIExample {

	// Using the Waldo Search API
	// --------------
	// A short example illustrating the use of the [full text search capabilities](http://docs.stardog.com/#_full_text_search) in Stardog
	// via the SNARL API.
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

		try {
			// Open an `AdminConnection` to Stardog so that we can setup the database for the example
			try (AdminConnection dbms = AdminConnectionConfiguration.toEmbeddedServer()
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
					                        .credentials("admin", "admin")
					                        .connect()) {
					// To start, lets add some data into the database so that it can be queried and searched
					aConn.begin();
					aConn
						.add().io()
						.format(RDFFormats.RDFXML)
						.file(Paths.get("data/catalog.rdf"));

					aConn.commit();

					// Lets try an example with the basic Waldo API
					// We want to view this connection as a [searchable connection](http://docs.stardog.com/javadoc/snarl/com/complexible/stardog/api/search/SearchConnection.html),
					// so we request a view of the `Connection` as a `SearchConnection`
					SearchConnection aSearchConn = aConn.as(SearchConnection.class);

					// With that done, let's create a [Searcher](http://docs.stardog.com/javadoc/snarl/com/complexible/stardog/api/search/Searcher.html)
					// that we can use to run some full text searches over the database.
					// Here we will specify that we only want results over a score of `0.5`, and no more than `50` results
					// for things that match the search term `mac`.  Stardog's full text search is backed by [Lucene](http://lucene.apache.org)
					// so you can use the full Lucene search syntax in your queries.
					Searcher aSearch = aSearchConn.search()
					                              .limit(50)
					                              .query("mac")
					                              .threshold(0.5);

					// We can run the search and then iterate over the results
					SearchResults aSearchResults = aSearch.search();

					try (CloseableIterator<SearchResult> resultIt = aSearchResults.iterator()) {
						System.out.println("\nAPI results: ");
						while (resultIt.hasNext()) {
							SearchResult aHit = resultIt.next();

							System.out.println(aHit.getHit() + " with a score of: " + aHit.getScore());
						}
					}

					// The `Searcher` can be re-used if we want to find the next set of results.  We already found the
					// first fifty, so lets grab the next page.
					aSearch.offset(50);

					aSearchResults = aSearch.search();

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
