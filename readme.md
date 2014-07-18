# Examples of using and extending Stardog

This is a small collection of examples of working with [Stardog](http://stardog.com) via its APIs, as
well as examples of how to use some of the extension points within Stardog.

## How to build examples


You'll need [Ant](http://ant.apache.org/), and a valid Stardog download.  First copy the example properties file
into the correct place:

```bash
cp project.properties.example project.properties
```

Then, using your text editor of choice, specify the paths to your Stardog installation location and home directory.

From there you can navigate into any of the example directories where there is a `build.xml` file and run `ant build`

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

Or you can use the supplied `ant docs` task in each build file that will run Docco against all annotated source files.

## Service Loading

You'll notice that a number of examples have in their source a directory `META-INF/services`, these are the service
registrations for each example.

Stardog uses the JDK [ServiceLoader](http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html) to load
new services at runtime and make them available to the various parts of the system.  The files in the `services`
directory should be the fully qualified class name of the class/service, such as `com.complexible.stardog.plan.filter.functions.Function`,
and the contents of the file should be a list of the fully qualified class names of the implementations of that service.

These need to be a part of your classpath, usually embedded in the jar file with the compiled source, in order for
the `ServiceLoader` to make them up.
