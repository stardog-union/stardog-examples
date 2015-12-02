package com.complexible.stardog.examples.tinkerpop;

import java.nio.file.Paths;

import com.complexible.common.protocols.server.Server;
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
import com.complexible.stardog.metadata.Metadata;
import com.complexible.stardog.protocols.snarl.SNARLProtocolConstants;
import com.google.common.base.Strings;
import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * @author  Edgar Rodriguez-Diaz
 * @since   4.0
 * @version 4.0
 */
final class Util {

	/**
	 * Stardog User {@value}
	 */
	static final String STARDOG_USER = "admin";

	/**
	 * Stardog Password {@value}
	 */
	static final String STARDOG_PASSWORD = "admin";

	static Server loadDataset(final String dbName,
	                                  final String filePath) throws ServerException {
		// Creating a Server
		// -----------------
		// You'll need a server to connect to, obviously.  For the example, lets create an embedded server.
		Server aServer = Stardog.buildServer()
		                        .bind(SNARLProtocolConstants.EMBEDDED_ADDRESS)
		                        .start();

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
			 * - Setting the database to have a Memory index (2)
			 * - Disable canonical literals (3)
			 * @see <a href="http://docs.stardog.com/#_database_configuration">Database Configuration for TinkerPop 3</a>
			 */
			aAdminConnection.builder(Metadata.create()
				                         .set(DatabaseOptions.NAME, dbName)                             // (1)
				                         .set(IndexOptions.INDEX_TYPE, IndexOptions.IndexType.Memory)   // (2)
				                         .set(IndexOptions.CANONICAL_LITERALS, false))                  // (3)
				.create(Paths.get(filePath));

		}

		return aServer;
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

	static Graph openGraph(final String theConnString, final boolean theReasoningFlag, final boolean theCache) {
		return openGraph(theConnString, theReasoningFlag, theCache, null);
	}

}
