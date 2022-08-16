# Router Demo: Ontologies, graph analytics and more

## The data

Run `build.sh` to recreate the database used in this example. The data are derived from an actual dataset concerning routers and their connections. This set contains a sample of 2113 routers and their connections between them. No further information about the routers is available here.

```
stardog query router --reasoning "select (count(distinct(?r)) as ?n) {?r a net:Router}"
```

To make the example more interesting, I have split the routers into two classes: *Regional* and *Local*. As we shall see, the *Regional* routers are fewer in number and have more edge connections than the *Local* ones.

```
stardog query router "select ?type (count(distinct(?r)) as ?n) {?r a ?type . ?type rdfs:subClassOf net:Router} group by ?type"
```

*Note that I omit the reasoning flag in this query. If I include reasoning, then each router is counted twice - once under its type (`net:Regional` or `net:Local`), and once under `net:Router`.*


## Calculating node degree

We can view node degree in one of two ways:

* the number of edges leaving a node, taking edge direction into account.
* the number of edges leaving a node regardless of direction.

Router connections are mapped to property `net:connects`, which the ontology defines to be a symmetric property. This means I can make my edges bidirecitonal by including the reasoning switch.

### List of node degrees, ignoring edge direction 

```
# Node_index.rq
# To get all edges regardless of direction. Must add --reasoning to the query

select ?r (count(?connection) as ?edges) {
    VALUES (?connection) {(net:connects)}
    ?r 
        a net:Router ;
        ?connection ?s .

    ?s 
        a net:Router.
}
group by ?r 
order by desc(?edges) 
```

To run the query and get the top 5 indeces, do `stardog query router --reasoning --limit 5 node_index.rq

### Top 5 taking direction into account

Without the ontology, `?r a net:Router` return 0 triples. We need to adjust for the router type and count the connections as before.



```
## Node index directional
## Omit reasoning

select ?r (count(?connection) as ?edges) {
    VALUES (?connection) {(net:connects)}
    ?r 
        a ?type1;
        ?connection ?s .

    ?s 
        a ?type2 .
    
}
group by ?r 
order by desc(?edges) 
```


### Average node index by router scope

```
## Average node index by scope 

select ?type (avg(?edges) as ?node_index_avg) {
    SELECT ?r ?type (count(?connection) as ?edges) {

        VALUES (?connection) {(net:connects)}
        ?r 
            ?connection ?s ;
            a ?type

        FILTER (?type != net:Router)     
    }
    GROUP BY ?r ?type
}
group by ?type
```

## A centrality measure: PageRank

Results of Stardog queries can be passed to Spark GraphX directly to run GraphX' graph analytics algorithms. The results are written back to the database as part of a named graph. For the purposes of this example, you can access this functionality on your local machine with the following preparation. No knowledge of Spark is required to run these algorithms, but you do need to install Spark.

1. Install Java, Stardog, Scala and Spark.
2. Download the jar file needed to connect Stardog and Spark, [https://stardog-spark-connector.s3.amazonaws.com/stardog-spark-connector-1.0.1.jar](stardog-spark-connector-1.0.1.jar). Check the current Stardog documentation to ensure you have the correct link for your version. These instructions are for Stardog 7.9.1. Place the `jar` file in the same folder as files for this demo.
3. In the demo directory, run `spark-shell` to ensure that your machine can find and run Spark. If it works, exit the Spark shell by typing `:quit`. 
   1. If Spark complains that it can't initialize the session due to a missing `.spark_events` file, do `mkdir .spark_events` in your working folder.
4. Create a `properties` file to set the parameters for your Spark job. In this example, I'm calling the file `router.properties`.
5. At the command line, do `spark-submit --master local[*] --files router.properties  stardog-spark-connector-1.0.1.jar router.properties`

The PageRank algorithm is a useful measure of centrality. We can obtain the pagerank score of each router.

```
algorithm.name=PageRank
algorithm.iterations=10

# Stardog connection parameters
stardog.server=http://localhost:5820
stardog.database=router
stardog.username=admin
stardog.password=admin
stardog.query.timeout=10m
stardog.reasoning=true
stardog.query=construct {?r net:connects ?s} where {  graph <tag:stardog:api:context:default> {?r a net:Router ; net:connects ?s . ?s a net:Router.}}

# Output parameters
#output.property= .... name of property from the algorithm
#output.graph= ... named graph for the results. Defaults to the default graph.

# Spark parameters

spark.dataset.size=20000
```

Submitting the job creates a new set of connections between the nodes and their page rank, by way of the new property `stardog:PageRank`. To check this out, let's get the top 10 nodes by page rank score.

```
stardog query router "select *   {?s stardog:PageRank ?score .} order by desc(?score) limit 10"
```

