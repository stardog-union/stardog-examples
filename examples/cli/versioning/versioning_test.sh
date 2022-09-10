#!/usr/bin/env bash
# are we up and running?
stardog-admin server status
# Dirty Drop
stardog-admin db drop demo
# where are we?
pwd
# check files
ls -ll
# Create the Database
stardog-admin db create -o versioning.enabled=true -n demo namespaces.ttl
# versioning commit 1
stardog vcs commit --add version1_add.trig -m "Adding Alice and Bob" -u john -p john demo
# versioning commit 2
stardog vcs commit --add version2_add.trig --remove version2_remove.trig -m "Changed Alice's email" -u jane -p jane demo
# versioning commit with sparql
stardog vcs commit --query version3_add.sparql -m "Add Charlie" demo
# Show commits
stardog vcs list demo
# show only 1 commit for Jane
stardog vcs list --committer jane --limit 1 demo
# do a diff
stardog vcs diff demo
# diff one commit
stardog vcs diff demo b69d3e37-e1ae-4efb-9e9d-c620315a4376
# diff one commit show jane only
stardog vcs diff --single demo b69d3e37-e1ae-4efb-9e9d-c620315a4376
#----
# tags
stardog vcs tag --create v2 demo
stardog vcs tag --list demo
stardog vcs diff demo v1
stardog vcs revert -m "Revert to version 1" demo v1
stardog vcs diff demo
stardog vcs query demo "SELECT * { ?v a vcs:Version }"
stardog vcs query demo list_versions.sparql
stardog vcs query -f turtle demo "CONSTRUCT WHERE {?s ?p ?o}" > vcs_export.ttl
stardog vcs query demo "select * { ?v vcs:updates/vcs:graph :Charlie }"
stardog vcs query demo who_killed_charlie.sparql
