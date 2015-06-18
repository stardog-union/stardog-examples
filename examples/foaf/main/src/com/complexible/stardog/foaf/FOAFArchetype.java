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

package com.complexible.stardog.foaf;

import com.complexible.stardog.db.DatabaseArchetype;
import org.openrdf.model.vocabulary.FOAF;

import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.icv.ConstrainedDatabaseArchetypeBase;

/**
 * User-defined database archetype example for FOAF. This example shows an archetype with constraints but we can
 * also define archetypes without constraints by extending {@link DatabaseArchetype} instead.
 * 
 * @author Evren Sirin
 */
public class FOAFArchetype extends ConstrainedDatabaseArchetypeBase {
	public FOAFArchetype() {
	    super(FOAF.PREFIX, // name of the archetype (also used as the prefix in namespaces)
	          Values.uri(FOAF.NAMESPACE), // namespace for the archetype (used in stored namesapces)
	          readStatements(FOAFArchetype.class, "foaf.rdf"), // one or more ontologies for the archetype
	          readStatements(FOAFArchetype.class, "foaf-constraints.ttl")
	    );
    }
}
