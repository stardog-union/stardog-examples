CSV example
===========

This example shows how to map a [simple CSV file](cars.csv) (original csv from [wikipedia](http://en.wikipedia.org/wiki/Comma-separated_values#Example))
into the [Vehicle Sales Ontology (VSO)](http://www.heppnetz.de/ontologies/vso/ns) that extends [GoodRelations vocabulary](http://www.heppnetz.de/projects/goodrelations/).
See the comments in [the template file](cars_mappings.ttl) for the details of the mapping. The final output can be seen in [cars.ttl](cars.ttl).

The following command can be used to import the contents of the CSV file into the Stardog database `mydb`:

```
stardog-admin virtual import mydb cars_mappings.ttl cars.csv 
```