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
package com.complexible.stardog.plan.aggregates;

import java.math.BigDecimal;
import java.util.List;

import com.complexible.stardog.plan.filter.Expression;
import com.complexible.stardog.plan.filter.expr.ValueOrError;
import com.complexible.stardog.plan.filter.functions.numeric.Multiply;
import com.complexible.stardog.plan.filter.functions.numeric.Root;
import com.google.common.base.Preconditions;
import com.stardog.stark.Literal;
import com.stardog.stark.Namespaces;
import com.stardog.stark.Value;

import static com.stardog.stark.Values.literal;

/**
 * <p>Implementation of a custom aggregate for calculating the geometric mean of the input values.</p>
 *
 * @author  Michael Grove
 * @since   3.0
 * @version 5.0
 */
public final class GMean extends AbstractAggregate {
	private Root mRoot;
	private Multiply mProduct;

	private Count mCount;

	private Value mCurr = null;

	public GMean() {
		super(Namespaces.STARDOG + "gmean");

		mCount = new Count();
		mRoot = new Root();
		mProduct = new Multiply();
	}

	GMean(final GMean theAgg) {
		super(theAgg);

		mCount = new Count();
		mRoot = new Root();
		mProduct = new Multiply();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		super.initialize();

		mCount.initialize();
		mRoot.initialize();
		mProduct.initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setArgs(final List<Expression> theArgs) {
		Preconditions.checkArgument(theArgs.size() == 1, "Geometric mean aggregate function takes only one argument, %d found",
		                            theArgs.size());

		super.setArgs(theArgs);

		mCount.setArgs(theArgs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ValueOrError _getValue() {
		if (mCurr == null) {
			return ValueOrError.BigDecimal.of(new BigDecimal(0));
		}
		else {
			return mRoot.evaluate(ValueOrError.General.of(mCurr), mCount.get());
		}
	}

	@Override
	protected ValueOrError aggregate(final long theMultiplicity, final Value theValue, final Value... theOtherValues) {

		mCount.aggregate(theMultiplicity, theValue);

		if (mCurr == null) {
			mCurr = theValue;
		}
		else {
			mCurr = mProduct.evaluate(theMultiplicity == 1
					? theValue :
					mProduct.evaluate(theValue, literal(theMultiplicity), mCurr).value()).value();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GMean copy() {
		return new GMean(this);
	}

}

