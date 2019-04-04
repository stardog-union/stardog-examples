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
import java.util.Set;

import javax.annotation.Nonnull;

import com.complexible.common.base.CloseableIterator;
import com.complexible.common.rdf.StatementIterator;
import com.complexible.stardog.ContextSets;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.icv.Constraint;
import com.complexible.stardog.icv.ConstraintFactory;
import com.complexible.stardog.icv.api.ICVConnection;
import com.complexible.stardog.icv.shacl.SHACL;
import com.complexible.stardog.reasoning.Proof;
import com.complexible.stardog.reasoning.ProofWriter;
import com.google.common.collect.ImmutableSet;
import com.stardog.stark.IRI;
import com.stardog.stark.Statement;
import com.stardog.stark.Values;
import com.stardog.stark.io.AbstractRDFHandler;
import com.stardog.stark.io.RDFFormat;
import com.stardog.stark.io.RDFFormats;
import com.stardog.stark.io.RDFWriters;
import com.stardog.stark.vocabs.RDF;
import com.stardog.stark.vocabs.RDFS;
import org.apache.http.config.ConnectionConfig;

import static com.stardog.stark.Axioms.some;
import static com.stardog.stark.Axioms.subClassOf;
import static com.stardog.stark.Values.statement;

/**
 * <p></p>
 *
 * @author Evren Sirin
 */
public class SHACLExample {

	// Basic example of how to use Stardog's SHACL support for for data validation
	public static void main(String[] args) throws Exception {
		String aDb = "testSHACL";
		
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

		// Open an `AdminConnection` to Stardog so we can set up our database for the example
		try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
		                                                                    .credentials("admin", "admin")
		                                                                    .connect()) {
			// If the example database exists, drop it, so we can create it fresh
			if (aAdminConnection.list().contains(aDb)) {
				aAdminConnection.drop(aDb);
			}

			// create a new database with some data
			ConnectionConfiguration aConfig = aAdminConnection.newDatabase(aDb)
			                                                  .create(Paths.get("data/music_beatles.ttl"));

			// connect to the database and convert the connection to an ICVConnection
			try (ICVConnection aConn = aConfig.connect().as(ICVConnection.class)) {
				// add the SHACL constraints to the database
				aConn.addConstraints()
				     .format(RDFFormats.TURTLE)
				     .file(Paths.get("data/music_shacl.ttl"));

				// print if data is valid
				System.out.printf("Is data valid: %s%n%n", aConn.isValid());

				// get the SHACL validation report
				StatementIterator aFullReport = aConn.reporter().report();

				// collect the report contents into a set and print it in pretty turtle format
				System.out.println("Full validation report:");
				RDFWriters.write(System.out, RDFFormats.PRETTY_TURTLE, aFullReport.toGraph(), aConn.namespaces());

				// namespace for the data
				String aNS = "http://stardog.com/tutorial/";

				// validate one specific shape and return only one validation result
				String aShape = "AlbumShape";
				try (StatementIterator aReport = aConn.reporter()
				                                      .shape(Values.iri(aNS, aShape))
				                                      .countLimit(1)
				                                      .report()) {
					System.out.printf("Number of triples in the validation report for the shape %s: %d%n%n",
					                  aShape, CloseableIterator.size(aReport));
				}

				// validate one specific node and pass the validation results to a streaming handler
				String aNode = "Imagine";
				System.out.printf("Shapes violated by the node %s: %n", aNode);
				aConn.reporter()
				     .node(Values.iri(aNS, aNode))
				     .report(new AbstractRDFHandler() {
					     @Override
					     public void handle(@Nonnull final Statement theStatement) {
						     if (theStatement.predicate().equals(SHACL.sourceShape)) {
							     System.out.println(((IRI) theStatement.object()).localName());
						     }
					     }
				     });
			}
			finally {
				// drop the database
				aAdminConnection.drop(aDb);
			}
		}
		finally {
			aStardog.shutdown();
		}
	}
}
