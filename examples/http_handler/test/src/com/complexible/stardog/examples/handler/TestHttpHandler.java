package com.complexible.stardog.examples.handler;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.complexible.common.protocols.server.Server;
import com.complexible.common.protocols.server.ServerOptions;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.stardog.http.server.undertow.HttpServiceLoader;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TestHttpHandler  {

	private static final String DB = "testExampleHandler";

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
	public void testExampleHandler() throws Exception {

		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {
			@Override
			public Principal getUserPrincipal() {
				return new Principal() {
					@Override
					public String getName() {
						return "admin";
					}
				};
			}
			@Override
			public String getPassword() {
				return "admin";
			}
		});

		CloseableHttpClient httpclient2 = HttpClients.custom()
		                                             .setDefaultSocketConfig(SocketConfig.custom()
		                                                                                 .setSoKeepAlive(true)
		                                                                                 .setTcpNoDelay(true)
		                                                                                 .build())
		                                             .setMaxConnPerRoute(Math.max(10, 20))
		                                             .setMaxConnTotal(200)
		                                             .evictExpiredConnections()
		                                             .evictIdleConnections(1L, TimeUnit.SECONDS)
		                                             .setDefaultCredentialsProvider(credentialsProvider)
		                                             .build();

		HttpUriRequest request = new HttpGet("http://localhost:" + TEST_PORT);

		HttpResponse response = httpclient2.execute(request);
		boolean found = false;
		for(Header aH : response.getAllHeaders()) {
			if ("TEST_HEADER".equals(aH.getName())) {
				found = true;
			}
		}
		assertTrue("The test header was not found", found);
	}
}
