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

package com.complexible.stardog.examples.connectable.listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;

import com.complexible.common.protocols.server.Server;
import com.complexible.common.protocols.server.ServerOptions;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.stardog.stark.Statement;
import com.stardog.stark.Values;

import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is NOT a test class. This class simply makes some changes in the database which will end up calling the ExampleConnectable that will
 * print the changes in the console. The console output for each test can be used for informational purposes. This class can be turned into a proper
 * automated test with some additional work.
 *
 * @author  Evren Sirin
 */
public class ExampleConnectableTest {
	private static final String DB = "ConnectableTestDB";

	private static int TEST_PORT = 5858;

	private static Stardog STARDOG;

	private static Server SERVER;

	private static String SERVER_URL = "http://localhost:" + TEST_PORT;

	@BeforeClass
	public static void beforeClass() throws Exception {
		STARDOG = Stardog.builder()
		                 .set(ServerOptions.SECURITY_DISABLED, true)
		                 .create();

		SERVER = STARDOG.newServer()
		                .set(ServerOptions.SECURITY_DISABLED, true)
		                .bind(new InetSocketAddress("localhost", TEST_PORT))
		                .start();
	}

	@AfterClass
	public static void afterClass() throws IOException {
		SERVER.stop();

		STARDOG.shutdown();
	}

	private AdminConnection connectAdmin() {
		return AdminConnectionConfiguration.toServer(SERVER_URL)
		                                   .credentials("admin", "admin")
		                                   .connect();
	}

	private Connection connect() {
		return ConnectionConfiguration
			       .to(DB)
			       .server(SERVER_URL)
			       .credentials("admin", "admin")
			       .connect();
	}

	@Before
	public void createDB() {
		// we will recreate the DB for each test to avoid test contamination
		try (AdminConnection aConn = connectAdmin()) {
			if (aConn.list().contains(DB)) {
				aConn.drop(DB);
			}
			aConn.newDatabase(DB).create();
		}
	}

	@After
	public void dropDB() {
		try (AdminConnection aConn = connectAdmin()) {
			if (aConn.list().contains(DB)) {
				aConn.drop(DB);
			}
		}
	}

	@Test
	public void testAddAndRemove() throws Exception {
		try (Connection aConn = connect()) {
			aConn.begin();
			Set<Statement> g1 = ImmutableSet.of(Values.statement(Values.iri("urn:s1"),
			                                             Values.iri("urn:p1"),
			                                             Values.iri("urn:o1"),
			                                             Values.iri("urn:g1")));
			Set<Statement> g2 = ImmutableSet.of(Values.statement(Values.iri("urn:s2"),
			                                             Values.iri("urn:p2"),
			                                             Values.iri("urn:o2"),
			                                             Values.iri("urn:g2")));
			aConn.add()
			     .graph(g1)
			     .graph(g2);
			aConn.commit();

			aConn.begin();
			aConn.remove().graph(g1);
			aConn.commit();
		}
	}

	@Test
	public void testRemoveGraph() throws Exception {
		try (Connection aConn = connect()) {
			aConn.begin();
			Set<Statement> g = ImmutableSet.of(Values.statement(Values.iri("urn:s1"),
			                                            Values.iri("urn:p1"),
			                                            Values.iri("urn:o1"),
			                                            Values.iri("urn:g1")));
			aConn.add().graph(g);
			aConn.commit();

			aConn.begin();
			aConn.remove().context(Values.iri("urn:g3"));
			aConn.commit();
		}
	}

	@Test
	public void testRemoveAll() throws Exception {
		try (Connection aConn = connect()) {
			aConn.begin();
			Set<Statement> g1 = ImmutableSet.of(Values.statement(Values.iri("urn:s1"),
			                                             Values.iri("urn:p1"),
			                                             Values.iri("urn:o1"),
			                                             Values.iri("urn:g1")));
			Set<Statement> g2 = ImmutableSet.of(Values.statement(Values.iri("urn:s2"),
			                                             Values.iri("urn:p2"),
			                                             Values.iri("urn:o2")));
			aConn.add()
			     .graph(g1)
			     .graph(g2);
			aConn.commit();

			aConn.begin();
			aConn.remove().all();
			aConn.commit();
		}
	}

	@Test
	public void testRollback() throws Exception {
		try (Connection aConn = ConnectionConfiguration
			                        .to(DB)
			                        .server(SERVER_URL)
			                        .credentials("admin", "admin")
			                        .connect()) {
			aConn.begin();
			aConn.add().statement(Values.statement(Values.iri("urn:s1"),
			                                       Values.iri("urn:p1"),
			                                       Values.iri("urn:o1")));
			aConn.rollback();
		}
	}

	@Test
	public void testMoveQuery() throws Exception {
		try (Connection aConn = connect()) {
			aConn.begin();
			Set<Statement> g1 = ImmutableSet.of(Values.statement(Values.iri("urn:s1"),
			                                             Values.iri("urn:p1"),
			                                             Values.iri("urn:o1"),
			                                             Values.iri("urn:g1")));
			Set<Statement> g2 = ImmutableSet.of(Values.statement(Values.iri("urn:s2"),
			                                             Values.iri("urn:p2"),
			                                             Values.iri("urn:o2"),
			                                             Values.iri("urn:g2")));
			aConn.add()
			     .graph(g1)
			     .graph(g2);
			aConn.commit();

			aConn.update("MOVE <urn:g1> TO <urn:g2>").execute();
		}
	}
}
