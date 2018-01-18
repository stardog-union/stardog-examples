# Example Stardog Undertow HTTP Handler Extension

This package is an example of how to create an extension point to the Stardog servers
HTTP handlers.  It allows end users to intercept server calls for monitoring,
altering, doing admission control, etc.

## Build

To build the example simply run: `./gradlew jar`.  This will create the file:
`./build/libs/handler-1.0.jar` which contains the handler extension and the
`META-INF/services` definition that tells Stardog how to load this into the
 handler chain.

## Load into Stardog

To load the module into Stardog set the environment variable `STARDOG_EXT`
to the path that contains the jar referenced above or copy the jar file
to a directory in Stardog's classpath, eg: `server/ext`.  
Then use `stardog-admin` to start the server in the typical manner.
For example: 
```
export STARDOG_EXT=/home/bresnaha/stardog-examples/examples/handler/build/libs
stardog-admin server start
```

For more information see the documentation [here](https://www.stardog.com/docs/#_extending_stardog)
