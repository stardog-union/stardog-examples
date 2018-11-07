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

package com.complexible.stardog.examples.functions;

import com.complexible.common.base.Strings2;
import com.complexible.stardog.plan.filter.expr.ValueOrError;
import com.complexible.stardog.plan.filter.functions.AbstractFunction;
import com.complexible.stardog.plan.filter.ExpressionVisitor;
import com.complexible.stardog.plan.filter.functions.string.StringFunction;
import com.stardog.stark.Datatype;
import com.stardog.stark.Literal;
import com.stardog.stark.Namespaces;
import com.stardog.stark.Value;

import static com.stardog.stark.Values.literal;

/**
 * <p>Example for creating your own SPARQL filter function</p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 1.0
 *
 * @see <a href="http://www.w3.org/TR/2012/PR-sparql11-query-20121108/#extensionFunctions">Extension Functions</a>
 */
public final class TitleCase extends AbstractFunction implements StringFunction {

	// ## Initializing a Function
	//
	// This implementation extends from [AbstractFunction](http://docs.stardog.com/java/snarl/com/complexible/stardog/plan/filter/functions/AbstractFunction.html)
	// which takes care of much of the work of creating a custom function.  We're passing in that our new function
	// `TitleCase` takes a single argument, and that it's name is tag:stardog:api:titleCase.  Note that names
	// should be URIs.
	//
	// Functions can take a range of arguments, regex is an example, in which case the first argument is the valid
	// [Range](http://docs.guava-libraries.googlecode.com/git-history/release/javadoc/com/google/common/collect/Range.html)
	// for the number of arguments.
	//
	// Functions must always have at least one name, again, which should be a URI.  But you can provide multiple
	// URIs as the names for the function and it will be available using any of those names.  The name is how
	// the function is referred to in the sparql query.
	//
	// For this example, if the namespace `tag:stardog:api:` is associated with the prefix `stardog` we can call
	// this function from a SPARQL query: `bind(stardog:titleCase(?var) as ?tc)`
	protected TitleCase() {

		super(1 /* takes a single argument */, Namespaces.STARDOG+"titleCase");
	}

	private TitleCase(final TitleCase theExpr) {
		super(theExpr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TitleCase";
	}

	// # Filter Evaluation
	//
	// Here is where we can evaluate the function.  The parameters of this method correspond to the arguments
	// passed into the filter; it's the values of the variables for each solution of the query.  Here we can
	// perform whatever actions are required for our filter.  `AbstractFunction` will have already taken care
	// of validating that we're getting the correct number of arguments to the function, but we still have to
	// validate the input.
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ValueOrError internalEvaluate(final Value... theArgs) {

		// Verify that the single input argument is a plain literal, or an xsd:string.
		assertStringLiteral(theArgs[0]);

		// We know that we have a string, so let's just title case it and return it.
		return ValueOrError.General.of(literal(Strings2.toTitleCase(((Literal)theArgs[0]).label()), Datatype.STRING));
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(final ExpressionVisitor theVisitor) {
		theVisitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TitleCase copy() {
		return new TitleCase(this);
	}
}
