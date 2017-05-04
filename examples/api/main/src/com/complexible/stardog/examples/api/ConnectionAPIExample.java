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

import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;

import com.complexible.common.openrdf.model.ModelIO;
import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.rdf.model.Values;
import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.Getter;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.rio.RDFFormat;

/**
 * <p>Example code illustrating use of the Stardog Connection API</p>
 *
 * @author Michael Grove
 * @version 5.0
 * @since 0.4
 */
public class ConnectionAPIExample {

	// Using the SNARL API
	// -------------------
	// In this example we'll walk through the basic usage of the Stardog Native API for the RDF Language (SNARL)
	// API, which is the preferred way to interact with Stardog.  This will show how to use both the administrative
	// and client APIs to perform some basic operations.
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

		try {
			// Using AdminConnection
			// ---------------------
			// Now that the server is running, we want to create a connection to the DBMS itself so we can perform
			// some administrative actions, namely, creating a new database to use for the purpose of this example.
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
				// if it does, we want to drop it and re-create so that we can run the example from a clean database.
				if (aAdminConnection.list().contains("testConnectionAPI")) {
					aAdminConnection.drop("testConnectionAPI");
				}

				// Convenience function for creating a non-persistent in-memory database with all the default settings.
				aAdminConnection.createMemory("testConnectionAPI");
			}

			// Using the SNARL API
			// -------------------
			// Now that we've created our database for the example, let's open a connection to it.  For that we use the
			// [ConnectionConfiguration](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/ConnectionConfiguration.html)
			// to configure and open a new connection to a database.
			//
			// We'll use the configuration to specify which database we want to connect to as well as our login information,
			// then we can obtain a new connection.

