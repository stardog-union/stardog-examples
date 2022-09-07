/*
 * Copyright (c) 2010 - 2017, Stardog Union. <http://www.stardog.com>
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

package com.complexible.stardog.examples.describe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;

import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.protocols.server.Server;
import com.complexible.common.protocols.server.ServerOptions;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.google.common.base.Charsets;
import com.stardog.stark.Statement;
import com.stardog.stark.Values;
import com.stardog.stark.io.RDFFormats;
import com.stardog.stark.query.GraphQueryResult;
import com.stardog.stark.vocabs.RDF;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Pavel Klinov
 */
public class TestExampleDescribe {
	private static final String DB = "testExampleDescribe";

	private static int TEST_PORT = 5858;

	private static Stardog STARDOG;

	private static Server SERVER;

	@BeforeClass
	public static void beforeClass() throws Exception {
		STARDOG = Stardog.builder()
		                 .set(ServerOptions.SECURITY_DISABLED, true)
		                 .create();

		SERVER = STARDOG.newServer()
		                .set(ServerOptions.SECURITY_DISABLED, true)
		                .bind(new InetSocketAddress("localhost", TEST_PORT))
		                .start();

		try (AdminConnection aConn = AdminConnectionConfiguration.toEmbeddedServer()
		                                                         .credentials("admin", "admin")
		                                                         .connect()) {
			if (aConn.list().contains(DB)) {
				aConn.drop(DB);
			}

			aConn.newDatabase(DB).create();
		}
	}

	@AfterClass
	public static void afterClass() throws IOException {
		SERVER.stop();
		STARDOG.shutdown();
	}

	@Test
	public void testExampleDescribe() throws Exception {
		String DATA = "@prefix : <urn:> . " +
		              ":Alice a :Person ;" +
		              "    :knows :Bob ." +
		              "" +
		              ":Bob a :Person ;" +
		              "  :knows :Charlie , :Dave ." +
		              "" +
		              ":Charlie a :Person ;" +
		              "  :knows :Alice ." +
		              "" +
		              ":Dave a :Person .";

		String aQuery = "prefix : <urn:> \n" +
		                "#pragma describe.strategy example \n" +
		                "DESCRIBE :Bob";

		try (Connection aConn = ConnectionConfiguration.to(DB)
		                                               .server("http://localhost:" + TEST_PORT)
		                                               .credentials("admin", "admin")
		                                               .connect()) {

			aConn.begin();
			aConn.add().io().format(RDFFormats.TURTLE).stream(new ByteArrayInputStream(DATA.getBytes(Charsets.UTF_8)));
			aConn.commit();

			try (GraphQueryResult aQueryResult = aConn.graph(aQuery).execute()) {
				Set<Statement> aResult = aQueryResult.toGraph();
				assertTrue(aResult.contains(Values.statement(Values.iri("urn:Bob"), RDF.TYPE, Values.iri("urn:Person"))));
				assertTrue(aResult.contains(Values.statement(Values.iri("urn:Alice"), Values.iri("urn:knows"), Values.iri("urn:Bob"))));
			}
		}
	}
}
