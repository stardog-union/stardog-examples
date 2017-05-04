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

package com.complexible.stardog.foaf;

import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.db.DatabaseOptions;
import com.complexible.stardog.icv.api.ICVConnection;
import com.complexible.stardog.reasoning.Proof;
import com.complexible.stardog.reasoning.ProofType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.IRI;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Tests showing how the user-defined FOAF archetype works.
 *
 * @author Evren Sirin
 */
public class FOAFTest {
	private static final String DB = "testFOAF";

	private static final String NS = "urn:example:";

	private static final IRI alice = Values.iri(NS, "alice");
	private static final IRI bob = Values.iri(NS, "bob");
	private static final IRI homepage = Values.iri(NS, "homepage");

	@BeforeClass
	public static void beforeClass() throws Exception {
		try (AdminConnection aAdminConn = AdminConnectionConfiguration.toEmbeddedServer()
		                                                              .credentials("admin", "admin")
		                                                              .connect()) {
			if (aAdminConn.list().contains(DB)) {
				aAdminConn.drop(DB);
			}

			try (Connection aConn = aAdminConn.memory(DB)
			                                  .set(DatabaseOptions.ARCHETYPES, ImmutableList.of("foaf"))
			                                  .create()
			                                  .connect()) {
				aConn.begin();
				aConn.add()
				     .statement(alice, FOAF.KNOWS, bob)
				     .statement(alice, FOAF.HOMEPAGE, homepage)
				     .statement(bob, FOAF.IS_PRIMARY_TOPIC_OF, homepage);
				aConn.commit();
			}
		}
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void testInference() throws Exception {
		try (Connection aConn = ConnectionConfiguration.to(DB)
		                                               .credentials("admin", "admin")
		                                               .reasoning(true)
		                                               .connect()) {
			// test domain inference
			assertTrue(aConn.get().subject(alice).predicate(RDF.TYPE).object(FOAF.PERSON).ask());

			// test range and subClassOf inference
			assertTrue(aConn.get().subject(bob).predicate(RDF.TYPE).object(FOAF.AGENT).ask());

			// test inverse inference
			assertTrue(aConn.get().subject(homepage).predicate(FOAF.PRIMARY_TOPIC).object(alice).ask());

			// test subPropertyOf inferences
			assertTrue(aConn.get().subject(alice).predicate(FOAF.IS_PRIMARY_TOPIC_OF).object(homepage).ask());
			assertTrue(aConn.get().subject(alice).predicate(FOAF.PAGE).object(homepage).ask());
		}
	}

	@Test
	public void testValidation() throws Exception {

		try (ICVConnection aConn = ConnectionConfiguration.to(DB)
		                                                  .credentials("admin", "admin")
		                                                  .reasoning(true)
		                                                  .connect()
		                                                  .as(ICVConnection.class)) {
			assertFalse(aConn.isValid());

			Proof aProof = aConn.explain().proof();
			assertEquals(ProofType.VIOLATED, aProof.getType());
			assertEquals(Values.statement(FOAF.IS_PRIMARY_TOPIC_OF, RDF.TYPE, OWL.INVERSEFUNCTIONALPROPERTY),
			             Iterables.getOnlyElement(aProof.getExpression().model()));
		}
	}
}
