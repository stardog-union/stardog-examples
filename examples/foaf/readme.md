# Database Archetype Extensibility

The [Stardog database archetypes)(http://docs.stardog.com/#_database_archetypes_) provide a simple way to associate one or more
ontologies and optionally a set of constraints with a database. Stardog provides two built-in database archetypes out-of-the-box:
PROV and SKOS. This example shows a user-define archetype for [FOAF](http://xmlns.com/foaf/spec/).

## Running FOAF Example

First build the jar file for this example using gradle:
```bash
$ ./gradlew jar
```

Copy the jar file to your Stardog installation directory and (re)start the server:
```bash
$ cp examples/foaf/build/libs/foaf-*.jar $STARDOG/server/dbms/
$ $STARDOG/bin/stardog-admin server start
```

Create a new database using the FOAF archetype:
```bash
$ $STARDOG/bin/stardog-admin db create -o database.archetypes="foaf" -n foafDB
```

That's it. Even tough you created a database without any data you will see that there is a default namespace, ontology and
constraints associated with this database:

```bash
$ bin/stardog namespace list foafDB
+---------+---------------------------------------------+
| Prefix  |                  Namespace                  |
+---------+---------------------------------------------+
| foaf    | http://xmlns.com/foaf/0.1/                  |
| owl     | http://www.w3.org/2002/07/owl#              |
| rdf     | http://www.w3.org/1999/02/22-rdf-syntax-ns# |
| rdfs    | http://www.w3.org/2000/01/rdf-schema#       |
| stardog | tag:stardog:api:                            |
| xsd     | http://www.w3.org/2001/XMLSchema#           |
+---------+---------------------------------------------+
$ bin/stardog reasoning schema foafDB
foaf:publications a owl:ObjectProperty
foaf:jabberID a owl:InverseFunctionalProperty
foaf:jabberID a owl:DatatypeProperty
foaf:interest rdfs:domain foaf:Agent
foaf:workInfoHomepage a owl:ObjectProperty
foaf:schoolHomepage rdfs:range foaf:Document
foaf:status a owl:DatatypeProperty
foaf:currentProject rdfs:domain foaf:Person
...
$ bin/stardog icv export foafDB
AxiomConstraint{foaf:isPrimaryTopicOf a owl:InverseFunctionalProperty}
```

## Registering Archetypes

User-defined archetypes are loaded to Stardog through JDK `ServiceLoader` framework. Create a file called
`com.complexible.stardog.db.DatabaseArchetype` in the `META-INF/services` directory.  The contents of this file
should be all of the *fully-qualified* class names for your custom archetypes.  The jar containing this
`META-INF/services` directory as well as the implementations for the archetypes) it references is added on
the classpath. Stardog will pick up the archetype implementations on startup.
