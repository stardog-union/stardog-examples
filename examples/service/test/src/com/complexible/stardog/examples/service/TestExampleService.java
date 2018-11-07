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

package com.complexible.stardog.examples.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.complexible.common.protocols.server.Server;
import com.complexible.common.protocols.server.ServerOptions;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.stardog.stark.io.RDFFormats;
import com.stardog.stark.query.SelectQueryResult;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Evren Sirin
 */
public class TestExampleService {
	private static final String DB = "testExampleService";

	private static int TEST_PORT = 5858;

	private static File TEST_HOME;

	private static Stardog STARDOG;

	private static Server SERVER;

	@BeforeClass
	public static void beforeClass() throws Exception {
		TEST_HOME = Files.createTempDir();

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

			aConn.createMemory(DB);
		}
	}

	@AfterClass
	public static void afterClass() throws IOException {
		SERVER.stop();

		STARDOG.shutdown();

		FileUtils.deleteDirectory(TEST_HOME);
	}

	@Test
	public void testExampleService() throws Exception {
		String DATA = ":Alice a :Person ;" +
		              "    :knows :Bob ." +
		              "" +
		              ":Bob a :Person ;" +
		              "  :knows :Charlie , :Dave ." +
		              "" +
		              ":Charlie a :Person ;" +
		              "  :knows :Alice ." +
		              "" +
		              ":Dave a :Person .";

		String aQuery = "SELECT * {\n" +
		                "  ?person a :Person " +
		                "" +
		                "  SERVICE <example://localhost:" + TEST_PORT + "/" + DB + "/query> {\n" +
		                "     ?person :knows ?friend " +
		                "  }" +
		                "}";

		try (Connection aConn = ConnectionConfiguration.to(DB)
		                                               .server("http://localhost:" + TEST_PORT)
		                                               .credentials("admin", "admin")
		                                               .connect()) {

			aConn.begin();
			aConn.add().io().format(RDFFormats.TURTLE).stream(new ByteArrayInputStream(DATA.getBytes(Charsets.UTF_8)));
			aConn.commit();

			try (SelectQueryResult aResult = aConn.select(aQuery).execute()) {
				assertTrue("Should have a result", aResult.hasNext());
			}
		}
	}
}
