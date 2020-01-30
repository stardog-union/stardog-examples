# Stardog .NET Examples

The example project demonstrates how to connect to a Stardog server using [TrinityRDF](https://trinity-rdf.net/) and execute queries using SPARQL and LINQ.

TrinityRDF provides an object mapper layer similar to Entity Framework for RDF data. The ontologies the sample uses can be found in the solution's `Ontologies/` folder. Trinity RDF discovers the ontologies in the solution's `ontologies.config` file. TrinityRDF uses the ontologies to generate mapping code - `Ontologies/Ontologies.g.cs`. A developer uses this generated code to create object models (see the files under the solution's `ObjectModels/` folder) with annotations that connect the C# object to the RDF data. These object models then allow .NET developers to create powerful applications without the need to learn SPARQL!

## Prerequisites
* Stardog with a database named "music" populated the data from `./examples/dotnet/TrinityConsoleSample/Ontologies/music_schema.ttl` and [`music.ttl.gz`](https://github.com/stardog-union/stardog-tutorials/blob/master/music/music.ttl.gz) in a named graph called `http://stardog.com/tutorial`
* C# development environment (e.g., Visual Studio Community, Visual Studio for Mac, Visual Studio Code, .NET Core SDK,  etc.)

## Running the sample

Build the solution and then run the resulting program. The program will connect to Stardog and run several queries and print the results to STDOUT.