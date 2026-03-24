#!/bin/bash

stardog-admin virtual add -o -f sms2 -n uniques examples.properties 01uniquekeys.sms

stardog-admin virtual add -o -f sms2 -n denormalized1 examples.properties 03denormalized_v1.sms

stardog-admin virtual add -o -f sms2 -n denormalized2 examples.properties 03denormalized_v2.sms

stardog-admin virtual add -o -f sms2 -n denormalized3 examples.properties 03denormalized_v3.sms

stardog-admin virtual add -o -f sms2 -n actor1 examples.properties 04rdftype_v1.sms

stardog-admin virtual add -o -f sms2 -n actor2 examples.properties 04rdftype_v2.sms

stardog-admin virtual add -o -f sms2 -n predicates examples.properties 05predicates.sms

stardog-admin virtual add -o -f sms2 -n templates examples.properties 05templates.sms

stardog-admin virtual add -o -f sms2 -n remakes examples.properties 06functions.sms

stardog-admin virtual add -o -f sms2 -n datasets1_v1 examples.properties 05datasets1_v1.sms

stardog-admin virtual add -o -f sms2 -n datasets2_v1 examples.properties 05datasets2_v1.sms

stardog-admin virtual add -o -f sms2 -n datasets1_v2 examples.properties 05datasets1_v2.sms

stardog-admin virtual add -o -f sms2 -n datasets2_v2 examples.properties 05datasets2_v2.sms
