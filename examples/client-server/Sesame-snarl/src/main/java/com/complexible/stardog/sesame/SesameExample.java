// Copyright (c) 2010 - 2012 -- Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.examples.sesame;

import java.io.FileInputStream;

import com.complexible.stardog.api.ConnectionConfiguration;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.sesame.StardogRepository;

/**
 * <p>A basic example of using Stardog via the Sesame API</p>
 *
 * @author  Michael Grove
 * @since   0.4
 * @version 2.0
 */
public class SesameExample {

	public static void main(String[] args) throws Exception {
		String aServerUrl = "snarl://localhost:5820";

		// first create a temporary database to use (if there is one already, drop it first)
		AdminConnection aAdminConnection = AdminConnectionConfiguration.toServer(aServerUrl).credentials("admin", "admin").connect();
		if (aAdminConnection.list().contains("testSesame")) {
			aAdminConnection.drop("testSesame");
		}
		aAdminConnection.createMemory("testSesame");
		aAdminConnection.close();

		// Create a Sesame Repository from a Stardog ConnectionConfiguration.  The configuration will be used
		// when creating new RepositoryConnections
		Repository aRepo = new StardogRepository(ConnectionConfiguration
			                                         .to("testSesame")
			                                         .server(aServerUrl)
			                                         .credentials("admin", "admin"));

		// init the repo
		aRepo.initialize();

		// now you can use it like a normal Sesame Repository
		RepositoryConnection aRepoConn = aRepo.getConnection();

		// always best to turn off auto commit
		aRepoConn.setAutoCommit(false);

		// add some data
		aRepoConn.add(new FileInputStream("data/sp2b_10k.n3"), "http://sesame.stardog.com/", RDFFormat.N3);

		// commit the data to stardog
		aRepoConn.commit();

		// we can send queries...
		// we currently only support SPARQL
		TupleQuery aQuery = aRepoConn.prepareTupleQuery(QueryLanguage.SPARQL, "select * where { ?s ?p ?o. filter(?s = <http://localhost/publications/articles/Journal1/1940/Article1>).}");

		// run the query
		TupleQueryResult aResults = aQuery.evaluate();

		// print the results in tabular format
		QueryResultIO.write(aResults, TextTableQueryResultWriter.FORMAT, System.out);

		// always close your query results!
		aResults.close();

		// always close your connections!
		aRepoConn.close();

		// make sure you shut down the repository as well as closing the repository connection as this is what releases the internal Stardog connection
		aRepo.shutDown();
	}
}
