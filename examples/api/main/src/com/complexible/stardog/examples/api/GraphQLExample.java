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

package com.complexible.stardog.examples.api;

import java.nio.file.Paths;

import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.api.graphql.GraphQLConnection;
import graphql.ExecutionResult;

/**
 * <p>Example showing how to run the GraphQL queries</p>
 *
 * @author Evren Sirin
 */
public class GraphQLExample {

	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

		String db = "graphQL";

		try {
			// Open an `AdminConnection` to Stardog so we can set up our database for the example
			try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
			                                                                    .credentials("admin", "admin")
			                                                                    .connect()) {
				// If the example database exists, drop it, so we can create it fresh
				if (aAdminConnection.list().contains(db)) {
					aAdminConnection.drop(db);
				}

				// create a new database with starwars data
				aAdminConnection.newDatabase(db).create(Paths.get("data/starwars.ttl"));

				// Obtain a GraphQLConnection connection to the database
				try (GraphQLConnection aConn = ConnectionConfiguration
					                               .to(db)
					                               .credentials("admin", "admin")
					                               .connect()
					                               .as(GraphQLConnection.class)) {
					ExecutionResult aResult;

					// execute a GraphQL query
					aResult = aConn.graphql("{ Human { name } }")
					               .execute();
					System.out.println("All Humans:");
					System.out.println(aResult.getData().toString());

					// execute a GraphQL query with arguments
					String query = "query getHuman($id: Integer) { " +
					               "  Human(id: $id) { " +
					               "     name " +
					               "  } " +
					               "}";
					aResult = aConn.graphql(query)
					               .parameter("id", 1000)
					               .execute();
					System.out.println("Human with id 1000");
					System.out.println(aResult.getData().toString());
				}

				// Obtain a GraphQLConnection connection to the database with reasoning enabled
				try (GraphQLConnection aConn = ConnectionConfiguration
					                               .to(db)
					                               .credentials("admin", "admin")
					                               .reasoning(true)
					                               .connect()
					                               .as(GraphQLConnection.class)) {
					// execute a GraphQL query with reasoning
					String query = "{" +
					               "  Character { " +
					               "     name " +
					               "  } " +
					               "}";
					ExecutionResult aResult = aConn.graphql(query).execute();
					System.out.println("All Characters:");
					System.out.println(aResult.getData().toString());
				}
				finally {
					// remove the database
					aAdminConnection.drop(db);
				}
			}
		}
		finally {
			aStardog.shutdown();
		}
	}
}
