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

package com.complexible.stardog.examples.docs;

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
import com.complexible.stardog.docs.BitesConnection;
import com.stardog.stark.IRI;
import com.stardog.stark.Literal;
import com.stardog.stark.query.SelectQueryResult;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>Test case for Stardog pluggable RDF extractor</p>
 *
 * @author  Jess Balint
 */
public class WordCountExtractorTest {
	private static final String DB = "testExtractor";

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

		try (AdminConnection aConn = AdminConnectionConfiguration.toServer(SERVER_URL)
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
	public void testWordCountExtractor() throws Exception {
		try (Connection aConn = ConnectionConfiguration
			                        .to(DB)
			                        .server(SERVER_URL)
			                        .credentials("admin", "admin")
			                        .connect()) {
			BitesConnection aDocsConn = aConn.as(BitesConnection.class);

			IRI aDocIri = aDocsConn.putDocument(new File("input.pdf").toPath());

			String aQuery = "select ?wc { graph ?doc { ?doc <tag:stardog:example:wordcount> ?wc } }";
			try(SelectQueryResult aRes = aConn.select(aQuery).parameter("doc", aDocIri).execute()) {
				int aWordCount = Literal.intValue(aRes.next().literal("wc").orElseThrow(Exception::new));
				assertEquals(313, aWordCount);
			}
		}
	}
}
