# Configuration Examples

## database.properties

An example of a configuration file that can be provided to the `db create` command of the Stardog CLI to define database metadata for the newly created database.

## stardog.properties

A properties file showing all of the valid properties for the Stardog server, their usage, and a short discussion for each.

## obfuscation.ttl

A configuration file that can be used for `data obfuscate` and `query obfuscate` commands to customize how database contents will be obfuscated.

## stardog-cluster.yaml

A Kubernetes configuration file for a complete Stardog Cluster deployment (including 3 node ZooKeeper ensemble). 
You must provide base64-encoded strings of your Artifactory credentials (to get the Stardog Docker image) and a 
Stardog License, replacing `<base64 encoded string of the license file>` and `<base64 encoded string of credentials>` 
in the file before you apply it via `kubectl -f stardog-cluster.yaml`.

