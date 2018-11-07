# Docs Examples

## WordCountExtractor

This is an RDF extractor that processes documents to extract an RDF
model about the document. The extractor is called when a document is
added or updated in the document store.

RDF extractors must be registered using the service loader. Check the
main/resources/META-INF/services for how this is done.

RDF extractors can be configured for each DB with the property
"docs.default.rdf.extractors". This is a comma separated list of extractors
that can consist of the following:

* A fully qualified class name of an RDF extractor registered via the service loader
* A simple class name of an RDF extractor registered via the service loader
* The string "tika" to enable the Apache Tika metadata extractor which reads embedded metadata

RDF extractors can be also be chosen for each `put()` request by
providing a sequence of extractor names to the appropriate version of
the `put()` method on a `BitesConnection`.

## Usage

- Run `gradle clean jar`
- In build\libs there should now be a `docs-x.y.z.jar`
- Copy that jar into [$STARDOG_EXT](https://www.stardog.com/docs/#_extending_stardog) or somewhere such as `$STARDOG\server\dbms` that is on your Stardog classpath
- Restart your Stardog server
- `WordCountExtractor` will be available as an RDF extractor

See `WordCountExtractorTest.java` for an API usage example.
