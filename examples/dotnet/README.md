# Stardog .NET Examples

- [Stardog .NET Examples](#stardog-net-examples)
  - [Prequisites to run any of the samples](#prequisites-to-run-any-of-the-samples)
  - [Running an Example](#running-an-example)
  - [The Examples](#the-examples)
    - [HTTP API Example](#http-api-example)
    - [dotNetRDF Example](#dotnetrdf-example)
    - [TrinityRDF Example](#trinityrdf-example)
      - [Prerequisites](#prerequisites)

The Stardog .NET examples show how you can use .NET code to connect to a Stardog server, manage Stardog databases and query them.

## Prequisites to run any of the samples

* C# development environment (e.g., Visual Studio Community, Visual Studio for Mac, Visual Studio Code, .NET Core SDK,  etc.)

## Running an Example

In Visual Studio, select the example project you want to run as the Startup Project. 
Build the project and run the resulting program. The program will connect to Stardog and run several queries and print the results to STDOUT.

## The Examples

### HTTP API Example

This example project demonstrates how to connect to a Stardog server using Stardog's HTTP API and list, create, and drop databases.

### dotNetRDF Example

This example project demonstrates how to connect to a Stardog server using [dotNetRDF](https://github.com/dotnetrdf/dotnetrdf) and list, create, and drop databases; how to insert triples into a graph and query a graph using SPARQL.

### TrinityRDF Example

This example project demonstrates how to connect to a Stardog server using [TrinityRDF](https://trinity-rdf.net/) and execute queries using SPARQL and LINQ.

TrinityRDF provides an object mapper layer similar to Entity Framework for RDF data. The ontologies the sample uses can be found in the solution's `Ontologies/` folder. Trinity RDF discovers the ontologies in the solution's `ontologies.config` file. TrinityRDF uses the ontologies to generate mapping code - `Ontologies/Ontologies.g.cs`. A developer uses this generated code to create object models (see the files under the solution's `ObjectModels/` folder) with annotations that connect the C# object to the RDF data. These object models then allow .NET developers to create powerful applications without the need to learn SPARQL!

#### Prerequisites

* Stardog with a database named "music" populated the data from `./examples/dotnet/TrinityConsoleSample/Ontologies/music_schema.ttl` and [`music.ttl.gz`](https://github.com/stardog-union/stardog-tutorials/blob/master/music/music.ttl.gz) in a named graph called `http://stardog.com/tutorial`