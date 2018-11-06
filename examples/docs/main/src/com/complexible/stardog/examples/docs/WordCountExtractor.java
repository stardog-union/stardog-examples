/*
 * Copyright (c) 2010-2016 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.stardog.examples.docs;

import java.io.BufferedReader;
import java.io.Reader;

import com.complexible.common.rdf.StatementSource;
import com.complexible.common.rdf.impl.MemoryStatementSource;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.docs.extraction.tika.TextProvidingRDFExtractor;
import com.google.common.collect.ImmutableSet;
import com.stardog.stark.IRI;
import com.stardog.stark.Statement;
import com.stardog.stark.Values;

/**
 * A Stardog RDF extractor that will process the document to compute
 * a word count. The word count is asserted as an RDF statement.  <p/>
 * This extractor inherits from {@link TextProvidingRDFExtractor}
 * which directly provides the text in the form of a Reader. The
 * document can be accessed directly by implementing {@link
 * com.complexible.stardog.docs.extraction.RDFExtractor}. In this case
 * the extractor is responsible for dealing with the format of the
 * file (pdf, docx, etc).
 */
public class WordCountExtractor extends TextProvidingRDFExtractor {

	/**
	 * Compute the word count, create an RDF triple linking the word count to the document, return it as a graph.
	 */
	@Override
	protected StatementSource extractFromText(final Connection theConnection, final IRI theDocIri, final Reader theText) throws Exception {
		int words = 0;
		String line;
		BufferedReader aBufferedReader = new BufferedReader(theText);

		while ((line = aBufferedReader.readLine()) != null) {
			words += line.split(" ").length;
		}

		Statement aWordCountStatement = Values.statement(theDocIri, Values.iri("tag:stardog:example:wordcount"), Values.literal(words));

		return MemoryStatementSource.of(ImmutableSet.of(aWordCountStatement));
	}

}
