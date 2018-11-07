package com.complexible.stardog.examples.tinkerpop;

import java.util.Calendar;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.Stardog;
import com.google.common.collect.ImmutableList;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.openrdf.model.IRI;
import org.openrdf.model.vocabulary.RDF;

/**
 * <p>A basic example using Stardog with the TinkerPop 3 API with Stardog Rules Reasoning.</p>
 *
 * @author  Edgar Rodriguez-Diaz
 * @since   4.0
 * @version 4.0
 */
public final class TinkerPop3Example {

	/**
	 * The name of the database we'll be using through the example
	 */
	private static final String TP3_DB = "testTinkerPop3";

	private static final String TP3_DATA_FILE = "data/vehicles.trig";

	public static void main(final String[] args) throws Exception {
		// Initialize the Stardog instance so the embedded server can be used
		Stardog aStardog = Util.loadDataset(TP3_DB, TP3_DATA_FILE);

		try {
			/**
			 * Open the TinkerPop3 Graph over the Stardog DB `testTinkerPop3` on its default graph
			 * @see TinkerPop3Example#openGraph(String)
             */
			try (Graph aTp3Graph = Util.openGraph(TP3_DB,
			                                      true /* reasoning enabled */,
			                                      true /* cache */,
			                                      "http://myvehicledata.com/" /* base IRI */)) {
				// Create a reusable graph traversal source.
				final GraphTraversalSource g = aTp3Graph.traversal();

				final IRI aYearModelKey = Values.iri("http://example.org/vehicles/yearModel");
				final IRI aOldModelClass = Values.iri("http://example.org/vehicles/OldModel");
				final IRI aLatestModelClass = Values.iri("http://example.org/vehicles/LatestModel");

				// Traverse the vertices of this graph, they're just 9 vertices
				for (Vertex aVertex : g.V().toList()) {
					// Get the `http://example.org/vehicles/yearModel` property if the vertex has it
					Property<Integer> aYearModel = aVertex.property(aYearModelKey.stringValue());

					if (aYearModel != VertexProperty.<Integer>empty()) {

						// Comparing the year model with today's date, we can find out if the car is an old model
						// or a latest model, according to the stardog rules in the dataset.
						final int yearNow = Calendar.getInstance().get(Calendar.YEAR);
						if (yearNow - aYearModel.value() > 10) {

							// Looks like this car is more than 10 years old, so according to the stardog rules, the
							// Stardog reasoner should've applied the class <http://example.org/vehicles/OldModel> to this
							// individual (vertex).
							hasClassAndPrint(g, aOldModelClass, aVertex);
						}
						else if (yearNow - aYearModel.value() == 0) {
							// Looks like this car's model is the latest year, so according to the stardog rules, the
							// Stardog reasoner should've applied the class <http://example.org/vehicles/LatestModel> to this
							// individual (vertex).
							hasClassAndPrint(g, aLatestModelClass, aVertex);
						}
						else {
							// Not too old but not the latest model for this car (individual/vertex)
							System.out.println("------------");
							System.out.println(String.format("Car <%s> is not that old yet.",
							                                 aVertex.id()));
							System.out.println("------------");
						}
					}
				}
			}
		}
		finally {
			// delete the database and stop Stardog
			Util.cleanup(TP3_DB);
			aStardog.shutdown();
		}
	}

	/**
	 * Just print if the car is member of the given class or not.
	 *
	 * @param theG          the Graph traversal source
	 * @param theClass      the Class to look for membership
	 * @param theVertex     the vertex to test for membership on class `theClass`
	 * @throws Exception    if there's an error while doing the check/print out
	 */
	private static void hasClassAndPrint(final GraphTraversalSource theG,
	                                     final IRI theClass, final Vertex theVertex) throws Exception {
		final IRI aVertexId = (IRI) theVertex.id();
		final boolean hasClass = getClassMemebers(theG, theClass).contains(aVertexId);

		final String isOrNot = (hasClass) ? "is"
		                                  : "is not";

		System.out.println("------------");
		System.out.println(String.format("Car <%s> %s <%s>",
		                                 aVertexId.stringValue(),
		                                 isOrNot,
		                                 theClass));
		System.out.println("------------");
	}

	/**
	 * Gets the members of a owl:Class using a Gremlin Traversal. In order to be a member of a Class, a Vertex should have
	 * an out-edge with label rdf:type to the owl:Class.
	 * From the point of view of an owl:Class, to get all elements who are members of that class, we just need to do a
	 * traversal of the in-edges to the specific owl:Class to query, with label {@value RDF#NAMESPACE}:type,
	 * finding the vertices pointing at it as the source of those in-edges.
	 *
	 * @param theG          the Graph traversal source
	 * @param theClass      the Class to look for membership
	 * @return              the Collection of IRI for the members of the given Class
	 * @throws Exception    if there's an error while doing the traversal
	 */
	private static Collection<IRI> getClassMemebers(final GraphTraversalSource theG, final IRI theClass) throws Exception {
		final ImmutableList.Builder<IRI> aFoundInd = ImmutableList.builder();

		// Traverse the - in-edges source vertices - of Vertex with Id `theClass` having label rdf:type
		GraphTraversal<Vertex, Vertex> aIndividuals = theG.V(theClass)
		                                                  .in(RDF.TYPE.stringValue());

		// Iterate and get the source vertices
		try (Stream<Vertex> aVertexStream = aIndividuals.toStream()) {
			aFoundInd.addAll(aVertexStream.map(aVertex -> (IRI) aVertex.id())
			                              .collect(Collectors.toList()));
		}

		return aFoundInd.build();
	}
}
