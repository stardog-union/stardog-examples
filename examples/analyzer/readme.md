# Custom Analyzers

By default, the full-text index in Stardog uses Lucene's
[StandardAnalyzer](https://lucene.apache.org/core/4_7_2/analyzers-common/org/apache/lucene/analysis/standard/StandardAnalyzer.html).

However, any class implementing `org.apache.lucene.analysis.Analyzer` can be used in place of the default analyzer. To
specify a different `Analyzer` a service named `com.complexible.stardog.search.AnalyzerFactory` should be registered.
`AnalyzerFactory` returns the desired `Analyzer` implementation to be used when creating the Lucene index from the
RDF contained in the database.

This is an example of an AnalyzerFactory which uses the built-in Lucene analyzer for the French language:

```java
public final class FrenchAnalyzerFactory implements AnalyzerFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analyzer get() {
		return new FrenchAnalyzer(Version.LUCENE_47);
	}
}
```


Any of the [common Lucene analyzers](https://lucene.apache.org/core/4_7_2/analyzers-common/index.html) can be used as
well as any custom implementation of `Analyzer`.  In the latter case, be sure your implementation is in Stardog's
class path.

Create a file called `com.complexible.stardog.search.AnalyzerFactory` in the `META-INF/services` directory.
The contents of this file should be the *fully-qualified* class name of your `AnalyzerFactory`.  The
jar containing this `META-INF/services` directory as well as the implementation for the class it references is then
added on the classpath. Stardog will pick up the implementation on startup by using the JDK `ServiceLoader` framework.

Note, as of Stardog 3.0, only *one* `AnalyzerFactory` can be registered at a time, attempts to register more than one
will yield errors on startup.
