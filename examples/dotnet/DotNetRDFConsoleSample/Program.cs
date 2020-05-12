using System;
using System.Linq;
using VDS.RDF;
using VDS.RDF.Parsing;
using VDS.RDF.Query;
using VDS.RDF.Storage;
using VDS.RDF.Storage.Management;
using VDS.RDF.Storage.Management.Provisioning;

namespace DotNetRDFConsoleSample
{
    class Program
    {
        static void Main(string[] args)
        {
            // This will print out basic traces of HTTP requests and responses to the Console 
            Options.HttpDebugging = true;

            const string SERVER_URL = "http://localhost:5820";
            const string STARDOG_USERNAME = "admin";
            const string STARDOG_PASSWORD = "admin";
            const string DATABASE_NAME = "MyDB";

            // using will automatically call Dispose() for us
            using (StardogServer stardog = new StardogServer(SERVER_URL, STARDOG_USERNAME, STARDOG_PASSWORD))
            {

                // if the database already exists, delete it
                if (stardog.ListStores().Contains(DATABASE_NAME))
                {
                    stardog.DeleteStore(DATABASE_NAME);
                }

                IStoreTemplate template = stardog.GetDefaultTemplate(DATABASE_NAME);
                Console.WriteLine("Template ID " + template.ID);

                stardog.CreateStore(template);
            }

            using (StardogConnector stardogConn = new StardogConnector(SERVER_URL, DATABASE_NAME, STARDOG_USERNAME, STARDOG_PASSWORD))
            {
                // Construct the Triple we wish to add
                Graph g = new Graph();
                INode s = g.CreateBlankNode();
                INode p = g.CreateUriNode(new Uri(RdfSpecsHelper.RdfType));
                INode o = g.CreateUriNode(new Uri("http://example.org/Example"));
                Triple t = new Triple(s, p, o);

                // Now add a Triple to a Graph in the Store
                if (stardogConn.UpdateSupported)
                {
                    // UpdateGraph takes enumerables of Triples to add/remove or null to indicate none
                    // Hence why we create a Triple array to pass in the Triple to be added
                    stardogConn.UpdateGraph("http://example.org/graph", new Triple[] { t }, null);
                }
                else
                {
                    throw new Exception("Store does not support triple level updates");
                }

                // OR load from a file:

                //Graph g = new Graph();
                //g.LoadFromFile("example.rdf");

                //// Set its BaseUri property to the URI we want to save it as
                //g.BaseUri = new Uri("http://example.org/graph");

                //// Now save it to the store
                //if (!stardogConn.IsReadOnly)
                //{
                //    stardogConn.SaveGraph(g);
                //}

                // Make a SPARQL Query against the store
                object results = stardogConn.Query("SELECT * WHERE { GRAPH ?g { ?s ?p ?o } } LIMIT 100");
                if (results is SparqlResultSet)
                {
                    //Print the results
                    SparqlResultSet rset = (SparqlResultSet)results;
                    foreach (SparqlResult r in rset)
                    {
                        Console.WriteLine("RESULT: {0}", r.ToString());
                    }
                }
                else
                {
                    throw new Exception("Did not get a SPARQL Result Set as expected");
                }

                // List the Graphs
                // Making sure that we check this feature is supported first
                if (stardogConn.ListGraphsSupported)
                {
                    //Iterate over the Graph URIs and print them
                    foreach (Uri u in stardogConn.ListGraphs())
                    {
                        Console.WriteLine("GRAPH: {0}", u.ToString());
                    }
                }
                else
                {
                    throw new Exception("Store does not support listing graphs");
                }

                // Now delete the Triple we added earlier in the Store
                if (stardogConn.UpdateSupported)
                {
                    stardogConn.UpdateGraph("http://example.org/graph", null, new Triple[] { t });
                }
                else
                {
                    throw new Exception("Store does not support triple level updates");
                }

            }

            Console.ReadLine();

        }

       
    }
}
    
