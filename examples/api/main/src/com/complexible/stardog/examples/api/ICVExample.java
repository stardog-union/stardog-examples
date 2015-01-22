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
package com.complexible.stardog.examples.api;

import static com.complexible.common.openrdf.util.ExpressionFactory.some;
import static com.complexible.common.openrdf.util.ExpressionFactory.subClassOf;

import java.util.Set;

import com.complexible.stardog.protocols.snarl.SNARLProtocolConstants;
import com.complexible.stardog.reasoning.Proof;
import com.complexible.stardog.reasoning.ProofWriter;
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
import com.complexible.common.protocols.server.Server;
import com.complexible.stardog.ContextSets;
import com.complexible.stardog.Stardog;
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

/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   0.7
 * @version 2.0
 */
public class ICVExample {

	// Using the Stardog ICV API
	// ---
	// Basic example of how to use Stardog's support for [integrity constraints](http://docs.stardog.com/icv)
	// via the manual validation support offered by an ICVConnection.
	public static void main(String[] args) throws Exception {
		// As always, we need to create and start a Stardog server to use in our example

		Server aServer = Stardog
			                 .buildServer()
			                 .bind(SNARLProtocolConstants.EMBEDDED_ADDRESS)
			                 .start();

		try {
			// Open an `AdminConnection` to Stardog so we can set up our database for the example
			AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
			                                                               .credentials("admin", "admin")
			                                                               .connect();

			try {
				// If the example database exists, drop it, so we can create it fresh
				if (aAdminConnection.list().contains("testICVExample")) {
					aAdminConnection.drop("testICVExample");
				}

				aAdminConnection.createMemory("testICVExample");
			}
			finally {
				aAdminConnection.close();
			}

			// Obtain a connection to the database
			Connection aConn = ConnectionConfiguration
				                   .to("testICVExample")
				                   .reasoning(true)
				                   .credentials("admin", "admin")
				                   .connect();

			try {
				URI Engine = ValueFactoryImpl.getInstance().createURI("urn:Engine");
				URI Product = ValueFactoryImpl.getInstance().createURI("urn:Product");
				URI Manufacturer = ValueFactoryImpl.getInstance().createURI("urn:Manufacturer");
				URI manufacturedBy = ValueFactoryImpl.getInstance().createURI("urn:manufacturedBy");
				URI e1 = ValueFactoryImpl.getInstance().createURI("urn:e1");
				URI m1 = ValueFactoryImpl.getInstance().createURI("urn:m1");

				Graph aGraph = Graphs.newGraph();

				// Let's create a very simple piece of data, complete with a bit of schema information, to use
				aGraph.add(Engine, RDFS.SUBCLASSOF, Product);
				aGraph.add(e1, RDF.TYPE, Engine);
				aGraph.add(e1, manufacturedBy, m1);

				// We'll insert that into the database
				aConn.begin();
				aConn.add().graph(aGraph);
				aConn.commit();

				// Now let's define a constraint; we want to say that a product must be manufactured by a Manufacturer:
				Constraint aConstraint = ConstraintFactory.constraint(subClassOf(Product, some(manufacturedBy, Manufacturer)));

				// Grab an [ICVConnection](http://docs.stardog.com/java/snarl/com/complexible/stardog/icv/api/ICVConnection.html)
				// so we can add our constraint to the database and start using ICV.
				ICVConnection aValidator = aConn.as(ICVConnection.class);

				// Add the constraint we just created to our database
				aValidator.addConstraint(aConstraint);

				// So we can check whether or not our data is valid,
				// which it isn't; we're lacking the assertion that m1 is a Manufacturer.
				System.out.println("The data " + (aValidator.isValid(ContextSets.DEFAULT_ONLY) ? "is" : "is NOT") + " valid!");

				// Ok, so our data is invalid.  But what's wrong with it?  It's easy to see in this case, but if we have
				// a lot of data, it may not be so clear what we're missing.  So lets ask!  Like with reasoning, we
				// can get a [Proof](http://docs.stardog.com/java/snarl/com/complexible/stardog/reasoning/Proof.html) for
				// integrity constraint violations.
				Proof aProof = aValidator.explain(aConstraint).proof();
				System.out.println(ProofWriter.toString(aProof));
			}
			finally {
				// Always close your connections when you're done
				aConn.close();
			}
		}
		finally {
			// You MUST stop the server if you've started it!
			aServer.stop();
		}
	}
}
