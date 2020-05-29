[![Join the chat at https://gitter.im/Complexible/stardog-examples](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Complexible/stardog-examples?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Programming Stardog: Examples

This is a small collection of examples of working with [Stardog](http://stardog.com) via its APIs, as
well as examples of how to use some of the extension points within Stardog.

## How to build examples

First, you'll need a valid Stardog download.

You can use the included Gradle Wrapper script to build the examples in `examples/api`, `example/foaf`, `examples/function`.

To compile or run the examples, gradle will automatically download the dependencies from our public maven repository:

```bash
$ ./gradlew compileJava
```

To run the examples, they require a valid `$STARDOG_HOME`; you can provide this via the parameter `stardog.home`
(eg `-PstardogHome=/my/stardog/home`).

To run any of the examples, you can use the Gradle `execute` task.  By default, this will run the `ConnectionAPIExample`
program, but you can specify the fully-qualified class name of any of the other examples using the `mainClass` parameter.

```java
$ ./gradlew execute -PmainClass=com.complexible.stardog.examples.api.ICVExample
```

### .NET Examples

To compile the .NET examples, you will need to install [.NET Core](https://dotnet.microsoft.com/download). Make sure that the `dotnet` tool is on your `PATH`

While you can build the sample with the latest version of the .NET Core SDK you will need .NET Core runtime installed to **run** the example. You can download it from [here](https://dotnet.microsoft.com/download/dotnet-core/2.1) - you can verify which .NET Core runtimes you have installed by running:

```bash
dotnet --list-runtimes
```

Once you have installed .NET Core SDK and runtime,you can build the sample with the following command:

```bash
$ ./gradlew compileDotnet
```

To run the .NET examples you should have Stardog running locally and listening on port 5820 (you can change this by editing the connection string in `./examples/dotnet/TrinityConsoleSample/Program.cs`. In addition, the example expects Stardog to have a database named 'music' loaded with the data from `./examples/dotnet/TrinityConsoleSample/Ontologies/music_schema.ttl` and [music.ttl.gz](https://github.com/stardog-union/stardog-tutorials/blob/master/music/music.ttl.gz) in a named graph called `http://stardog.com/tutorial`

You can run the sample with the following command (NOTE: the following command will automatically rebuild the sample):

```bash
$ ./gradlew runDotnet
```

The sample project will connect to Stardog and execute several queries using SPARQL and LINQ. It will print the results to the console.

For additional information see the DotNet Samples' README file - `./examples/dotnet/README.md`

## Generating Documentation

The Stardog [documentation](http://docs.stardog.com) and its [javadocs](http://docs.stardog.com/java/snarl) are a good
place to start. But some examples in this repository are annotated using Markdown; they can be processed by
[Docco](http://jashkenas.github.io/docco/).

If you don't have Docco installed, it's pretty easy to get started:

```bash
$ sudo npm install -g docco
```

Then, you can run it directly against any example:

```bash
$ docco -o docs main/src/com/complexible/stardog/api/ConnectionAPIExample.java
```

Or you can use the supplied `gradle docs` task in each build file that will run Docco against all annotated source files.

## Service Loading

You'll notice that a number of examples have in their source a directory `META-INF/services`, these are the service
registrations for each example.

Stardog uses the JDK [ServiceLoader](http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html) to load
new services at runtime and make them available to the various parts of the system.  The files in the `services`
directory should be the fully qualified class name of the class/service, such as `com.complexible.stardog.plan.filter.functions.Function`,
and the contents of the file should be a list of the fully qualified class names of the implementations of that service.

These need to be a part of your classpath, usually embedded in the jar file with the compiled source, in order for
the `ServiceLoader` to make them up.

## List of Examples

1. [Custom Analyzers](./examples/analyzer/readme.md)
1. [Stardog API Examples](./examples/api/readme.md)
1. [Integrity Constraint Validation](./examples/cli/icv)
1. [CSV example](./examples/cli/virtual/csv/readme.md)
1. [Docs Examples](./examples/docs/readme.md)
1. [Database Archetype Extensibility](https://github.com/stardog-union/stardog-archetypes/)
1. [DotNet Examples](./examples/dotnet/README.md)
1. [Function Extensibility](./examples/function/readme.md)
1. [Transaction Listener](./examples/listener/readme.md)
1. [Cloud Foundry Example Application](https://github.com/stardog-union/cf-example)
1. [Machine Learning](./examples/machinelearning)
1. [Anti-money laundering (AML) Example](./examples/aml)
1. [Transaction listener](./examples/connectable)
1. [Http handler](./examples/http_handler)
