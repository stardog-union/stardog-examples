#! /bin/bash

# whoami
date

spark-submit --master local[*] --files router.properties  stardog-spark-connector-1.0.1.jar router.properties

date


