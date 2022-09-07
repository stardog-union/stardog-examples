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

import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.api.reasoning.ReasoningConnection;
import com.complexible.stardog.reasoning.ExpressionWriter;
import com.complexible.stardog.reasoning.Proof;
import com.complexible.stardog.reasoning.ProofWriter;
import com.stardog.stark.IRI;
import com.stardog.stark.OWL;
import com.stardog.stark.Values;
import com.stardog.stark.vocabs.RDF;
import com.stardog.stark.vocabs.RDFS;

/**
 * <p>Simple example to show how to use Stardog's explanation facilities.</p>
 *
 * @author Michael Grove
 * @version 6.0
 * @since 0.7.3
 */
public class ExplanationExample {

	private static final IRI x = Values.iri("urn:x");

	private static final IRI y = Values.iri("urn:y");

	private static final IRI z = Values.iri("urn:z");

	protected static final IRI A = Values.iri("urn:A");

	private static final IRI B = Values.iri("urn:B");

	private static final IRI C = Values.iri("urn:C");

	protected static final IRI p = Values.iri("urn:p");

	// Explanations in Stardog
	// -------------------
	// Here we will show a short example of how to use the [explanation features of Stardog](http://docs.stardog.com/#_explaining_reasoning_results)
	// to find out _why_ an inference was made.
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

		try {
			try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer().credentials("admin", "admin").connect()) {
				// Drop the example database if it exists and start fresh
				if (aAdminConnection.list().contains("reasoningTest")) {
					aAdminConnection.drop("reasoningTest");
				}

				// Create a disk-based database with default settings
				aAdminConnection.newDatabase("reasoningTest").create();
				// Open a `Connection` to the database we just created with reasoning turned on.
				// We'll use `as(...)` to give us a view of the parent connection that exposes the Stardog
				// [reasoning capabilities](http://docs.stardog.com/javadoc/snarl/com/complexible/stardog/api/reasoning/ReasoningConnection.html).
				try (ReasoningConnection aReasoningConnection = ConnectionConfiguration.to("reasoningTest")
				                                                                       .credentials("admin", "admin")
				                                                                       .reasoning(true)
				                                                                       .connect()
				                                                                       .as(ReasoningConnection.class)) {
					// Add a simple schema and couple instance triples to the database that we'll use for the example
					aReasoningConnection.begin();
					aReasoningConnection.add()
					                    .statement(p, RDFS.DOMAIN, B)
					                    .statement(B, RDFS.SUBCLASSOF, A)
					                    .statement(x, p, y)
					                    .statement(z, RDF.TYPE, B);
					aReasoningConnection.commit();

					// Now that we have data in the database, let's see the effects of using reasoning.  The above snippet
					// states `B(z)` and `subClassOf(B, A)`, so therefore `A(z)`.  Let's look for that without using reasoning.
					// We'll use the `Getter` interface to look up `:z a :A`, but we'll specify that no reasoning should
					// be performed
					boolean aExistsNoReasoning = aReasoningConnection.get()
					                                                 .reasoning(false)
					                                                 .subject(z)
					                                                 .predicate(RDF.TYPE)
					                                                 .object(A)
					                                                 .ask();

					// We will see that it's not there since we're not using reasoning
					System.out.println("Exists without reasoning? " + aExistsNoReasoning);

					// But if we do the same thing, but this time we don't disable reasoning, remember we said `RL` when
					// we created the `Connection`, we will see that the statement is inferred to exist.
					boolean aExistsReasoning = aReasoningConnection.get()
					                                               .subject(z)
					                                               .predicate(RDF.TYPE)
					                                               .object(A)
					                                               .ask();

					System.out.println("Exists with reasoning? " + aExistsReasoning);

					// Pretty cool!  Now lets find out _why_ that statement was inferred by the reasoner.  Stardog can
					// provide explanations for why an inference was made in the form of a
					// [Proof](http://docs.stardog.com/javadoc/snarl/com/complexible/stardog/reasoning/Proof.html).  A `Proof`
					// will list the steps the reasoner took to arrive at the conclusion that the triple was inferred.
					// To get the explanation, we simply ask the `Connection` to provide us with the `Proof` for the given
					// Statement

					Proof aExplanation = aReasoningConnection.explain(Values.statement(z, RDF.TYPE, A)).proof();

					// Now that we have the proof, we can print it out and we will see that the subClassOf axiom is
					// responsible for the inference.
					System.out.println("\nExplain inference: ");
					System.out.println(ProofWriter.toString(aExplanation));

					// Another statement the reasoner will infer is `(x, RDF.TYPE, A)`.  But to infer this, it needs both of
					// the axioms in the TBox, so the `Proof` to explain this is a bit more complicated.
					aExplanation = aReasoningConnection.explain(Values.statement(x, RDF.TYPE, A)).proof();

					System.out.println("Explain inference: ");
					System.out.println(ProofWriter.toString(aExplanation));

					// Now let's Introduce a simple inconsistency in our database.  We'll say that A and D are disjoint and
					// that `D(z)`.  We have already shown that `A(z)`, so `z` is both an `A` and a `D`, and that's the
					// inconsistency.
					aReasoningConnection.begin();
					aReasoningConnection.add()
					                    .statement(A, OWL.DISJOINTWITH, C)
					                    .statement(z, RDF.TYPE, C);
					aReasoningConnection.commit();

					// We can see now that our database is inconsistent
					System.out.println("Consistent? " + aReasoningConnection.isConsistent());

					// But maybe we didn't know that we were making an error, so we'll be wondering why the database is
					// now inconsistent.  Fortunately for us, Stardog can explain that as well.  Let's ask for the `Proof`
					// and find out why.
					Proof aProof = aReasoningConnection.explainInconsistency().proof();
					System.out.println("Explain inconsistency: ");
					System.out.println(ProofWriter.toString(aProof));

					System.out.println("Render only asserted statements: ");
					System.out.println(ExpressionWriter.toString(aProof.getStatements()));
				}
				finally {
					if (aAdminConnection.list().contains("reasoningTest")) {
						aAdminConnection.drop("reasoningTest");
					}
				}
			}
		}
		finally {
			aStardog.shutdown();
		}
	}
}
