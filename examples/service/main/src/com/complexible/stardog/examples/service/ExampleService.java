/*
 * Copyright (c) 2010 - 2017, Stardog Union. <http://www.stardog.com>
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

package com.complexible.stardog.examples.service;

import java.io.InputStream;

import com.complexible.common.base.Strings2;
import com.complexible.stardog.plan.PlanNode;
import com.complexible.stardog.plan.PlanVarInfo;
import com.complexible.stardog.plan.eval.ExecutionContext;
import com.complexible.stardog.plan.eval.operator.Operator;
import com.complexible.stardog.plan.eval.operator.OperatorException;
import com.complexible.stardog.plan.eval.operator.SolutionIterator;
import com.complexible.stardog.plan.eval.service.ResultsToSolutionIterator;
import com.complexible.stardog.plan.eval.service.Service;
import com.complexible.stardog.plan.eval.service.ServiceQuery;
import com.complexible.stardog.plan.util.SPARQLRenderer;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openrdf.model.IRI;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;

/**
 * Example SERVICE implementation. A SPARQL service has a SPARQL query string and a service IRI associated with it. A service implementation reports if it
 * {@link #canEvaluate(IRI) can evaluate} a service IRI. The execution engine will pick the first registered service that can evaluate the service IRI. The
 * order of service implementations is arbitrary so this example uses a unique IRI scheme {@value EXAMPLE_SCHEME}. This implementation simply executes the
 * SPARQL query over HTTP. It can be customized to use additional HTTP headers or query parameters.
 *
 * NOTE: This example does not send authentication headers so Stardog needs to be started with the {@code --disable-security} option for the example to
 * work. The example can be modified easily to send authentication information.
 *
 * @author  Evren Sirin
 */
final class ExampleService implements Service {
	public static final String EXAMPLE_SCHEME = "example://";

	// we will use just the SPARQL/XML result format
	private static final TupleQueryResultFormat FORMAT = TupleQueryResultFormat.SPARQL;

	/**
	 * Returns whether or not this instance can a SERVICE query against the given IRI.
	 *
	 * @param theIRI    the IRI of the service
	 * @return          {@code true} if this instance can {@code #evaluate} the service, {@code false}  otherwise
	 */
	@Override
	public boolean canEvaluate(final IRI theIRI) {
		return theIRI.stringValue().startsWith(EXAMPLE_SCHEME);
	}

	/**
	 * Create a query which evaluates the service over HTTP.
	 *
	 * @param theIRI the IRI of the service call
	 * @param thePlanNode the SPARQL body of the service call
	 * @return a {@link ServiceQuery} to evaluate the service call
	 */
	@Override
	public ServiceQuery createQuery(final IRI theIRI, final PlanNode thePlanNode) {
		return new ServiceQuery(theIRI, thePlanNode) {
			@Override
			public SolutionIterator evaluate(final ExecutionContext theContext, final Operator theOperator, final PlanVarInfo theVarInfo) throws OperatorException {
				// convert the service IRI to an http:// IRI
				String aServiceIRI = serviceTerm().getValue().stringValue().replace(EXAMPLE_SCHEME, "http://");

				// retrieve the SPARQL string for the service
				String aQueryStr = SPARQLRenderer.renderSelect(body(), theVarInfo, true);

				// create an HTTP request
				final HttpGet aGet = createRequest(aServiceIRI, aQueryStr);

				// execute the HTTP request
				try (CloseableHttpClient aClient = HttpClientBuilder.create().build();
				     CloseableHttpResponse aResponse = aClient.execute(aGet)) {

					final int aResponseCode = aResponse.getStatusLine().getStatusCode();

					// check if the request failed
					if (aResponseCode != HttpStatus.SC_OK) {
						throw new OperatorException("SERVICE evaluation returned HTTP response code " + aResponseCode);
					}

					try (InputStream aServiceResults = aResponse.getEntity().getContent()) {
						// parse the query results
						TupleQueryResult aTupleQueryResult = QueryResultIO.parseTuple(aServiceResults, FORMAT);

						// convert the results to a solution iterator so it can be processed by the execution engine, e.g. joined with the rest of the queyr results
						return new ResultsToSolutionIterator(theContext, aTupleQueryResult, theVarInfo, body().getAllVars());
					}
				}
				catch (Exception e) {
					throw new OperatorException(e);
				}
			}
		};
	}

	private HttpGet createRequest(final String theService, final String theQueryStr) {
		final String aURLStr = theService + "?query=" + Strings2.urlEncode(theQueryStr);

		HttpGet aGet = new HttpGet(aURLStr);

		aGet.setHeader("Accept", FORMAT.getDefaultMIMEType());

		return aGet;
	}
}
