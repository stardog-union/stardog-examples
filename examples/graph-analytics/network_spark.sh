#! /bin/bash

# whoami
date

spark-submit --master local[*] --files network.properties  stardog-spark-connector-2.0.0.jar network.properties

date


