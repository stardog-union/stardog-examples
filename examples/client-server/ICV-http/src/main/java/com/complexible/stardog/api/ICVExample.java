// Copyright (c) 2010 - 202 -- Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.examples.api;

import static com.complexible.common.openrdf.util.ExpressionFactory.some;
import static com.complexible.common.openrdf.util.ExpressionFactory.subClassOf;

import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;

import com.complexible.common.base.Pair;
import com.complexible.common.iterations.Iteration;
import com.complexible.common.openrdf.model.Graphs;
import com.complexible.stardog.ContextSets;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.icv.Constraint;
import com.complexible.stardog.icv.ConstraintFactory;
import com.complexible.stardog.icv.ConstraintViolation;
import com.complexible.stardog.icv.ICV;
import com.complexible.stardog.icv.api.ICVConnection;
import com.complexible.stardog.reasoning.api.ReasoningType;

/**
 * <p>Basic example of how to use Stardog's support for integrity constraints via the manual validation support offered by an ICConnection.</p>
 *
 * @author  Michael Grove
 * @since   0.7
 * @version 2.0
 */
public class ICVExample {

	public static void main(String[] args) throws Exception {
		// Specify the server URL
		String aServerUrl = "http://localhost:5820";

		// first create a temporary database to use (if there is already a database with such a name,
		// drop it first)
		AdminConnection aAdminConnection = AdminConnectionConfiguration.toServer(aServerUrl).credentials("admin", "admin").connect();

		if (aAdminConnection.list().contains("testICVExample")) {
			aAdminConnection.drop("testICVExample");
		}

		aAdminConnection.createMemory("testICVExample");
		aAdminConnection.close();

		// obtain a connection to the database
		Connection aConn = ConnectionConfiguration
			                   .to("testICVExample")			// the name of the db to connect to
			                   .server(aServerUrl)
			                   .reasoning(ReasoningType.QL)	// need reasoning for ICV
			                   .credentials("admin", "admin")  // credentials to use while connecting
			                   .connect();						// now open the connection

		// Lets set up our DB

		// first create the data
		URI Engine = ValueFactoryImpl.getInstance().createURI("urn:Engine");
		URI Product = ValueFactoryImpl.getInstance().createURI("urn:Product");
		URI Manufacturer = ValueFactoryImpl.getInstance().createURI("urn:Manufacturer");
		URI manufacturedBy = ValueFactoryImpl.getInstance().createURI("urn:manufacturedBy");
		URI e1 = ValueFactoryImpl.getInstance().createURI("urn:e1");
		URI m1 = ValueFactoryImpl.getInstance().createURI("urn:m1");

		Graph aGraph = Graphs.newGraph();

		aGraph.add(Engine, RDFS.SUBCLASSOF, Product);
		aGraph.add(e1, RDF.TYPE, Engine);
		aGraph.add(e1, manufacturedBy, m1);

		// now put it in the db
		aConn.begin();
		aConn.add().graph(aGraph);
		aConn.commit();

		// Let's define a constraint
		// we want to say that a product should be manufactured by a Manufacturer:
		Constraint aConstraint = ConstraintFactory.constraint(subClassOf(Product, some(manufacturedBy, Manufacturer)));

		// ok, we have a database, and a constraint, lets use ICV.  We'll start out by creating a validator from our connection
		ICVConnection aValidator = aConn.as(ICVConnection.class);

		// and adding the constraint, must do this in a transaction
		aValidator.begin();
		aValidator.addConstraint(aConstraint);
		aValidator.commit();

		// now we can check whether or not our data is valid
		// which it isnt, we're lacking the assertion that m1 is a Manufacturer...

		System.out.println("The data " + (aValidator.isValid(ContextSets.DEFAULT_ONLY) ? "is" : "is NOT") + " valid!");

		// darn, so our data is invalid.  But what's wrong with it?  It's easy to see in this case, but if we have a lot of
		// data, it may not be so clear what we're missing.  So lets ask!

		Iteration<ConstraintViolation<BindingSet>, StardogException> aViolationIter = aValidator.getViolationBindings(ContextSets.DEFAULT_ONLY);

		while (aViolationIter.hasNext()) {
			ConstraintViolation<BindingSet> aViolation = aViolationIter.next();

			Iteration<Resource, StardogException> aViolatingIndividuals = ICV.asIndividuals(aViolation.getViolations());

			System.out.println("Each of these individuals violated the constraint: " + aViolation.getConstraint());

			while (aViolatingIndividuals.hasNext()) {
				System.out.println(aViolatingIndividuals.next());
			}

			System.out.println("These are the missing/expected statements for the violation");
			Iteration<BindingSet, StardogException> aVioIter = aViolation.getViolations();

			while (aVioIter.hasNext()) {
				BindingSet aBindingSet = aVioIter.next();

				Pair<Set<Statement>, Set<Statement>> aExpl = ICV.getExplanation(aViolation.getConstraint(),
				                                                                aBindingSet);

				if (aExpl.first.isEmpty()) {
					System.out.println("Statements present in the database which are in violation:");
					for (Statement aStmt : aExpl.first) {
						System.out.println(aStmt);
					}
				}

				if (!aExpl.second.isEmpty()) {
					System.out.println("Statements missing that caused the violation:");
					for (Statement aStmt : aExpl.second) {
						System.out.println(aStmt);
					}
				}
			}

			// ALWAYS close Iterations when you're done with them!
			aViolatingIndividuals.close();
			aVioIter.close();
		}

		// ALWAYS close Iterations when you're done with them!
		aViolationIter.close();

		// always close your connections when you're done
		aConn.close();
	}
}
