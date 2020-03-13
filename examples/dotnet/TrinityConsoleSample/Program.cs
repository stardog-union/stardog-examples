/*
 * Copyright (c) 2010 - 2019, Stardog Union. <http://www.stardog.com>
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
using System;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Reflection;
using Semiodesk.Trinity;
using Semiodesk.Trinity.Store;
using TrinityConsoleSample.ObjectModels;
using VDS.RDF;

namespace TrinityConsoleSample
{
    class Program
    {

        /// <summary>
        /// The model we are working on
        /// </summary>
        static IModel Model { get; set; }


        static void Main(string[] args)
        {
            // TODO: Change the connection string for your Stardog instance's host, port and credentials
            const string connectionString = "provider=stardog;host=http://localhost:5820;uid=admin;pw=admin;sid=music";

            OntologyDiscovery.AddAssembly(Assembly.GetExecutingAssembly());
            MappingDiscovery.RegisterCallingAssembly();

            // dotNetRDF HTTP call debugging (will print to STDOUT)

            //Options.HttpDebugging = true;

            // NOTE: Note: When full debugging is used the HTTP response stream is consumed,
            // this may cause dotNetRDF to throw different errors to those normally seen because
            // the stream the code expects has already been consumed.

            //Options.HttpFullDebugging = true;

            Stopwatch watch = Stopwatch.StartNew();

            // Load the stardog store provider
            StoreFactory.LoadProvider<StardogStoreProvider>();

            // Connect to the stardog store
            IStore store = StoreFactory.CreateStore(connectionString);
            store.InitializeFromConfiguration(Path.Combine(Environment.CurrentDirectory, "ontologies.config"));

            watch.Stop();

            Console.WriteLine("Connecting to Stardog took: {0} ms", watch.ElapsedMilliseconds);

            // Uncomment the following line to write all SPARQL queries to
            // STDERR (useful for debugging)
            // store.Log = (query) => Console.Error.WriteLine(query);

            // A model (named graph) is where we collect resources that logically belong together
            Model = store.GetModel(new Uri("http://stardog.com/tutorial"));

            #region using reasoning with LINQ

            watch.Restart();

            // NOTE: Creating a Queryable<> does not issue a query. Queries are created and executed in a 
            // lazy fashion when you access the results, depending on your requested result type:
            //
            // Default: SPARQL SELECT
            // Count(): SPARQL SELECT COUNT(x)
            // Any(): SPARQL ASK
            var artistsWithReasoning = Model.AsQueryable<Artist>(true);

            Console.WriteLine($"With reasoning set to 'true', the artists query returned {artistsWithReasoning.Count()} records");

            watch.Stop();

            Console.WriteLine("Reasoning query took: {0} ms", watch.ElapsedMilliseconds);

            var artistsWithoutReasoning = Model.AsQueryable<Artist>(false);

            Console.WriteLine($"With reasoning set to 'false', the artists query returned {artistsWithoutReasoning.Count()} records");

            #endregion

            #region paging through items using LINQ

            const int PAGE_SIZE = 4;
            const int MAX_NUMBER_OF_PAGES = 8;

            watch.Restart();

            var allBands = Model.AsQueryable<Band>()
                                .OrderBy((b) => b.Name)
                                .ToList();

            // loop through pages of bands

            for (int n = 0; n < MAX_NUMBER_OF_PAGES; n++)
            {
                Console.WriteLine($"Fetching {PAGE_SIZE} bands from page {n + 1}...");

                foreach (Band b in allBands.Skip(n * PAGE_SIZE).Take(PAGE_SIZE))
                {
                    Console.WriteLine($"Band: {b.Name}");

                    foreach (SoloArtist sa in b.Members)
                    {
                        Console.WriteLine($"\tMember: {sa.Name}");
                    }
                }
            }

            watch.Stop();

            Console.WriteLine("Looping through bands and members took: {0} ms", watch.ElapsedMilliseconds);

            #endregion

            const string SEARCH_STRING = "the beatles";

            #region filtering items using - SPARQL

            Console.WriteLine($"Get the members of bands that start with \"{SEARCH_STRING}\" using SPARQL");

            // NOTES ON SPARQL:
            // 1. No need to define the PREFIXes here because the SparqlQuery will automatically 
            // assert the ones registered in the ontologies.config file. This allows us to manage
            // namespaces at the app level.
            // 2. We do need a FROM to specify the named graph because it will be asserted by the Model

            var sparqlQuery = new SparqlQuery($@"
                  SELECT ?member ?member_name
                  WHERE {{
                    ?band rdf:type music:Band;
                      music:name ?name;
                      music:member ?member .
                    ?member music:name ?member_name .
                    FILTER(REGEX( ?name, '^{SEARCH_STRING}', 'i'))
                  }}");

            var bindings = Model.GetBindings(sparqlQuery).ToList();

            if (bindings.Count > 0)
            {
                Console.WriteLine($"Found {bindings.Count} matching members");

                foreach (var binding in bindings)
                {
                    Console.WriteLine($"\tMember: {binding.Values.First()}");
                }
            }

            #endregion

            #region filtering items using - LINQ

            Console.WriteLine($"Find bands that start with \"{SEARCH_STRING}\" using LINQ");


            // NOTES ON LINQ:
            // 1. Try to avoid calling .Count(), .Any(), .First(), etc. on queryables as much as possible because
            // these will result in SPARQL queries to be issued.
            // 2. Casting .ToList() here is a potential performance issue as there might be a large number of results.
            // In our case it's equivalent to the handling of the SPARQL query, though. The benefit here is that now,
            // .Any() and .First() are invoked upon a list in memory and does not result in additional SPARQL queries.
            var matchingBands = Model.AsQueryable<Band>()
                             .Where(band => band.Name.StartsWith(SEARCH_STRING, StringComparison.InvariantCultureIgnoreCase))
                             .ToList();

            Console.WriteLine($"Found {matchingBands.Count} band(s) that match!");

            if (matchingBands.Any())
            {

                Console.WriteLine("Printing the members of the first matching band");

                var firstBand = matchingBands.First();

                foreach (var m in firstBand.Members)
                {
                    Console.WriteLine($"Name: {m.Name}");
                }

                // Get all the albums by the first band from our result set...
                var albums = Model.AsQueryable<Album>()
                        .Where(album => album.Artist == firstBand)
                        .ToList();

                Console.WriteLine($"Band {firstBand.Name} has {albums.Count} albums");

                // Grab a random album...

                var rand = new Random();
                var randomAlbum = albums.Skip(rand.Next(albums.Count)).First();

                // Print the track list...
                Console.WriteLine($"Track list for album '{randomAlbum.Name}' by {randomAlbum.Artist.Name}:");

                foreach (var track in randomAlbum.Tracks)
                {
                    Console.WriteLine($"\tTrack: {track.Name}, Length: {TimeSpan.FromSeconds(track.Length).TotalMinutes:0.##}");

                    // Print the track's writers...

                    foreach (var writer in track.Writers)
                    {
                        Console.WriteLine($"\t\tWritten by: {writer.Name}");
                    }
                }

                // Print the album's length...

                int totalLength = randomAlbum.Tracks.Sum(s => s.Length);
                Console.WriteLine($"Total length is {TimeSpan.FromSeconds(totalLength).TotalMinutes:0.##}");

                #endregion
            }
        }
    }
}
