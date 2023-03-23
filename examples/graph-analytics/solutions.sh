#! /bin/bash

stardog-admin db drop -u $U -p $P router
stardog-admin db create -n router -u $U -p $P 
# stardog data remove --all router

stardog namespace add -u $U -p $P --prefix net --uri http://routers.stardog.com/ router
# stardog-admin virtual import  router $sms_file routers.csv
stardog-admin virtual import -u $U -p $P router scope.sms routers_scope.csv
stardog-admin virtual import -u $U -p $P router routers.sms routers.csv

stardog data add router -u $U -p $P -g net:basic basic.ttl
stardog data add router -u $U -p $P -g net:onto onto.ttl
stardog data add router -u $U -p $P -g net:sym onto_symmetric.ttl

stardog reasoning schema -u $U -p $P --add basic --graphs net:basic -- router
stardog reasoning schema -u $U -p $P --add onto --graphs net:onto -- router
stardog reasoning schema -u $U -p $P --add sym --graphs net:sym -- router

stardog query -u $U -p $P router "select (count(*) as ?n) {?s ?p ?o .}"


echo 'stardog query --schema onto  router "select * {  net:r_465 net:connects ?o .}"'
stardog query -u $U -p $P --schema basic  router "select * {  net:r_465 net:connects ?o .}"

echo 'stardog query --schema sym router "select * {  net:r_465 net:connects ?o .}"'
stardog query -u $U -p $P --schema sym router "select * {  net:r_465 net:connects ?o .}"

