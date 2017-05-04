package com.complexible.stardog.examples.tinkerpop;

import java.nio.file.Paths;

import com.complexible.common.protocols.server.ServerException;
import com.complexible.stardog.Contexts;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.db.DatabaseOptions;
import com.complexible.stardog.gremlin.StardogGraphConfiguration;
import com.complexible.stardog.gremlin.StardogGraphFactory;
import com.complexible.stardog.index.IndexOptions;
import com.google.common.base.Strings;
import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * @author Edgar Rodriguez-Diaz
 * @version 4.0
 * @since 4.0
 */
final class Util {

	/**
	 * Stardog User {@value}
	 */
	private static final String STARDOG_USER = "admin";

	/**
	 * Stardog Password {@value}
	 */
	private static final String STARDOG_PASSWORD = "admin";

	static Stardog loadDataset(final String dbName, final String filePath) throws ServerException {
		// Initializing Stardog instance
		Stardog aServer = Stardog.builder().create();

		// Next we'll establish a admin connection to Stardog so we can create a database to use for the example
		try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
		                                                                    .credentials(STARDOG_USER, STARDOG_PASSWORD)
		                                                                    .connect()) {
			// If the database already exists, we'll drop it and create a fresh copy
			if (aAdminConnection.list().contains(dbName)) {
				aAdminConnection.drop(dbName);
			}

			/**
			 * Create the DB with the given Metadata:
			 * - The database name (1)
			 * - Setting the database to have a disk index (2)
			 * - Disable canonical literals (3)
			 * @see <a href="https://stardog.com/docs/#_database_configuration">Database Configuration for TinkerPop 3</a>
			 */
			aAdminConnection.disk(dbName)
			                .set(DatabaseOptions.NAME, dbName)
			                .set(IndexOptions.CANONICAL_LITERALS, false)
			                .create(Paths.get(filePath));
		}

		return aServer;
	}

	static void cleanup(final String dbName) throws ServerException {
		try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
		                                                                    .credentials(STARDOG_USER, STARDOG_PASSWORD)
		                                                                    .connect()) {
			if (aAdminConnection.list().contains(dbName)) {
				aAdminConnection.drop(dbName);
			}
		}
	}

	static Graph openGraph(final String theConnString,
	                       final boolean theReasoningFlag,
	                       final boolean theCache,
	                       final String theBaseIRI) throws StardogException {
		StardogGraphConfiguration.Builder aConfig = StardogGraphConfiguration.builder()
		                                                                     .connectionString(theConnString)
		                                                                     .credentials(STARDOG_USER, STARDOG_PASSWORD)
		                                                                     .reasoning(theReasoningFlag) /* is reasoning enabled? */
		                                                                     .namedGraph(Contexts.DEFAULT.stringValue() /* Abox data is in the default graph */)
		                                                                     .cache(theCache);

		if (!Strings.isNullOrEmpty(theBaseIRI)) {
			aConfig.baseIRI(theBaseIRI);
		}

		return StardogGraphFactory.open(aConfig.build());
	}

}