			try (Connection aConn = ConnectionConfiguration
				                        .to("testConnectionAPI")
				                        .credentials("admin", "admin")
				                        .connect()) {
				// All changes to a database *must* be performed within a transaction.  We want to add some data to the database
				// so we can begin firing off some queries, so first, we'll start a new transaction.
				aConn.begin();

				// The SNARL API provides fluent objects for adding & removing data from a database.  Here we'll use the
				// [Adder](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/Adder.html) to read in an N3 file
				// from disk containing the 10k triples SP2B dataset.  Actually, for RDF data coming from a stream or from
				// disk, we'll use the helper class [IO](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/IO.html)
				// for this task.  `IO` will automatically close the stream once the data has been read.
				aConn.add().io()
				     .format(RDFFormat.N3)
				     .stream(new FileInputStream("data/sp2b_10k.n3"));

				// You're not restricted to adding, or removing, data from a file.  You can create `Model` objects
				// containing information you want to add or remove from the database and make the modification wit
				// that graph.  Here we'll create a new Model and add a statement that we want added to our database.
				Model aGraph = Models2.newModel(Values.statement(Values.iri("urn:subj"),
				                                                 Values.iri("urn:pred"),
				                                                 Values.iri("urn:obj")));

				Resource aContext = Values.iri("urn:test:context");

				// With our newly created `Graph`, we can easily add that to the database as well.  You can also
				// easily specify the context the data should be added to.  This will insert all of the statements
				// in the `Graph` into the given context.
				aConn.add().graph(aGraph, aContext);

				// Now that we're done adding data to the database, we can go ahead and commit the transaction.
				aConn.commit();

				// Removing data from a database is just as easy.  Again, we need to start a transaction before making any changes.
				aConn.begin();

				// Now we'll use the [Remover](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/Remover.html) to
				// remove some data from the database.  `Remover` has a very similar API to `Adder`, so this code should look
				// somewhat familiar.  It has many of the same methods as `Adder`, the only difference is that they'll cause
				// the triples to be removed instead of added.
				aConn.remove().io()
				     .format(RDFFormat.N3)
				     .file(Paths.get("data/remove_data.nt"));

				// Lastly, we'll commit the changes.
				aConn.commit();

				// A SNARL connection provides [parameterized queries](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/Query.html)
				// which you can use to easily build and execute SPARQL queries against the database.  First, let's create a simple
				// query that will get all of the statements in the database.
				SelectQuery aQuery = aConn.select("select * where { ?s ?p ?o }");

				// But getting *all* the statements is kind of silly, so let's actually specify a limit, we only want 10 results.
				aQuery.limit(10);

				// We can go ahead and execute this query which will give us a result set.  Once we have our result set, we can do
				// something interesting with the results.
				TupleQueryResult aResult = aQuery.execute();

				try {
					System.out.println("The first ten results...");

					QueryResultIO.writeTuple(aResult, TextTableQueryResultWriter.FORMAT, System.out);
				}
				finally {
					// *Always* close your result sets, they hold resources which need to be released.
					aResult.close();
				}

				// `Query` objects are easily parameterized; so we can bind the "s" variable in the previous query with a specific value.
				// Queries should be managed via the parameterized methods, rather than created by concatenating strings together,
				// because that is not only more readable, it helps avoid SPARQL injection attacks.
				IRI aURI = Values.iri("http://localhost/publications/articles/Journal1/1940/Article1");
				aQuery.parameter("s", aURI);

				// Now that we've bound 's' to a specific value, we're not going to pull down the entire database with our query
				// so we can go head and remove the limit and get all the results.
				aQuery.limit(SelectQuery.NO_LIMIT);

				// We've made our modifications, so we can re-run the query to get a new result set and see the difference in the results.
				aResult = aQuery.execute();

				System.out.println("\nNow a particular slice...");

				try {
					QueryResultIO.writeTuple(aResult, TextTableQueryResultWriter.FORMAT, System.out);
				}
				finally {
					// Again, *close* your result sets.
					aResult.close();
				}

				// The previous query was just getting the statements in which the value of `aURI` is the subject.  We can get the
				// same results just as easily via the [Getter](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/Getter.html)
				// interface.  `Getter` is designed to make it easy to list statements matching specific criteria; it's analogous to
				// `listStatements` or `match` in the Jena & Sesame APIs respectively.
				//
				// So here we'll create a `Getter` to obtain the list of statements with `aURI` as the subject.  If we print those
				// out we'll see that we've retrieved the same results as the query we just ran.
				System.out.println("\nOr you can use a getter to do the same thing...");

				aConn.get().subject(aURI)
				     .statements()
				     .forEach(System.out::println);

				// `Getter` objects are parameterizable just like `Query`, so you can easily modify and re-use them to change
				// what slice of the database you'll retrieve.
				Getter aGetter = aConn.get();

				// We created a new `Getter`, if we iterated over its results now, we'd iterate over the whole database; not ideal.  So
				// we will bind the predicate to `rdf:type` and now if we call any of the iteration methods on the `Getter` we'd only
				// pull back statements whose predicate is `rdf:type`
				aGetter.predicate(RDF.TYPE);

				// We can also bind the subject and get a specific type statement, in this case, we'll get all the type triples
				// for *this* individual.  In our example, that'll be a single triple.
				aGetter.subject(aURI);

				System.out.println("\nJust a single statement now...");

				aGetter.statements()
				       .forEach(System.out::println);

				// `Getter` objects are stateful, so we can remove the filter on the predicate position by setting it back to null.
				aGetter.predicate(null);

				// Subject is still bound to the value of `aURI` so we can use the `graph` method of `Getter` to get a graph of all
				// the triples where `aURI` is the subject, effectively performing a basic describe query.
				aGraph = aGetter.statements().collect(Models2.toModel());

				System.out.println("\nFinally, the same results as earlier, but as a graph...");

				ModelIO.write(aGraph, new OutputStreamWriter(System.out), RDFFormat.TURTLE);
			}
		}
		finally {
			aStardog.shutdown();
		}
	}
}
