# Transaction Listener

Stardog transaction framework provides extension points for adding custom behavior for transactions. This 
extension mechanism is internally used for features like free text search and geospatial indexing. This 
example shows a simple way to use this framework for attaching transaction listeners so every triple
added/removed in a transaction can be seen and processed. This example simply prints the contents of the
transaction in the log file but any custom behavior can be implemented by simple modifying one function
in the [ListenerConnectableConnection](main/src/com/complexible/stardog/examples/listener/ListenerConnectableConnection.java)
class.
