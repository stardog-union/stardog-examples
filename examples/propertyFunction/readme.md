# Extending the Query Engine

In addition to filter extensibility, Stardog provides a straightforward way to create what are called Property Functions.
These bind to a specific IRI, and when a BGP is encountered in a query that uses a Property Function, the function
is invoked instead of a normal scan of the database. An example of this is Stardog's 
[full-text search feature](http://docs.stardog.com/#_integration_with_sparql):

```sparql
SELECT DISTINCT ?s ?score
WHERE {
  ?s ?p ?l.
  ?l <tag:stardog:api:property:textMatch> 'mac'.
}

```

When the query engine encounters the property `tag:stardog:api:property:textMatch` in the query plan, the BGP it's a 
part of is replaced with the Property Function associated with that IRI. In the above example, the `textMatch` Property
Function will be called and the full-text index will be searched for the string `mac` and all of the results will
be bound to `?l`.

Property Functions can take multiple arguments, and can bind multiple values as output. For this, the standard
SPARQL list syntax is used:

```sparql
(?l ?score) <tag:stardog:api:property:textMatch> ('mac' 0.5 10).
```

In this case, the `textMatch` Property Function is called with a search term of 'mac' as well as the additional 
arguments `0.5` and `10` which correspond to the minimum score and the result limit. The results will bind both the
matching search term to `?l` and the score for the term as `?score`.

## Implementing a Property Function

Your Property Function should implement the `com.complexible.stardog.plan.PropertyFunction` 
[interface](http://docs.stardog.com/java/snarl/com/complexible/stardog/plan/PropertyFunction.html). You must provide
the IRI used to identify your property function via `getURIs` which actually returns a `List<IRI>` so you can have 
aliases for your Property Function.

A builder should be provided via `newBuilder`; the builder is an instance of 
`com.complexible.stardog.plan.PropertyFunctionNodeBuilder` and is responsible for creating a 
`com.complexible.stardog.plan.PlanNode`. `PlanNode` represents an element in a query plan, specifically, 
`com.complexible.stardog.plan.PropertyFunctionPlanNode` corresponds to a node in a query plan for a Property Function.

Finally, the physical operator which actually executes the Property Function is provided via `translate` as an 
implementation of `Operator`.

Additionally, you can override `estimate` and provide cardinality and cost estimations for your Property Function. These
are used by the query optimizer when searching for the best query plan. You can also override `explain` to provide a
custom rendering for the Property Function which is used for rendering query plans.

## QueryTerm

[com.complexible.stardog.plan.QueryTerm](http://docs.stardog.com/java/snarl/com/complexible/stardog/plan/QueryTerm.html)
represents terms within a SPARQL query. A `QueryTerm` can be either a `Constant` or a `Variable`. Variables have a `name`
which corresponds to their index within a Solution 

## Solutions

[com.complexible.stardog.plan.eval.operator.Solution](http://docs.stardog.com/java/snarl/com/complexible/stardog/plan/eval/operator/Solution.html)
represents a current query solution. `Solution` can be thought of an an array of `long` values. Each variable in the 
query has a corresponding `name` which is the index of that variable in the `Solution` where its value can be stored
or retrieved.

## Inputs & Outputs

It is up to the implementation of a Property Function as to whether its subjects are its inputs or outputs, same with 
its objects.

Whichever set of terms serves as the inputs to the property function, one or more of them can be variables which are
expected to be bound by another part of the query. These are enumerated by `PropertyFunctionPlanNode#getInputs` and used
during query optimization. Accurate estimates can be crucial to performance when taking input(s) from another BGP.

## Estimates

Normally core query operators provide very accurate cost and cardinality information to the query optimizer and this 
information is crucial to being able to select the best plan. Since by default Property Functions are somewhat of a 
black box to the optimizer in that the default costing and estimation are likely very inaccurate, queries using
custom Property Functions can be difficult to optimize. Thus it's crucial that implementors provide a best effort
implementation for the `estimate` method on the Property Function.

## Registering your custom Property Functions with Stardog

Create a file called `com.complexible.stardog.plan.PropertyFunction` in the `META-INF/services` directory.
The contents of this file should be all of the *fully-qualified* class names for your custom Property Function(s).  The
jar containing this `META-INF/services` directory as well as the implementations for the Property Function(s) it references 
should then be added to the classpath. Stardog will pick up the implementations on startup by using the 
JDK `ServiceLoader` framework.

