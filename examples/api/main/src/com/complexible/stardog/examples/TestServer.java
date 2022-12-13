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

package com.complexible.stardog.examples;

import java.net.InetSocketAddress;

import com.complexible.common.protocols.server.Server;
import com.complexible.common.protocols.server.ServerException;
import com.complexible.stardog.Stardog;

/**
 * A simple class to start and shutdown a Stardog server. Normally, Stardog server would be running independently outside the JVM where client code
 * is running. The examples can be made to work with a remote Stardog server by not using this class and adjusting the server URL.
 */
public class TestServer {
	public static final String HOSTNAME = "localhost";
	public static final int PORT = 5820;

	private final Stardog stardog;
	private final Server server;

	public TestServer() throws ServerException {
		// first need to initialize the Stardog instance
		stardog = Stardog.builder().create();
		// start an http server on the default port
		server = stardog.newServer()
		                .bind(new InetSocketAddress(HOSTNAME, PORT))
		                .start();
	}

	public String getServerURL() {
		return "http://" + HOSTNAME + ":" + PORT;
	}

	public void shutdown() throws Exception {
		try {
			// stop the http server
			server.stop();
		}
		finally {
			// stop the Stardog kernel
			stardog.shutdown();
		}
	}
}
