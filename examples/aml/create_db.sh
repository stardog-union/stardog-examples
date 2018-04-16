#!/bin/sh

set -x

DB=fraud_demo

stardog-admin db drop $DB

stardog-admin db create -o spatial.enabled=true -o search.enabled=true -n $DB
stardog namespace add $DB --prefix "" --uri tag:stardog:demo:aml:

stardog-admin virtual import $DB address-mapping.ttl aml_dataset_addresses.csv
stardog-admin virtual import $DB bank-account-mapping.ttl aml_dataset_bank_accounts.csv
stardog-admin virtual import $DB company-mapping.ttl aml_dataset_companies.csv
stardog-admin virtual import $DB people-mapping.ttl aml_dataset_people.csv

# These files allow multiple people and companies to share addresses
stardog-admin virtual import $DB company_address-mapping.ttl aml_dataset_companies_addresses.csv
stardog-admin virtual import $DB people_address-mapping.ttl aml_dataset_people_addresses.csv

stardog-admin virtual import $DB company_account-mapping.ttl aml_dataset_companies_accounts.csv
stardog-admin virtual import $DB people_account-mapping.ttl aml_dataset_people_accounts.csv

stardog-admin virtual import $DB company_shares-mapping.ttl aml_dataset_companies_shares.csv
stardog-admin virtual import $DB people_shares-mapping.ttl aml_dataset_people_shares.csv

# Uncomment this line to import transactions into Stardog
stardog-admin virtual import $DB transaction-mapping.ttl aml_dataset_transactions.csv

# Or, comment the prior line and uncomment these lines to load the transactions into MySQL
# Alternatively, can use load_transactions.sql instead of load_transactions-mysql.sql
#mysql -uadmin -padmin -hlocalhost stardog < load_transactions-mysql.sql
#stardog-admin virtual add aml.properties transaction-mapping-sql.ttl

stardog data add -g graph:schema $DB aml_rules.ttl
