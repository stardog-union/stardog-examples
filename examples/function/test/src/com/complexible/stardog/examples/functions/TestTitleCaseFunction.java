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

package com.complexible.stardog.examples.functions;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 5.0
 */
public class TestTitleCaseFunction {
	private static final String DB = "testTitleCase";

	private static Stardog STARDOG;

	@BeforeClass
	public static void beforeClass() throws Exception {
		STARDOG = Stardog.builder().create();

		try (AdminConnection aConn = AdminConnectionConfiguration.toEmbeddedServer()
		                                                         .credentials("admin", "admin")
		                                                         .connect()) {
			if (aConn.list().contains(DB)) {
				aConn.drop(DB);
			}

			aConn.createMemory(DB);
		}
	}

	@AfterClass
	public static void afterClass() {
		STARDOG.shutdown();
	}

	@Test
	public void testTitleCase() throws Exception {

		try (Connection aConn = ConnectionConfiguration.to(DB)
		                                               .credentials("admin", "admin")
		                                               .connect()) {

			final String aQuery = "prefix stardog: <" + Namespaces.STARDOG + ">" +
			                      "select ?str where { bind(stardog:titleCase(\"this sentence does not use title case.\") as ?str) }";

			try (TupleQueryResult aResult = aConn.select(aQuery).execute()) {
				assertTrue("Should have a result", aResult.hasNext());

				final String aValue = aResult.next().getValue("str").stringValue();

				assertEquals("This Sentence Does Not Use Title Case.", aValue);

				assertFalse("Should have no more results", aResult.hasNext());
			}
		}
	}

	@Test
	public void testTitleCaseTooManyArgs() throws Exception {

		try (Connection aConn = ConnectionConfiguration.to(DB)
		                                               .credentials("admin", "admin")
		                                               .connect()) {
			final String aQuery = "prefix stardog: <" + Namespaces.STARDOG + ">" +
			                      "select ?str where { bind(stardog:titleCase(\"this is one argument.\", \"And this is another\") as ?str) }";

			try (TupleQueryResult aResult = aConn.select(aQuery).execute()) {
				// there should be a result because implicit in the query is the singleton set, so because the bind
				// should fail due to the value error, we expect a single empty binding
				assertTrue("Should have a result", aResult.hasNext());

				final BindingSet aBindingSet = aResult.next();

				assertTrue("Should have no bindings", aBindingSet.getBindingNames().isEmpty());

				assertFalse("Should have no more results", aResult.hasNext());
			}
		}
	}

	@Test
	public void testTitleCaseWrongType() throws Exception {

		try (Connection aConn = ConnectionConfiguration.to(DB)
		                                               .credentials("admin", "admin")
		                                               .connect()) {

			final String aQuery = "prefix stardog: <" + Namespaces.STARDOG + ">" +
			                      "select ?str where { bind(stardog:titleCase(7) as ?str) }";

			try (TupleQueryResult aResult = aConn.select(aQuery).execute()) {
				// there should be a result because implicit in the query is the singleton set, so because the bind
				// should fail due to the value error, we expect a single empty binding
				assertTrue("Should have a result", aResult.hasNext());

				final BindingSet aBindingSet = aResult.next();

				assertTrue("Should have no bindings", aBindingSet.getBindingNames().isEmpty());

				assertFalse("Should have no more results", aResult.hasNext());
			}
		}
	}
}
