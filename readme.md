docco
===

http://jashkenas.github.io/docco/

sudo npm install -g docco


How to build examples
===

project.properties.example -> project.properties

edit as noted in the file


todo:
===

sort out how we link in stardog jars.  using ivy would be ideal and have it resolve out of a local repo
    but i have no idea how to build the local repo from the flat directory structure in the distro
    and we cannot preserve the hierarchical structure we'd get for free from ivy resolve without a pita in classpath
    construction in the CLI.
    maven (ugh!) might be an option here, could use the maven install scripts to build a maven repo locally
        but then we need one for the server jars
    or we can leave it like it is, the ivy file is pretty much useless as the list of full dependencies since it cant
    resolve the stardog ones, we'd only use it for 3rd party stuff that isnt in stardog? this is how the one example
    works now, but it's suboptimal
examples for filter Functions + tests
examples for Property Functions + tests
example for db archetype
rest of the example code
some sort of docs distribution based on docco?
link to main repo
publish and announce