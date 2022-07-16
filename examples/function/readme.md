# Function Extensibility

The Stardog [com.complexible.stardog.plan.filter.functions.Function](http://docs.stardog.com/javadoc/snarl/com/complexible/stardog/plan/filter/functions/Function.html)
interface is the extension point for section 17.6 (Extensible Value Testing) of the [SPARQL spec](http://www.w3.org/TR/2012/PR-sparql11-query-20121108/#extensionFunctions).

`Function` corresponds to built-in expressions used in `FILTER`, `BIND` and `SELECT` expressions, as well as
aggregate operators in a SPARQL query. Examples include `&&` and `||` and functions defined in the
SPARQL spec like `sameTerm`, `str`, and `now`.

## Implementing Custom Functions

The starting point for implementing your own custom function is to extend [AbstractFunction](http://docs.stardog.com/javadoc/snarl/com/complexible/stardog/plan/filter/functions/AbstractFunction.html).
This class provides much of the basic scaffolding for implementing a new `Function` from scratch.

If your new function falls into one of the existing categories, it should implement the appropriate marker interface:

* `com.complexible.stardog.plan.filter.functions.cast.CastFunction`
* `com.complexible.stardog.plan.filter.functions.datetime.DateTimeFunction`
* `com.complexible.stardog.plan.filter.functions.hash.HashFunction`
* `com.complexible.stardog.plan.filter.functions.numeric.MathFunction`
* `com.complexible.stardog.plan.filter.functions.rdfterm.RDFTermFunction`
* `com.complexible.stardog.plan.filter.functions.string.StringFunction`


If not, then it *must* implement `com.complexible.stardog.plan.filter.functions.UserDefinedFunction`.  Extending one
of these marker interfaces is required for the `Function` to be traverseable via the visitor pattern.

A zero-argument constructor *must* be provided which delegates some initialization to `super`, providing first the `int`
number of required arguments followed by one or more URIs which identify the function.  _Any_ these URIs can be used to
identify the function in a SPARQL query. The URIs are typed as `String` but *should* be valid URIs.

For functions which take take a range of arguments, for example a minimum of 2, but no more than 4 values, a
[Range](http://docs.guava-libraries.googlecode.com/git-history/release/javadoc/com/google/common/collect/Range.html)
can be used as the first parameter passed to `super` rather than an `int`.

`Function` extends from `Copyable`, therefore implementations should also provide a "copy constructor" which can be
called from the `copy` method:

```java

private MyFunc(final MyFunc theFunc) {
    super(myFunc);
    // make copies of any local data structures
}

@Override
public MyFunc copy() {
	return new MyFunc(this);
}
```

Evaluating the function is handled by `Value internalEvaluate(final Value...)` The parameters of this method correspond
to the arguments passed into the function; it's the values of the variables for each solution of the query.  Here we can
perform whatever actions are required for our function.  `AbstractFunction` will have already taken care of validating
that we're getting the correct _number_ of arguments to the function, but we still have to validate the input.
`AbstractFunction` provides some convenience methods to this end, for example `assertURI` and `assertNumericLiteral`
for requiring that inputs are either a valid URI, or a literal with a numeric datatype respectively.

Errors that occur in the evaluation of the function should throw a
`com.complexible.stardog.plan.filter.ExpressionEvaluationException`; this corresponds to the `ValueError` concept
defined in the SPARQL specification.

## Registering your custom Functions with Stardog

Create a file called `com.complexible.stardog.plan.filter.functions.Function` in the `META-INF/services` directory.
The contents of this file should be all of the *fully-qualified* class names for your custom Function(s).  The
jar containing this `META-INF/services` directory as well as the implementations for the Function(s) it references is
added on the classpath. Stardog will pick up the implementations on startup by using the JDK `ServiceLoader` framework.

## Using Custom Functions in a Query

Functions are identified by their URI; you can reference them in a query using their fully-qualified URI, or specify
prefixes for the namespaces and utilize only the qname.  For this example, if the namespace `tag:stardog:api:` is
associated with the prefix `stardog` and within that namespace we have our function `myFunc` we can invoke it
from a SPARQL query as: `bind(stardog:myFunc(?var) as ?tc)`

# Custom Aggregates

While the SPARQL specification has an extension point for value testing and allows for custom functions in
`FILTER`/`BIND`/`SELECT` expressions, there is no similar mechanism for aggregates.  The space of aggregates is closed
by definition, all legal aggregates are enumerated in the spec itself.

However like with custom functions, there are many use cases for creating and using custom aggregate functions.  Stardog
provides a mechanism for creating and using custom aggregates *without* requiring custom SPARQL syntax.

## Implementing a Custom Aggregate

To implement a custom aggregate, you should extend
[AbstractAggregate](http://docs.stardog.com/javadoc/snarl/com/complexible/stardog/plan/aggregates/AbstractAggregate.html).

The rules regarding constructor, "copy constructor" and the `copy` method for `Function` apply to `Aggregate` as well.

Two methods must be implemented for custom aggregates, `Value _getValue() throws ExpressionEvaluationException` and
`void aggregate(final Value theValue, final long theMultiplicity) throws ExpressionEvaluationException`.  `_getValue`
returns the computed aggregate value while `aggregate` adds a Value to the current running aggregation.  In terms of
the `COUNT` aggregate, `aggregate` would increment the counter and `_getValue` would return the final count.

The multiplicity argument to `aggregate` corresponds to the fact that intermediate solution sets have a
multiplicity associated with them.  It's most often 1, but joins and choice of the indexes used for the scans
internally can affect this.  Rather than repeating the solution N times, we associate a multiplicity of N with the
solution.  Again, in terms of `COUNT`, this would mean that rather than incrementing the count by `1`, it would be
incremented by the multiplicity.

## Registering your Custom Aggregate with Stardog

Aggregates such as `COUNT` or `SAMPLE` are implementations of `Function` in the same way `sameTerm` or `str` are and
are registered with Stardog in the exact same manner.


## Using Custom Aggregates in a Query

You can use your custom aggregates just like any other aggregate function.  Assuming we have
a custom aggregate `gmean` defined in the `tag:stardog:api:` namespace, we can refer to it within a query as such:

```sparql
PREFIX : <http://www.example.org>
PREFIX stardog: <tag:stardog:api:>

SELECT (stardog:gmean(?O) AS ?C)
WHERE { ?S ?P ?O }
```
