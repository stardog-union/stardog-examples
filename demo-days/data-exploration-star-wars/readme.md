# Demo Day - Data Exploration - May the Knowledge Graph Be With You

In this exercise we will create a knowledge graph from a blank slate using data from a galaxy far, far away. We will create a basic data model from scratch and use that to integrate and unify each of the sources. 

While this data may be from far away, it exhibits many characteristics of data closer to home:

- messy and inconsistent 
- key concepts represented as simple strings
- important facts like units completely missing
 
Knowledge graphs offer us tools to do data cleanup dynamically, without altering the underlying source of information. We will leverage Stardog’s key differentiator—the Inference Engine—to create perform transformations, extend our data model to represent the new concepts, and infer entirely new relationships. No need to copy and fix data, which requires high manual labor and results in low usability.
 
Finally, we will show how these rich data models, combined with the inference engine, provide a crucial semantic search capability over your knowledge graph. We will perform queries that leverage the rules we created to easily identify key concepts which otherwise would have taken extensive code and/or queries to find.

# Prerequistes

## Getting the Data

Most of the data you will need to participate in this exercise can be found at https://www.kaggle.com/datasets/jsphyg/star-wars. The datasets you would need to download are:

* planets.csv
* species.csv
* characters.csv
* spaceships.csv

You will need these files somewhere handy to use throughout the exercise, so it's best to grab them and put them in the data directory. 

## Getting a Stardog Account

You will also need a Stardog Account 
Go to https://cloud.stardog.com and create your account. This is free and is the best way to access the Stardog Platform. 

## Getting a Stardog Environment

You have several options for your Stardog environment for demo day. If you would like to actively participate, you will need your own Stardog instance. The easiest way to do this is to [subscribe to Stardog Cloud](https://cloud.stardog.com/get-started). However you are welcome to [download](https://www.stardog.com/get-started/) and operate the Stardog Platform yourself.

## Seeing the finished product

The final result of what we will _start_ building in this exercise is available via Stardog Cloud Express. The easiest way to see that is via [this link](https://cloud.stardog.com/connect?endpoint=https://express.stardog.cloud:5820&username=anonymous&password=anonymous) however you can add Express manually with the following instructions:

1. From the [cloud homepage](https://cloud.stardog.com) press `New Connection` 
2. Fill in the following details in the dialog

```
username=anonymous
password=anonymous
endpoint=https://express.stardog.cloud:5820
```

3. Add unique name like "Stardog Express" for this connection 
4. Push the `Connect` button. 

The final result is you will add a connection to Stardog Cloud Express associated with your Stardog Account. 

From there you can select this connection, jump into Explorer, and browse the database `kgc-demo` to see the final product.

# Exercise Overview

These are the main headlines or things we will accomplish in this exercise.

1. Map and Model the concept `Planet` to create our first, simple knowledge graph
2. Map and Model the concepts `Character` and `Species` from their respective data sources
3. Integrate our three data sources via the modeling and mapping procedure to create a single, cohesive graph
4. Map and model the concept `Spacecraft` and publish to the KG
5. Write a rule and use the inference engine to uplift a string-typed attribute of a `Spacecraft` into a numeric field
6. Write additional rules that will trigger based on the first rule to provide further ship classifications
7. Map and model the concept `Organization` and use it as a reference for a new rule to uplift string attributes to nodes/relationships
8. Link our two halves of the knowledge graph by associating `Character`s with their `Organization`s
9. Write a new rule that will infer group membership across nested groups
10. Create 2 additional views of the data, aka schemas, `rebels` and `empire` which classify the `Character`s in our universe as an `Enemy` based on which lens/perspective you assume.  