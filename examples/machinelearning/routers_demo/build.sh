#! /bin/bash

# Rebuild the data base

stardog-admin db drop router
stardog-admin db create -n router

stardog namespace add --prefix net --uri http://stardog.com/network/ router
stardog namespace add --prefix scope --uri http://stardog.com/network/classes/ router

stardog-admin virtual import  router routers_scope.sms routers_scope.csv
stardog-admin virtual import router routers_connect.sms routers_connect.csv

stardog data add router onto.ttl

stardog namespace list router
echo "Router database rebuilt"

stardog query router "select * {?s net:connects ?o} limit 15"
