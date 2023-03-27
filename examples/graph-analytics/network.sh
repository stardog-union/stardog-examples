#! /bin/bash

stardog-admin db drop network
stardog-admin db create -n network

stardog namespace add --prefix net --uri http://routers.stardog.com/ network
stardog data add network network.ttl 

