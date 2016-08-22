/*
 * Copyright (c) 2010-2016 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import com.complexible.common.protocols.server.Server;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.docs.StardocsConnection;
import com.complexible.stardog.docs.StardocsOptions;
import com.complexible.stardog.protocols.snarl.SNARLProtocolConstants;
import com.google.common.collect.Lists;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.IRI;
import org.openrdf.query.TupleQueryResult;

import java.io.File;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;

/**
 * <p>Test case for Stardog pluggable RDF extractor</p>
 *
 * @author  Jess Balint
 */
public class WordCountExtractorTest {
	private static Server SERVER = null;

	private static final String DB = "testExtractor";

	@BeforeClass
	public static void beforeClass() throws Exception {
		SERVER = Stardog.buildServer()
		                .bind(new InetSocketAddress("localhost", 5821))
		                .start();

		final AdminConnection aConn = AdminConnectionConfiguration.toServer("http://localhost:5821")
		                                                          .credentials("admin", "admin")
		                                                          .connect();

		try {
			if (aConn.list().contains(DB)) {
				aConn.drop(DB);
			}

			aConn.memory(DB).set(StardocsOptions.DOCS_DEFAULT_RDF_EXTRACTORS, "WordCountExtractor").create();
		}
		finally {
			aConn.close();
		}
	}

	@AfterClass
	public static void afterClass() {
		if (SERVER != null) {
			SERVER.stop();
		}
	}

	@Test
	public void testWordCountExtractor() throws Exception {
		Connection aConn = ConnectionConfiguration.to(DB).server("http://localhost:5821").credentials("admin", "admin").connect();
		StardocsConnection aDocsConn = aConn.as(StardocsConnection.class);

		IRI aDocIri = aDocsConn.putDocument(new File("input.pdf").toPath());

		try {
			String aQuery = "select ?wc { graph ?doc { ?doc <tag:stardog:example:wordcount> ?wc } }";
			TupleQueryResult aRes = aConn.select(aQuery).parameter("doc", aDocIri).execute();
			String wordCount = aRes.next().getBinding("wc").getValue().stringValue();
			assertEquals("313", wordCount);
		}
		finally {
			aConn.close();
		}
	}
}
