package com.stardog.examples;

import com.complexible.common.base.CloseableIterator;
import com.complexible.stardog.ext.spring.ConnectionCallback;
import com.complexible.stardog.ext.spring.SnarlTemplate;
import com.complexible.stardog.ext.spring.mapper.SimpleRowMapper;
import com.stardog.stark.Values;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.api.reasoning.ReasoningConnection;
import com.complexible.stardog.api.search.SearchConnection;
import com.complexible.stardog.api.search.SearchResult;
import com.complexible.stardog.api.search.SearchResults;
import com.complexible.stardog.api.search.Searcher;
import com.complexible.stardog.reasoning.ProofWriter;
import com.complexible.stardog.reasoning.StardogExplainer;
import com.stardog.stark.query.SelectQueryResult;
import com.stardog.stark.query.BindingSet;
import com.stardog.stark.Literal;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

@Configuration
public class StardogSpringClient {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        SnarlTemplate snarlTemplate = (SnarlTemplate) context.getBean("template");

        if (snarlTemplate != null) {

            // Query to run
            String sparql = "PREFIX foaf:<http://xmlns.com/foaf/0.1/> " +
                    "select * { ?s rdf:type foaf:Person }";

            // Queries the database using the SnarlTemplate and gets back a list of mapped objects
            List<Map<String, String>> results = snarlTemplate.query(sparql, new SimpleRowMapper());

            // Prints out the results
            System.out.println("** Members of Marvel Universe **");
            results.forEach(item -> item.forEach((k,v) -> System.out.println(v)));
            System.out.println("\n** Ask the database questions **");

            // Using the SnarlTemplate, you can ask the database questions. In the ontology, just because
            // you know someones does not imply that they know you.
            String askQuery = " PREFIX foaf:<http://xmlns.com/foaf/0.1/>" +
                    "ASK { :ironMan foaf:knows :spiderMan } ";
            System.out.println("Does Iron Man know Spiderman? " + snarlTemplate.ask(askQuery));

            askQuery = " PREFIX foaf:<http://xmlns.com/foaf/0.1/>" +
                    "ASK { :spiderMan foaf:knows :ironMan } ";
            System.out.println("Does Spiderman know IronMan? " + snarlTemplate.ask(askQuery));


            System.out.println("\n** Show Reasoning **");
            // Queries the database with reasoning off. With reasoning off, there would be
            // no connection between Spider-Man and his mother, Mary Parker, since the triple
            // is only associated with his mother
            boolean aExistsNoReasoning = snarlTemplate
                    .reasoning(false)
                    .subject(Values.iri("http://api.stardog.com/spiderMan"))
                    .predicate(Values.iri("http://api.stardog.com/childOf"))
                    .object(Values.iri("http://api.stardog.com/maryParker"))
                    .ask();


            System.out.println("aExistsNoReasoning: " + aExistsNoReasoning);

            // Queries the database with reasoning on. There is now a connection between
            // Spider-Man and his mother, Mary Parker. This locial connection exists since
            // there is a triple associated with Mary Parker and the ontology says that
            // :childOf is the inverse of :parentOf.
            boolean aExistsReasoning = snarlTemplate
                    .reasoning(true)
                    .subject(Values.iri("http://api.stardog.com/spiderMan"))
                    .predicate(Values.iri("http://api.stardog.com/childOf"))
                    .object(Values.iri("http://api.stardog.com/maryParker"))
                    .ask();

            System.out.println("aExistsReasoning: " + aExistsReasoning);


            // Using reasoning, we can get an explanation on how the inferred connection between the
            // objects was made. Here we are going to send the same query to the reasoning and get
            // an explanation back as to how they were connected and why the results were returned.
            System.out.println("\n** Show Inference **");
            StardogExplainer aExplanation = snarlTemplate
                    .as(ReasoningConnection.class)
                    .explain(Values.statement(
                                    Values.iri("http://api.stardog.com/spiderMan"),
                                    Values.iri("http://api.stardog.com/childOf"),
                                    Values.iri("http://api.stardog.com/maryParker")));


            System.out.println("Explain inference: ");
            System.out.println(ProofWriter.toString(aExplanation.proof()));

            // Full text search has the ability to do exactly that. Search the database for a specific value.
            // Here we will specify that we only want results over a score of `0.5`, and no more than `2` results
            // for things that match the search term `man`. Below we will perform the search in two different ways.
            snarlTemplate.execute(new ConnectionCallback<Boolean>() {
                @Override
                public Boolean doWithConnection(Connection connection) {
                    try {
                        // Stardog's full text search is backed by [Lucene](http://lucene.apache.org)
                        // so you can use the full Lucene search syntax in your queries.
                        Searcher aSearch = connection
                                .as(SearchConnection.class)
                                .search()
                                .limit(2)
                                .query("man")
                                .threshold(0.5);

                        // We can run the search and then iterate over the results
                        SearchResults aSearchResults = aSearch.search();

                        try (CloseableIterator<SearchResult> resultIt = aSearchResults.iterator()) {
                            System.out.println("\nAPI results: ");
                            while (resultIt.hasNext()) {
                                SearchResult aHit = resultIt.next();

                                System.out.println(aHit.getHit() + " with a score of: " + aHit.getScore());
                            }
                        }

                        // The SPARQL syntax is based on the LARQ syntax in Jena.  Here you will
                        // see the SPARQL query that is equivalent to the search we just did via `Searcher`,
                        // which we can see when we print the results.
                        String aQuery = "SELECT DISTINCT ?s ?score WHERE {\n" +
                                "\t?s ?p ?l.\n" +
                                "\t( ?l ?score ) <" + SearchConnection.MATCH_PREDICATE + "> ( 'man' 0.5 2 ).\n" +
                                "}";

                        SelectQuery query = connection.select(aQuery);

                        try (SelectQueryResult aResult = query.execute()) {
                            System.out.println("Query results: ");
                            while (aResult.hasNext()) {
                                BindingSet result = aResult.next();

                                result.value("s").ifPresent(s -> System.out.println(s + result.literal("score").map(score -> " with a score of: " + Literal.doubleValue(score)).orElse("")));
                            }
                        }

                    } catch (StardogException e) {
                        System.out.println("Error with full text search: " + e);
                        return false;
                    }
                    return true;
                }
            });
        }
    }
}
