Integrity Constraint Validation
===============================

This tutorial explains how Stardog [Integrity Constraint Validation 
(ICV)](http://docs.stardog.com/#_validating_constraints)
works. The tutorial uses the CLI but all the functionality explained here is 
also accessible via the [Java API](http://docs.stardog.com/java/snarl/com/complexible/stardog/icv/api/ICVConnection.html) 
or the [HTTP API](http://docs.stardog.apiary.io/#reference/icv).

We will use a slightly modified version of [this RDF Validation 
Example](http://www.w3.org/2012/12/rdf-val/SOTA). In this example, we 
have a database used for issue tracking. We have a [simple schema](issues-schema.ttl)
that defines the relevant classes and properties for tracking issues
and a set of [constraints](issues-constraints.ttl) defined in OWL. We 
will use [instance data](issues-data.ttl) with various validation errors
for the tutorial.


Validation
----------

First we create the Stardog database and load the sample schema and instance 
data into the database:
```
$ stardog-admin db create -n issues issues-schema.ttl issues-data.ttl
```

We can now validate database contents against a set of constraints:
```
$ stardog icv validate issues issues-constraints.ttl
Data is NOT valid.
The following constraints were violated:
AxiomConstraint{:state rdfs:range :ValidState}
AxiomConstraint{:state rdfs:domain :Issue}
AxiomConstraint{:Issue rdfs:subClassOf (:reportedBy max 1 owl:Thing)}
AxiomConstraint{:reportedOn rdfs:domain :Issue}
AxiomConstraint{:reproducedBy rdfs:range foaf:Person}
AxiomConstraint{:related rdfs:range :Issue}
AxiomConstraint{:reportedBy rdfs:range foaf:Person}
```

The constraint file may contain multiple constraint axioms and all of those
constraints will be checked for validation. If we are going to use the
validation axioms frequently we can add them to the database as constraints:
```
$ stardog-admin icv add issues issues-constraints.ttl
Successfully added constraints in 00:00:00.
```

Note that we are not adding the constraints as regular data and instead 
add them with a special command since constraints are interpreted different
than regular schema axioms. You can learn more about these differences by
looking at [detailed examples](http://docs.stardog.com/#_icv_examples).

You can see the list of all constraints in the database:
```
$ stardog icv export issues
AxiomConstraint{:state rdfs:range :ValidState}
AxiomConstraint{:Issue rdfs:subClassOf (:state min 1 owl:Thing)}
AxiomConstraint{:reportedOn rdfs:range xsd:dateTime}
AxiomConstraint{:reproducedOn rdfs:range xsd:dateTime}
AxiomConstraint{:reproducedBy rdfs:domain :Issue}
AxiomConstraint{:state rdfs:domain :Issue}
AxiomConstraint{:Issue rdfs:subClassOf (:reportedBy min 1 owl:Thing)}
AxiomConstraint{:Issue rdfs:subClassOf (:reportedBy max 1 owl:Thing)}
AxiomConstraint{:reportedOn rdfs:domain :Issue}
AxiomConstraint{:state a owl:FunctionalProperty}
AxiomConstraint{:reproducedBy rdfs:range foaf:Person}
AxiomConstraint{:reproducedOn rdfs:domain :Issue}
AxiomConstraint{:Issue rdfs:subClassOf (:reportedOn min 1 rdfs:Literal)}
AxiomConstraint{:Issue rdfs:subClassOf (:reportedOn max 1 rdfs:Literal)}
AxiomConstraint{:reportedBy rdfs:domain :Issue}
AxiomConstraint{:related rdfs:range :Issue}
AxiomConstraint{:related rdfs:domain :Issue}
AxiomConstraint{:reportedBy rdfs:range foaf:Person}
```

Running the validate command with a constraint file will do validation using 
the constraints added to the database:
```
$ stardog icv validate issues
Data is NOT valid.
The following constraints were violated:
...
```

Explanations
------------

The validate output tells us there are violations but doesn't provide more 
details. We can run the explanation command to get details about violations:
```
$ stardog icv explain issues
VIOLATED :state rdfs:range :ValidState
   ASSERTED :issue4 :state :unsinged
   NOT_INFERRED :unsinged a :ValidState
```
By default the explain command prints an explanation of a single violation.
Explanation is printed in an indented tree structure that shows the constraint
violated and how the presence of absence of inferences and assertions causes
the violation. In the above explanation we see that there is a typo for the 
state of `:issue4` and as a result the constraint for the state value to be
a `ValidState` is violated.

It is possible to increase the number of explanations shown with this command.
We can also use the `--merge` option so related violations will be grouped 
together in the explanation output:
```
$ stardog icv explain --limit 10 --merge issues
VIOLATED :Issue rdfs:subClassOf (:reportedBy max 1 owl:Thing)
   ASSERTED :issue7 :reportedBy :user6
   ASSERTED :issue7 :reportedBy :user2
   ASSERTED :issue7 a :Issue

VIOLATED :reproducedBy rdfs:range foaf:Person
   ASSERTED :issue7 :reproducedBy :user1
   NOT_INFERRED :user1 a foaf:Person

VIOLATED :state rdfs:domain :Issue
   ASSERTED :issue4 :state :unsinged
   NOT_INFERRED :issue4 a :Issue

VIOLATED :reportedOn rdfs:domain :Issue
   ASSERTED :issue4 :reportedOn "x0"
   NOT_INFERRED :issue4 a :Issue

VIOLATED :state rdfs:range :ValidState
   ASSERTED :issue4 :state :unsinged
   NOT_INFERRED :unsinged a :ValidState

VIOLATED :reportedBy rdfs:range foaf:Person
   ASSERTED :issue7 :reportedBy :user6
   NOT_INFERRED :user6 a foaf:Person

1.1) VIOLATED :related rdfs:range :Issue
   ASSERTED :issue7 :related :issue4
   NOT_INFERRED :issue4 a :Issue
1.2) VIOLATED :related rdfs:range :Issue
   ASSERTED :issue7 :related :issue3
   NOT_INFERRED :issue3 a :Issue
1.3) VIOLATED :related rdfs:range :Issue
   ASSERTED :issue7 :related :issue2
   NOT_INFERRED :issue2 a :Issue
```

Fixing Violations
-----------------

Stardog can help users fix constraint violations by suggesting necessary steps
to take for fixing all violations. Correct action to fix a violation depends on
the domain and sometimes adding new statements might be the right action and
sometimes deleting. Stardog repair plans try to minimize the number of actions.

The `icv fix` command by default suggests a single plan (written as a SPARQL
update query)  that will fix all the violations:

```
$ stardog icv fix issues 
#
# Plan 1
#
PREFIX : <http://example.org/issues#>
PREFIX foaf: <http://xmlns.com/foaf/0.1/'>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX stardog: <tag:stardog:api:>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
DELETE DATA {
	
	:issue7 :reportedBy :user6 .
	
	:issue4 :state :unsinged .
};
INSERT DATA {
	
	:issue4 a :Issue .
};
DELETE DATA {
	
	:issue7 :related :issue3 , :issue2 ;
		:reproducedBy :user1 .
};
```

You can increase the `--limit` to see more plans and pick a different plan
or customize a plan manually to apply a different set of changes. You can
also use the `--execute` option to run the first SPARQL update query right 
away but note that there is no way to undo this action so make sure the 
first plan is the correct one before using this option.

Note that, a possible fix for any violation is to remove the violated 
constraint but `icv fix` does not suggest that kind of repair, even though 
it may be appropriate in some cases.

Validation and Reasoning
------------------------

Constraint validation does not use reasoning by default but this can be 
enabled by the user. Enabling reasoning may cause a new violation to
occur or might cause a previous violation to disappear. Let's first
remove the instance data (but keep the schema):

```
$ stardog data remove issues issues-data.ttl
Removing data from file: issues-data.ttl
Removed 23 triples in 00:00:00.039
```

Now we will add an instance of `SecurityIssue` class:

```
$ stardog query issues "INSERT DATA { :secIssue a :SecurityIssue }"
```

One might expect the constraints defined for `Issue` to be applicable
to this instance since `SecurityIssue` is defined to be a subclass but
without reasoning this would not happen:
```
$ stardog icv validate issues
Data is valid.
```

We will get the expected violations if we enable reasoning:
```
$ stardog icv validate --reasoning issues
Data is NOT valid.
The following constraints were violated:
AxiomConstraint{:Issue rdfs:subClassOf (:state min 1 owl:Thing)}
AxiomConstraint{:Issue rdfs:subClassOf (:reportedBy min 1 owl:Thing)}
AxiomConstraint{:Issue rdfs:subClassOf (:reportedOn min 1 rdfs:Literal)}
```

We can enable reasoning for the explain command to see how reasoning
played a role in the violation where each inference is displayed and 
explained in the output:
```
$ stardog icv explain --reasoning issues
VIOLATED :Issue rdfs:subClassOf (:state min 1 owl:Thing)
   INFERRED :secIssue a :Issue
      ASSERTED :SecurityIssue rdfs:subClassOf :Issue
      ASSERTED :secIssue a :SecurityIssue
   NOT_INFERRED :secIssue :state <tag:stardog:api:variable:x0>
```

We can now add the missing triples and make the data valid again:
```
$ stardog query issues 'INSERT DATA {
    :secIssue :state :unassigned ;
              :reportedBy :user1 ;
              :reportedOn "2012-12-31T23:57:00Z"^^xsd:dateTime . 
    :user1 a foaf:Person . 
}'
Update query processed successfully in 00:00:00.029.
$ stardog icv validate --reasoning issues
Data is valid.
```
Note that the domain constraints for the properties `state`, `reportedBy`,
and `reportedOn` require the subject to be an `Issue` instance and these
constraints are satisfied since this inference is computed by reasoning.

Preventing Violations
---------------------

Stardog can perform constraint validation as part of its transactional 
cycle and fail transactions that violate constraints. This is called the
"guard mode" and prevents from any violation to occur. It must be enabled 
explicitly in the database configuration options but it cannot be enabled
if the database contains constraint violations so we will first clear our 
database:

```
$ stardog data remove --all issues 
```

Guard mode can be enabled at database creation time or later. Reasoning
for guard mode validation should be enabled separately. If we want to 
enable guard mode for an existing database, we need to take the database 
offline first and set the options later:

```
$ stardog-admin db offline --timeout 0 issues 
$ stardog-admin db metadata set -o icv.enabled=true icv.reasoning.enabled=true issues
$ stardog-admin db online issues
```

In the Web Console you can set the database offline, click Edit, change 
the "ICV Enabled" value (and "ICV reasoning enabled" if desired), click Save 
and set the database online again.

Once guard mode is enabled, modifications of the database (via SPARQL Update 
or any other method), whether adds or deletes, that violate the integrity 
constraints will cause the transaction to fail:

```
$ stardog query issues "INSERT DATA { :myIssue a :Issue }"
ICV validation failed, one or more constraints have been violated.
```

SPARQL Constraints
------------------

Stardog allows one to use SPARQL queries as constraints too. Validating a 
query constraints works such that if a query returns non-empty results it 
is violated.

We can write a very complex SPARQL query `issues-query.sparql` that would
encode the equivalent set of constraints in a single SPARQL query and
validate the query:
```
$ stardog icv validate issues issues-query.sparql
Data is NOT valid.
The following constraints were violated:
SPARQLConstraint{PREFIX : <http://www.w3.org/2012/12/rdf-val/issues-ex#>
PREFIX foaf: <http://xmlns.com/foaf/0.1/'>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

SELECT DISTINCT ?issue
...
```

We can add SPARQL constraints into the database too:
```
$ stardog icv add issues issues-query.sparql
``` 
