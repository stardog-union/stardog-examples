[![Join the chat at https://gitter.im/Complexible/stardog-examples](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Complexible/stardog-examples?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Programming Stardog: Examples

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/clarkparsia/stardog-examples?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This is a small collection of examples of working with [Stardog](http://stardog.com) via its APIs, as
well as examples of how to use some of the extension points within Stardog.

## How to build examples

First, you'll need a valid Stardog download.

For the examples in `examples/api`, `example/foaf`, and `examples/function`, you'll need [Gradle](http://www.gradle.org/).

To compile or run the examples, gradle will automatically download the dependencies from our public maven repository:

```bash
gradle compileJava
```

To run the examples, they require a valid `$STARDOG_HOME`; you can provide this via the parameter `stardog.home`
(eg `-PstardogHome=/my/stardog/home`).

To run any of the examples, you can use the Gradle `execute` task.  By default, this will run the `ConnectionAPIExample`
program, but you can specify the fully-qualified class name of any of the other examples using the `mainClass` parameter.

```java
gradle execute -PmainClass=com.complexible.stardog.examples.ICVExample
```

## Generating Documentation

The Stardog [documentation](http://docs.stardog.com) and its [javadocs](http://docs.stardog.com/java/snarl) are a good
place to start. But some examples in this repository are annotated using Markdown; they can be processed by
[Docco](http://jashkenas.github.io/docco/).

If you don't have Docco installed, it's pretty easy to get started:

```bash
sudo npm install -g docco
```

Then, you can run it directly against any example:

```bash
docco -o docs main/src/com/complexible/stardog/api/ConnectionAPIExample.java
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
