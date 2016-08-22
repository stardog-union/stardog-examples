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

import java.io.IOException;
import java.util.List;

import com.complexible.common.io.ByteReader;
import com.complexible.common.io.ByteWriter;
import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.index.memory.ValueSerializer;
import com.complexible.stardog.plan.filter.Expression;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;
import com.complexible.stardog.plan.filter.functions.numeric.Multiply;
import com.complexible.stardog.plan.filter.functions.numeric.Root;
import com.google.common.base.Preconditions;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import static com.complexible.common.rdf.model.Values.literal;

/**
 * <p>Implementation of a custom aggregate for calculating the geometric mean of the input values.</p>
 *
 * @author  Michael Grove
 * @since   3.0
 * @version 3.0
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

	protected GMean(final GMean theAgg) {
		super(theAgg);

		mCount = new Count();
		mRoot = new Root();
		mProduct = new Multiply();
	}

	// for deserialization purposes only
	private GMean(final boolean theDistinct, final List<Expression> theArgs) {
		super(theDistinct, theArgs, "SAMPLE");

		mRoot = new Root();
		mProduct = new Multiply();

		mRoot.initialize();
		mProduct.initialize();
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
	protected Value _getValue() throws ExpressionEvaluationException {
		if (mCurr == null) {
			return literal("0D");
		}
		else {
			return mRoot.evaluate(mCurr, mCount.get());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void aggregate(final Value theValue, final long theMultiplicity) throws ExpressionEvaluationException {
		if (!(theValue instanceof Literal)) {
			throw new ExpressionEvaluationException("Invalid argument to " + getName() + " argument MUST be a literal value, was: " + theValue);
		}

		mCount.aggregate(theValue, theMultiplicity);

		if (mCurr == null) {
			mCurr = theValue;
		}
		else {
			mCurr = mProduct.evaluate(theMultiplicity == 1
			                          ? theValue
			                          : mProduct.evaluate(theValue, literal(theMultiplicity)), mCurr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GMean copy() {
		return new GMean(this);
	}

	@Override
	public AggregateSerializer getSerializer() {
		return new AbstractAggregateSerializer() {

			private final ValueSerializer mValueSerializer = new ValueSerializer();

			@Override
			public int sizeOf(final Aggregate theAgg) {
				GMean aAgg = (GMean) theAgg;

				return baseSize(aAgg) + (aAgg.mFailed ? 0 : (aAgg.mCount.getSerializer().sizeOf(aAgg.mCount) + valueSize(aAgg.mCurr, mValueSerializer)));
			}

			@Override
			public Aggregate read(final ByteReader theReader) throws IOException {
				Aggregate aAgg = super.read(theReader, theDistinct -> new GMean(theDistinct, getArgs()));

				if (aAgg instanceof GMean) {
					((GMean) aAgg).mCount = (Count) mCount.getSerializer().read(theReader);
					((GMean) aAgg).mCurr = readValue(theReader, mValueSerializer);
				}

				return aAgg;
			}

			@Override
			public void write(final Aggregate theObj, final ByteWriter theWriter) throws IOException {
				GMean aAgg = (GMean) theObj;

				super.write(theObj, theWriter);

				if (!aAgg.mFailed) {
					mCount.getSerializer().write(aAgg.mCount, theWriter);
					writeValue(aAgg.mCurr, theWriter, mValueSerializer);
				}
			}

			@Override
			public boolean supportsNull() {
				return false;
			}

		};
	}
}

