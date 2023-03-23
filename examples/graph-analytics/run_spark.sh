#! /bin/bash

# whoami
date

spark-submit --master local[*] --files router.properties  stardog-spark-connector-2.0.0.jar router.properties

date


