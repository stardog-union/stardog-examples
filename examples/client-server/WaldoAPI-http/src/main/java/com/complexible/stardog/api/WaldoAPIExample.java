// Copyright (c) 2010 - 2012 -- Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.examples.api;

import java.io.File;

import org.openrdf.model.Literal;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rio.RDFFormat;

import com.complexible.common.iterations.Iteration;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.api.search.SearchConnection;
import com.complexible.stardog.api.search.SearchResult;
import com.complexible.stardog.api.search.SearchResults;
import com.complexible.stardog.api.search.Searcher;

/**
 * <p>Simple example </p>
 *
 * @author  Michael Grove
 * @since   0.6.5
 * @version 2.0
 */
public class WaldoAPIExample {

	public static void main(String[] args) throws Exception {
		// Specify the server URL
		String aServerUrl = "http://localhost:5820";

		// first create a temporary database to use
		AdminConnection dbms = AdminConnectionConfiguration.toServer(aServerUrl).credentials("admin", "admin").connect();

		// check whether there is no such database already, and if there is, drop it		
		if (dbms.list().contains("waldoTest")) {
			dbms.drop("waldoTest");
		}

		dbms.memory("waldoTest")
		    .searchable(true)
		    .create();

		dbms.close();

		// obtain a connection to the database
		Connection aConn = ConnectionConfiguration
			                   .to("waldoTest")		       // the name of the db to connect to
			                   .server(aServerUrl)
			                   .credentials("admin", "admin") // credentials to use while connecting
			                   .connect();				       // now open the connection

		// first things first, lets add some data into the index so that it can be queried and searched

		aConn.begin();
		aConn
			.add().io()
			.format(RDFFormat.RDFXML)
			.file(new File("data/catalog.rdf"));

		aConn.commit();

		// Lets try an example with the basic Waldo API

		// to start, we want to view this connection as a searchable connection, so we request a view of the connection
		// as a searchable connection
		SearchConnection aSearchConn = aConn.as(SearchConnection.class);

		// then we'll construct a searcher
		Searcher aSearch = aSearchConn.search()
			                   .limit(50) 			// as before we only want the top fifty results
			                   .query("mac")		// our search term
			                   .threshold(0.5);		// Since Waldo is implemented over lucene, we can also specify a min threshold for our results

		SearchResults aSearchResults = aSearch.search();

		// and now we can just iterate over the search results

		Iteration<SearchResult, QueryEvaluationException> resultIt = aSearchResults.iteration();

		System.out.println("\nAPI results: ");
		while (resultIt.hasNext()) {
			SearchResult aHit = resultIt.next();

			System.out.println(aHit.getHit() + " with a score of: " + aHit.getScore());
		}

		// don't forget to close your iteration!
		resultIt.close();

		// we can also re-use the searcher if we want to find the next set of results...

		aSearch.offset(50); // we already found the first fifty, so lets grab the next set

		aSearchResults = aSearch.search();

		// we can now check the next page of search results!

		// SPARQL syntax based on the LARQ syntax in Jena for doing the exact same thing directly in a query.

		String aQuery = "SELECT DISTINCT ?s ?score WHERE {\n" +
		                "\t?s ?p ?l.\n" +
		                "\t( ?l ?score ) <" + SearchConnection.MATCH_PREDICATE + "> ( 'mac' 0.5 50 ).\n" +
		                "}";

		SelectQuery query = aConn.select(aQuery);

		TupleQueryResult aResult = query.execute();

		System.out.println("Query results: ");
		while (aResult.hasNext()) {
			BindingSet result = aResult.next();

			System.out.println(result.getValue("s") + " with a score of: " + ((Literal) result.getValue("score")).doubleValue());
		}

		// always close your connections when you're done
		aConn.close();
	}
}
