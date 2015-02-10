package com.complexible.stardog.examples.analyzer;

import com.complexible.stardog.search.AnalyzerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.util.Version;

/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   3.0
 * @version 3.0
 */
public final class FrenchAnalyzerFactory implements AnalyzerFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analyzer get() {
		return new FrenchAnalyzer(Version.LUCENE_47);
	}
}
