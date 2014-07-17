# Examples of using and extending Stardog

This is a small collection of examples of working with [Stardog](http://stardog.com) via it's various APIs as
well as examples of how to use some of the extension points within Stardog.

## How to build examples


You'll need [Ant](http://ant.apache.org/), and a valid Stardog download.  First copy the example properties file
into the correct place:

```bash
cp project.properties.example project.properties
```

Then, using your text editor of choice, specify the paths to your Stardog installation location and home directory.

From there, you can navigate into any of the example directories where there is a `build.xml` file and run `ant build`

## Generating Documentation

The Stardog [documentation](http://docs.stardog.com) and it's [javadocs](http://docs.stardog.com/java/snarl) are a good
place to start.  But some examples are annotated using Markdown so they can be processed by
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

todo:

examples for filter Functions + tests
examples for Property Functions + tests
example for db archetype
rest of the example code
link to main repo
publish and announce