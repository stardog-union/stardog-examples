/*
 * Copyright (c) 2010-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.stardog.examples.connectable.materialization;

import java.nio.file.Paths;

import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Simply loads some data to see if the {@link MaterializationConnectable} works
 *
 * @author Pavel Klinov
 */
public class MaterializationConnectableTest {

	private static final String DB = "testMaterialization";

	private static Stardog stardog;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// we need to initialize the Stardog instance which will automatically start the embedded server.
		stardog = Stardog.builder()
		                 // TODO path to your home dir here, put your Stardog license file there
		                 //.home(new File("/Users/pavel/cp/data/derivo/home"))
		                 .create();
	}

	@Test
	public void testMaterialization() throws Exception {
		try (AdminConnection aAdminConn = AdminConnectionConfiguration.toEmbeddedServer()
		                                                              .credentials("admin", "admin")
		                                                              .connect()) {
			if (aAdminConn.list().contains(DB)) {
				aAdminConn.drop(DB);
			}
			// the connectable will be invoked when the database is initialized
			aAdminConn.newDatabase(DB)
			          .set(MaterializationOptions.MATERIALIZATION_ENABLED, true)
			          .create(Paths.get(MaterializationConnectableTest.class.getClassLoader().getResource("test-data.nt.gz").toURI())).connect();

			aAdminConn.drop(DB);
		}
	}

	@AfterClass
	public static void afterClass() throws Exception {
		stardog.shutdown();
	}

}
