CSV example
===========

This example shows how to map a [simple CSV file](cars.csv) (original csv from
[wikipedia](http://en.wikipedia.org/wiki/Comma-separated_values#Example)) into the
[Vehicle Sales Ontology (VSO)](http://www.heppnetz.de/ontologies/vso/ns) that extends
[GoodRelations vocabulary](http://www.heppnetz.de/projects/goodrelations/). See the comments in
[the template file](cars_mappings.sms) for the details of the mapping. The final output can be seen in
[cars.ttl](cars.ttl).

The following command can be used to import the contents of the CSV file into the Stardog database `mydb`:

```
stardog-admin virtual import --format sms2 mydb cars_mappings.sms cars.csv
```

Mappings can be ommitted from the virtual import command for delimited files and they will be generated automatically.
See the comments in the [cars_autogenerate.properties](cars_autogenerate.properties) file for a description of the
needed properties for automatic mappings generation. The final output for an import with generated mappings can be seen
in [cars_autogenerate.ttl](cars_autogenerate.ttl).

The following command can be used to import the contents of the CSV file using automatically generated mappings into
the Stardog database `mydb`:

```
stardog-admin virtual import mydb cars_autogenerate.properties cars.csv
```
