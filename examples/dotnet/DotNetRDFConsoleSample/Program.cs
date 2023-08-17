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
                stardog.CreateStore(template);
            }

            using (StardogConnector stardogConn = new StardogConnector(SERVER_URL, DATABASE_NAME, STARDOG_USERNAME, STARDOG_PASSWORD))
            {
                // make a triple
                Graph g = new Graph();
                INode s = g.CreateBlankNode();
                INode p = g.CreateUriNode(new Uri(RdfSpecsHelper.RdfType));
                INode o = g.CreateUriNode(new Uri("http://example.org/Example"));
                Triple t = new Triple(s, p, o);

                // add it to a graph in the store
                if (stardogConn.UpdateSupported)
                {
                    // UpdateGraph takes lists of Triples to add or remove (or null if you do not want to perform that operation)
                    stardogConn.UpdateGraph("http://example.org/graph", new Triple[] { t }, null);
                }
                else
                {
                    throw new Exception("Store does not support triple level updates");
                }

                // ** OR **

                // load from a file:

                //Graph g = new Graph();
                //g.LoadFromFile("example.rdf");

                //// Set its BaseUri property to the graph URI
                //g.BaseUri = new Uri("http://example.org/graph");

                //// write it to the store
                //if (!stardogConn.IsReadOnly)
                //{
                //    stardogConn.SaveGraph(g);
                //}

                // Run a SPARQL query
                object results = stardogConn.Query("SELECT * WHERE { ?s ?p ?o } LIMIT 100");
                if (results is SparqlResultSet)
                {
                    SparqlResultSet rset = (SparqlResultSet)results;
                    foreach (SparqlResult r in rset)
                    {
                        Console.WriteLine("RESULT: {0}", r.ToString());
                    }
                }
                else
                {
                    throw new Exception("Result was not a SPARQL result set");
                }

                // List the graphs in the store
                if (stardogConn.ListGraphsSupported)
                {
                    // print the graph URIs
                    foreach (Uri u in stardogConn.ListGraphs())
                    {
                        Console.WriteLine("GRAPH: {0}", u.ToString());
                    }
                }
                else
                {
                    throw new Exception("Store does not support listing graphs");
                }

                // delete the triple we added earlier
                if (stardogConn.UpdateSupported)
                {
                    stardogConn.UpdateGraph("http://example.org/graph", null, new Triple[] { t });
                }
                else
                {
                    throw new Exception("Store does not support triple level updates");
                }

            }

        }
       
    }
}
    
