// Copyright (c) 2010 - 2012 -- Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.examples.api;

import java.io.File;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rio.RDFFormat;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.IO;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.api.reasoning.ReasoningConnection;
import com.complexible.stardog.reasoning.api.ReasoningType;

/**
 * <p>A small example program illustrating how to access Stardog's reasoning capabilities.</p>
 *
 * @author  Michael Grove
 * @since   0.4.5
 * @version 2.0
 */
public class ReasoningExample {
    // Using Reasoning in Stardog
    // --------------------------
    // In this example we'll walk through a simple example using the SNARL API to access Stardog's
    // reasoning capabilities.
	public static void main(String[] args) throws Exception {
        // Specify the server URL
        // ----------------------
	String aServerUrl = "http://localhost:5820";

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
        AdminConnection aAdminConnection = AdminConnectionConfiguration.toServer(aServerUrl).credentials("admin", "admin").connect();

        // With our admin connection, we're able to see if the database for this example already exists, and
        // if it does, we want to drop it and re-create so that we can run the example from clean database.
		if (aAdminConnection.list().contains("reasoningExampleTest")) {
			aAdminConnection.drop("reasoningExampleTest");
		}

        // Convenience function for creating a non-persistent in-memory database with all the default settings.
		aAdminConnection.createMemory("reasoningExampleTest");

        // *ALWAYS* close your connections!
		aAdminConnection.close();

        // Using reasoning via SNARL
        // -------------------------
        // Now that we've created our database for the example, lets open a connection to it.  For that we use the
        // [SNARLConnectionConfiguration](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/SNARLConnectionConfiguration.html)
        // to configure and open a new connection to a database.
        //
        // We'll use the configuration to specify which database we want to connect to as well as our login information,
        // then we can obtain a new connection.  This is also where you specify the type of reasoning you would like the connection
        // to use.  Please note that reasoning is *per connection* there's no requirement to specify the type of
        // reasoning you want to use when you create a database.  You can use whatever type of reasoning suits your
        // current use-case by specifying it when you create a Connection.
		ReasoningConnection aReasoningConn = ConnectionConfiguration
			.to("reasoningExampleTest")
			.server(aServerUrl)
			.credentials("admin", "admin")
			.reasoning(ReasoningType.QL)
			.connect()
			.as(ReasoningConnection.class);

		// now obtain a non-reasoning connection to the database for comparison
		Connection aConn = ConnectionConfiguration
						.to("reasoningExampleTest")
						.server(aServerUrl)
						.credentials("admin", "admin")
						.connect();

		// now lets add lubm1 and the lubm ontology to the database.
		// we can use either the reasoning connection or the base connection for addition, results will be same
		aReasoningConn.begin();

		IO aAdder = aReasoningConn.add().io().format(RDFFormat.RDFXML);

		aAdder.file(new File("data/University0_0.owl"))
			  .file(new File("data/lubmSchema.owl"));

		aReasoningConn.commit();

		// so lets print out how many of some different types there are...
		System.out.println("The default results...");
		printCounts(aConn);

		// lets do the same thing with the reasoning connection
		// and print the same set of counts, but this time, with reasoning so we can see the difference

		System.out.println("\nResults with reasoning...");
		printCounts(aReasoningConn);

		// Closing the reasoning connection will close the base connection too
		aReasoningConn.close();
		aConn.close();
	}

	private static void printCounts(final Connection theConn) throws StardogException, QueryEvaluationException {
		URI PERSON = ValueFactoryImpl.getInstance().createURI("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Person");
		URI STUDENT = ValueFactoryImpl.getInstance().createURI("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Student");
		URI GRAD_STUDENT = ValueFactoryImpl.getInstance().createURI("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#GraduateStudent");
		URI PROFESSOR = ValueFactoryImpl.getInstance().createURI("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Professor");
		URI FULL_PROFESSOR = ValueFactoryImpl.getInstance().createURI("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#FullProfessor");

		SelectQuery aQuery = theConn.select("SELECT ?x WHERE {\n" +
									 "?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type\n" +
									 "}");

		aQuery.parameter("type", PERSON);
		TupleQueryResult aResult = aQuery.execute();
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

	private static int count(final TupleQueryResult theResult) throws QueryEvaluationException {
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
