Stardog Versioning
==================

This tutorial explains how [Stardog versioning](http://docs.stardog.com/#_versioning) works. The tutorial uses the CLI but all the functionality explained here is 
also accessible via the [Java API](http://docs.stardog.com/java/snarl/com/complexible/stardog/api/versioning/VersioningConnection.html) 
or the [HTTP API](http://docs.stardog.apiary.io/#reference/versioning).

Stardog supports versioning capability that lets users track changes between revisions 
of a graph, i.e., a Stardog database, add comments and other metadata to the revisions, extract diffs 
between those revisions, tag revisions with labels, and query the revision history 
of the database using SPARQL.

Versioning support in Stardog works a bit like version control systems (`git`, for example)
so most of the terminology will be familiar if you work with these systems.
However, the versioning model in Stardog is simpler in the sense that there is no notion
of "branching", i.e., there is a single version history. 

Setup
------

In this tutorial we will use multiple users; let's create them:
```
$ stardog-admin user add -n jane --superuser
$ stardog-admin user add -n john --superuser
```

For simplicity the passwords of these users will be their usernames. We declare 
these users to be superusers so we don't need to grant explicit permissions.
*Needless to say this is done only for the purposes of this tutorial and should not be 
repeated in practice*.

Versioning support for a database is disabled by default but can be enabled at any time 
by setting the configuration option `versioning.enabled` to true. We will now create a
database with versioning enabled, and submit an empty file with only namespace
declarations so these namespaces will be saved in the database:
```
$ stardog-admin db create -o versioning.enabled=true -n demo namespaces.ttl
```

Versions
--------

When versioning is enabled all the changes to the database will be tracked automatically
and the differences between each commit will be saved in the revision history. We can also
associate a commit message with each version using the `vcs commit` command:
```
$ stardog vcs commit --add version1_add.trig -m "Adding Alice and Bob" -u john -p john demo
```

This command allows us to add and remove triples in one transaction:
```
$ stardog vcs commit --add version2_add.trig --remove version2_remove.trig -m "Changed Alice's email" -u jane -p jane demo
```

It is also possible to make a commit through a SPARQL update query:
```
$ stardog vcs commit --query version3_add.sparql -m "Add Charlie" demo
```

Note that the first two commands used an explicit username whereas the third command used
the default username `admin`. If the database is updated through the regular `data add`, 
`data remove`, or `query` commands when versioning is enabled, a corresponding version will 
be created but the commit message will be empty.

We can now list all the versions in this database:
```
$ stardog vcs list demo
Version:   e5efe6dd-6eed-4fe4-affa-a638f3b73adc
Committer: admin
Date:      2015-12-15 16:15:39

    Add Charlie

Version:   b69d3e37-e1ae-4efb-9e9d-c620315a4376
Committer: jane
Date:      2015-12-15 16:15:35

    Changed Alice's email

Version:   5e8c4060-e07a-4e66-8e10-68fdf1cb7512
Committer: john
Date:      2015-12-15 16:15:31

    Adding Alice and Bob

Version:   bc6cdd79-a406-447a-97fe-d8561f41952f
Committer: admin
Date:      2015-12-15 16:10:49

    Database creation
```

The `vcs list` command shows all the versions in reverse chronological order so newer
versions appear at the top. Every database will have an initial version that is created
automatically at database creation time but the initial contents of the database are not 
tracked so there will be no update data associated with the first version.

We can also specify constraints to list only a specific set of versions. For example, the
following command will show the last version committed by the user `jane`:
```
$ stardog vcs list --committer jane --limit 1 demo
Version:   b69d3e37-e1ae-4efb-9e9d-c620315a4376
Committer: jane
Date:      2015-12-15 16:15:35

    Changed Alice's email
```

Diffs
-----

Versioning allows users to see the exact set of changes between different versions of
the database. The diffs between versions are displayed as SPARQL update queries.

Executing the `vcs diff` command with no arguments will show the changes in the 
last commit:
```
$ stardog vcs diff demo
PREFIX : <http://example.org/test/>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX stardog: <tag:stardog:api:>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
INSERT DATA {
	
	:Charlie dc:publisher "Charlie" .
	GRAPH :Charlie {
		:Charlie a foaf:Person ;
			foaf:mbox "mailto:charlie@example.org" .
	}
};
```

We can see the diff between the current version of the database and any other version
by using the version ID. We will use the version created by `jane`'s commit 
(note that if you are following these steps the automatically generated version IDs
in your case will be different):
```
$ stardog vcs diff demo b69d3e37-e1ae-4efb-9e9d-c620315a4376
PREFIX : <http://example.org/test/>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX stardog: <tag:stardog:api:>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
INSERT DATA {
	
	:Charlie dc:publisher "Charlie" .
	GRAPH :Charlie {
		:Charlie a foaf:Person ;
			foaf:mbox "mailto:charlie@example.org" .
	}
};
```
We are seeing all the changes committed after the given version ID which is why the 
changes committed by `jane` are not included in the result.

In order to see the changes committed in a specific version we can use the `--single` 
option which will show the changes committed by `jane`:
```
$ stardog vcs diff --single demo b69d3e37-e1ae-4efb-9e9d-c620315a4376
PREFIX : <http://example.org/test/>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX stardog: <tag:stardog:api:>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
INSERT DATA {
	GRAPH :Alice {
		:Alice foaf:mbox "mailto:alice@another.example.org" .
	}
};
DELETE DATA {
	GRAPH :Alice {
		:Alice foaf:mbox "mailto:alice@example.org" .
	}
};
```

Tags
----

Passing version identifiers to commands can be cumbersome and error-prone. We can tag 
versions in the history with short, human-friendly names and use these names in the
commands. We can create a tag for the current version as follows:
```
$ stardog vcs tag --create v2 demo
```

We can also specify a version ID if we need to tag an older version. Let's tag the version created
by `jane`'s commit:
```
$ stardog vcs tag --create v1 --version b69d3e37-e1ae-4efb-9e9d-c620315a4376 demo
```

We can see a list of all tags:
```
$ stardog vcs tag --list demo
v2
v1
```

We can now perform the diff between the current version and the tagged version and get the 
same output as before:
```
$ stardog vcs diff demo v1
PREFIX : <http://example.org/test/>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX stardog: <tag:stardog:api:>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
INSERT DATA {
	
	:Charlie dc:publisher "Charlie" .
	GRAPH :Charlie {
		:Charlie a foaf:Person ;
			foaf:mbox "mailto:charlie@example.org" .
	}
};
```

Revert
------

We can revert the database to a previous version using the `vcs revert` command. This command 
does not perform any conflict resolution and simply applies the corresponding diff(s) in reverse
chronological order. All the additions specified in the given range will be removed and all 
the removals in the given range will be added. 

Note: You can use the diff command with the same range  to see the exact changes in the given range. Good practice is to check the effect of a revert command with a diff command preceding it.

The revert operation creates a new commit itself so we need to supply a commit message:
```
$ stardog vcs revert -m "Revert to version 1" demo v1 
Successfully reverted database
$ stardog vcs diff demo
PREFIX : <http://example.org/test/>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX stardog: <tag:stardog:api:>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
DELETE DATA {
	
	:Charlie dc:publisher "Charlie" .
	GRAPH :Charlie {
		:Charlie a foaf:Person ;
			foaf:mbox "mailto:charlie@example.org" .
	}
};
```

In a way similar to the diff command, a single commit can be reverted using the `--single` flag.

Version History
---------------

The version history is saved and indexed by Stardog as a separate database. The versioning
commands explained above operate by querying the version history. This database can be
queried directly by users.

Version history stores metadata in its default graph and the actual changes in named graphs.
[Versioning ontology](vcs_ontology.ttl) extends the [W3C PROV ontology](http://www.w3.org/TR/prov-overview/); comments in the ontology provide detailed explanations about the terms.

We will start by running a simple command that will retrieve all the versions:
```
$ stardog vcs query demo "SELECT * { ?v a vcs:Version }"
+-------------------------------------------------------------------------+
|                                    v                                    |
+-------------------------------------------------------------------------+
| tag:stardog:api:versioning:version:bc6cdd79-a406-447a-97fe-d8561f41952f |
| tag:stardog:api:versioning:version:5e8c4060-e07a-4e66-8e10-68fdf1cb7512 |
| tag:stardog:api:versioning:version:b69d3e37-e1ae-4efb-9e9d-c620315a4376 |
| tag:stardog:api:versioning:version:e5efe6dd-6eed-4fe4-affa-a638f3b73adc |
| tag:stardog:api:versioning:version:d6ff0689-570a-403d-b4af-f2392fb36e08 |
+-------------------------------------------------------------------------+

Query returned 5 results in 00:00:00.041
```

We can write a [query](list_versions.sparql) that retrieves more details about the versions:
```
$ stardog vcs query demo list_versions.sparql
+---------+-------------------------+------------------------------------------------------------------------------+
| author  |           msg           |                                     date                                     |
+---------+-------------------------+------------------------------------------------------------------------------+
| "admin" | "Database creation"     | "2015-12-15T16:10:49.446-05:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> |
| "admin" | "Add Charlie"           | "2015-12-15T16:15:39.043-05:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> |
| "admin" | "Revert to version 1"   | "2015-12-15T16:43:37.646-05:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> |
| "john"  | "Adding Alice and Bob"  | "2015-12-15T16:15:31.446-05:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> |
| "jane"  | "Changed Alice's email" | "2015-12-15T16:15:35.263-05:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> |
+---------+-------------------------+------------------------------------------------------------------------------+

Query returned 5 results in 00:00:00.048
```

We can also export the [version history metadata](vcs_export.ttl) with a query:
```
$ stardog vcs query -f turtle demo "CONSTRUCT WHERE {?s ?p ?o}" > vcs_export.ttl
```

Each update in a version specifies the named graph that was modified so we can list
all versions that modified a specific named graph:
```
$ stardog vcs query demo "select * { ?v vcs:updates/vcs:graph :Charlie }"
+-------------------------------------------------------------------------+
|                                    v                                    |
+-------------------------------------------------------------------------+
| tag:stardog:api:versioning:version:e5efe6dd-6eed-4fe4-affa-a638f3b73adc |
| tag:stardog:api:versioning:version:d6ff0689-570a-403d-b4af-f2392fb36e08 |
+-------------------------------------------------------------------------+

Query returned 2 results in 00:00:00.043
```

Now suppose we would like to know when the `Charlie` instance was deleted and by whom. We can get this information by finding the changeset that removed the triples with the subject `Charlie` and finding the associated version. Executing the [query](who_killed_charlie.sparql) will return the version we are looking for:
```
$ stardog vcs query demo who_killed_charlie.sparql 
+---------+-----------------------+------------------------------------------------------------------------------+
| author  |          msg          |                                     date                                     |
+---------+-----------------------+------------------------------------------------------------------------------+
| "admin" | "Revert to version 1" | "2015-12-15T16:43:37.646-05:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> |
+---------+-----------------------+------------------------------------------------------------------------------+

Query returned 1 results in 00:00:00.679
```
