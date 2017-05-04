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

import java.util.Arrays;

import com.complexible.common.openrdf.vocabulary.Vocabulary;
import com.complexible.common.rdf.model.Values;
import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
import com.complexible.stardog.ContextSets;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.db.DatabaseOptions;
import com.complexible.stardog.icv.api.ICVConnection;
import com.complexible.stardog.prov.ProvVocabulary;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.resultio.QueryResultIO;

/**
 * <p>Example code illustrating use of the built-in ontologies in Stardog, specifically for PROV and SKOS ontologies.</p>
 *
 * @author  Evren Sirin
 * @since   2.0
 * @version 4.0
 */
public class ProvSkosExample {
	// Very simple publication vocabulary used in this example
	private static class PublicationVocabulary extends Vocabulary {
		private static final PublicationVocabulary INSTANCE = new PublicationVocabulary();

		private PublicationVocabulary() {
			super("urn:example:publication:");
		}

		private IRI Book = term("Book");
		private IRI Fiction = term("Fiction");
		private IRI ScienceFiction = term("ScienceFiction");

		private IRI Author = term("Author");
	}

	// Define constants for vocabularies that we will use
	private static final PublicationVocabulary PUB = PublicationVocabulary.INSTANCE;
	private static final ProvVocabulary PROV = ProvVocabulary.INSTANCE;

	// Database Archetypes
	// ---
	// A Database Archteype is a built-in "type" of a Database, they include common axioms and constraints for
	// a particular type of data.  The default archetypes build into Stardog are currently SKOS and PROV.
	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

		try {
			String db = "exampleProvSkos";

			// Create an `AdminConnection` to Stardog
			try (AdminConnection dbms = AdminConnectionConfiguration.toEmbeddedServer()
			                                                        .credentials("admin", "admin")
			                                                        .connect()) {
				// Drop the example database if it exists so we can create it fresh
				if (dbms.list().contains(db)) {
					dbms.drop(db);
				}

				// Enable both `PROV` and `SKOS` ontologies for the current database
				dbms.memory(db).set(DatabaseOptions.ARCHETYPES, Arrays.asList("skos", "prov")).create();
			}

			// Obtain a connection to the database

			try (Connection aConn = ConnectionConfiguration
				                        .to(db)
				                        .credentials("admin", "admin")
				                        .reasoning(true)
				                        .connect()) {
				// First create some SKOS data and introduce an error (related and transitive broader relations should be disjoint)
				aConn.begin();
				aConn.add()
				     .statement(PUB.Book, RDF.TYPE, SKOS.CONCEPT)
				     .statement(PUB.Fiction, RDF.TYPE, SKOS.CONCEPT)
				     .statement(PUB.ScienceFiction, RDF.TYPE, SKOS.CONCEPT)
				     .statement(PUB.Book, SKOS.NARROWER, PUB.Fiction)
				     .statement(PUB.ScienceFiction, SKOS.BROADER, PUB.Fiction)
				     .statement(PUB.ScienceFiction, SKOS.RELATED, PUB.Book);
				aConn.commit();

				// Let's validate the SKOS data we just created. Note that SKOS inferences and constraints are automatically
				// included in the database because it uses the SKOS archetype.  So there's no extra work we have to do
				// we just insert our SKOS data and we're good to go.
				ICVConnection aValidator = aConn.as(ICVConnection.class);

				// For simplicity, we will just print that the data is not valid (explanations can be retrieved separately)
				System.out.println("The data " + (aValidator.isValid(ContextSets.DEFAULT_ONLY)
				                                  ? "is"
				                                  : "is NOT") + " valid!");

				// Let's remove the problematic triple and add some PROV data
				IRI The_War_of_the_Worlds = Values.iri("http://dbpedia.org/resource/The_War_of_the_Worlds");
				IRI H_G_Wells = Values.iri("http://dbpedia.org/resource/H._G._Wells");
				Resource attr = Values.bnode();
				aConn.begin();
				aConn.remove()
				     .statements(PUB.ScienceFiction, SKOS.RELATED, PUB.Book);
				aConn.add()
				     .statement(The_War_of_the_Worlds, RDF.TYPE, PROV.Entity)
				     .statement(The_War_of_the_Worlds, DC.SUBJECT, PUB.ScienceFiction)
				     .statement(The_War_of_the_Worlds, PROV.qualifiedAttribution, attr)
				     .statement(attr, RDF.TYPE, PROV.Attribution)
				     .statement(attr, PROV.agent, H_G_Wells)
				     .statement(attr, PROV.hadRole, PUB.Author);
				aConn.commit();

				// Now that the problematic triples is removed, the data will be valid
				System.out.println("The data " + (aValidator.isValid(ContextSets.DEFAULT_ONLY)
				                                  ? "is"
				                                  : "is NOT") + " valid!");

				// Finlaly run a query that will retrieve all fiction books and their authors.
				// This query uses both PROV and SKOS inferences that are automatically included with the archetypes.
				// Using `Book -[skos:narrower]-> Fiction <-[skos:broader]- ScienceFiction` triples, we infer `ScienceFiction -[skos:broaderTransitive]-> Book`
				// Using `The_War_of_the_Worlds -[prov:qualifiedAttribution]-> :_attr -[prov:agent]-> H_G_Wells`, we infer `The_War_of_the_Worlds -[prov:wasAttributedTo]-> H_G_Wells`
				// Also note that we don't need to define prefixes for skos and prov which are automatically registered
				// to the database when the archetypes are loaded
				SelectQuery aQuery = aConn.select(
					"PREFIX pub: <" + PUB.uri() + ">" +
					"PREFIX dc: <" + DC.NAMESPACE + ">" +
					"SELECT * WHERE {\n" +
					"  ?book dc:subject/skos:broaderTransitive pub:Book;\n" +
					"        prov:wasAttributedTo ?author\n" +
					"}");

				// Print the query results
				QueryResultIO.writeTuple(aQuery.execute(), TextTableQueryResultWriter.FORMAT, System.out);
			}
		}
		finally {
			aStardog.shutdown();
		}
	}
}
