package com.complexible.stardog.examples.tinkerpop;

import com.complexible.stardog.Stardog;
import com.complexible.stardog.gremlin.structure.StardogGraph;
import org.apache.tinkerpop.gremlin.process.computer.ComputerResult;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.process.computer.VertexProgram;
import org.apache.tinkerpop.gremlin.process.computer.ranking.pagerank.PageRankVertexProgram;
import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * <p>A basic example using Stardog with the TinkerPop 3 API using the Stardog {@link GraphComputer} implementation
 * to run {@link PageRankVertexProgram}.</p>
 *
 * @author  Edgar Rodriguez-Diaz
 * @since   4.0
 * @version 4.0
 */
public class StardogGraphComputerExample {

	/**
	 * The name of the database we'll be using through the example
	 */
	private static final String LUBM = "lubm";

	/**
	 * The file path of the data we'll load into the database for the example
	 */
	private static final String LUBM_DATA_FILE = "data/University0_0.owl";

	public static void main(String[] args) throws Exception {
		// Initialize the Stardog instance so the embedded server can be used
		Stardog aStardog = Util.loadDataset(LUBM, LUBM_DATA_FILE);

		try {
			/**
			 * Open the TinkerPop3 Graph over the Stardog DB `testTinkerPop3` on its default graph
			 * @see TinkerPop3Example#openGraph(String)
			 */
			try (Graph aTp3Graph = Util.openGraph(LUBM,     // the database where the graph is (in the default graph)
			                                      false,    // no reasoning
			                                      false,    // no cache
			                                      "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#"))  // the base IRI
			{
				/**
				 * Setup the {@link VertexProgram} for the {@link GraphComputer}. In this case we're going to execute
				 * {@link PageRankVertexProgram} which is distributed in the TinkerPop 3 Framework.
				 */
				final VertexProgram<?> aPageRank = PageRankVertexProgram.build()
					                                   .vertexCount(((StardogGraph) aTp3Graph).vertexCount()) // StardogGraph includes a method to get the vertex count
					                                   .iterations(10)              // let's just run the program for 10 iterations
					                                   .create(aTp3Graph);          // create the program with the Graph reference

				/**
				 * Setup the {@link GraphComputer}.
				 */
				final ComputerResult aResult = aTp3Graph.compute()                  // We get the GraphComputer implementation from the Stardog Graph
				                                        .program(aPageRank)         // Assign the PageRankVertexProgram to run
				                                        .result(GraphComputer.ResultGraph.ORIGINAL)     // We'll store the PageRank values in the original Graph
				                                        .submit()                   // submit program to start execution (performed in parallel for each vertex)
				                                        .get();                     // get the result from the Future

				// Once we got the value from the async computation, we can figure out how long it took to run all the iterations
				System.out.println("> Pagerank in this graph took: "+ aResult.memory().getRuntime() +" ms");

				// Access the result graph, in this case since we store PageRank values in the original graph,
				// aResultGraph and aTp3Graph will be the same reference
				try (Graph aResultGraph = aResult.graph()) {

					// We can use a traversal through the Vertices to find out the PageRank values, let's just query 10 of them
					aResultGraph.traversal().V()
					            .valueMap("name", PageRankVertexProgram.PAGE_RANK)      // We'll print the name (ub:name) from the dataset
					            .limit(10)                                              // inspect just 10 entries
					            .forEachRemaining(System.out::println);                 // apply the print out function for each entry
				}
			}
		}
		finally {
			// delete the database and stop Stardog
			Util.cleanup(LUBM);
			aStardog.shutdown();
		}
	}
}
