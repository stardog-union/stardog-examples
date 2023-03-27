#! /bin/bash

stardog-admin db drop router
stardog-admin db create -n router
# stardog data remove --all router

stardog namespace add --prefix net --uri http://routers.stardog.com/ router
# stardog-admin virtual import  router $sms_file routers.csv
stardog-admin virtual import router scope.sms routers_scope.csv
stardog-admin virtual import router routers.sms routers.csv

stardog data add router -g net:basic basic.ttl
stardog data add router -g net:onto onto.ttl
stardog data add router -g net:sym onto_symmetric.ttl

stardog reasoning schema --add basic --graphs net:basic -- router
stardog reasoning schema --add onto --graphs net:onto -- router
stardog reasoning schema --add sym --graphs net:sym -- router

stardog query router "select (count(*) as ?n) {?s ?p ?o .}"


echo 'stardog query --schema onto  router "select * {  net:r_465 net:connects ?o .}"'
stardog query --schema basic  router "select * {  net:r_465 net:connects ?o .}"

echo 'stardog query --schema sym router "select * {  net:r_465 net:connects ?o .}"'
stardog query --schema sym router "select * {  net:r_465 net:connects ?o .}"

