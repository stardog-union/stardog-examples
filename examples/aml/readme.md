# Stardog AML Example

This example includes a number of source files and queries that illustrate an Anti-Money Laundering (AML) use case.
It incorporates reasoning and (optionally) virtual graphs.

Included are "tables" (csv files) for People, Companies, Addresses, BankAccounts, Shares of company stock, and
bank Transactions. The data for Transactions can be loaded either into Stardog with along with the rest of the
data, or loaded into an RDBMS 

## Setting Up

Open a terminal that has `$STARDOG_HOME/bin` in the path. The `create_db.sh` script will create a Stardog DB with
the name `fraud_demo` and import data from this directory.

If you wish to work with virtual data using MySQL, you can modify the script to comment and uncomment the following lines:

```bash
# Uncomment this line to import transactions into Stardog
stardog-admin virtual import $DB transaction-mapping.ttl aml_dataset_transactions.csv

# Or, comment the prior line and uncomment these lines to load the transactions into MySQL
# Alterntively, can use load_transactions.sql instead of load_transactions-mysql.sql
#mysql -uadmin -padmin -hlocalhost stardog < load_transactions-mysql.sql
#stardog-admin virtual add aml.properties transaction-mapping-sql.ttl
```

Before adding the virtual graph, you will need to modify the connection information in the `aml.properties` to match your
server. You will also need to obtain the MySQL Connector/J jar from https://dev.mysql.com/downloads/connector/j/5.1.html
and copy the `mysql-connector-java-5.1.45-bin.jar` file to `$STARDOG_HOME/server/dbms`.

If you wish to use a different RDBMS server you can try altering the `load_transactions.sql` file, replacing backticks
with double quotes, changing identifier casing, etc. as needed to conform to your platform's requirements, or use your
platform's bulk importer to load the `aml_dataset_transactions.csv` file.

You may also need to make similar changes for quoting and casing to the `transaction-mapping-sql.ttl` mapping file.

## Running the Examples

Once you have the data loaded, try running the queries from the `queries.sparql` file, as well as attempting your
own queries. You can alter the data for testing and simple rerun the `create_db.sh` script to reload the data.

The goal of the queries in this example is to identify fraudulent financial activity. Fraudsters can employ countless
strategies to obscure their activity, often incorporating multiple financial institutions from several countries. This
example endeavors to find activity that matches a general pattern where money flows from an "originator" through several
affiliated entities (other People or Companies) outside the perview of this institution, into one of our accounts,
followed by several more extra-institution transfers, ultimately ending in the hands of a "benificiary."

In the course of normal business, such transactions happen all the time. In isolation, everything can look consistently
suspicious (or benign). The signal we will use to indicate suspicion will be the presence of multiple such transfers of
funds through unique sets of affiliates.

For the purpose of this example, two entities (People or Companies) are affiliated if they share an address or if one owns
shares in the other. Both of these are bidirectional (symmetric) relationships.

To identify candidates for investigation, we will assign a score based upon the product of:

1. The total amount of money transferred between the affiliates
1. The square of the number of the unique paths from originator to beneficiary

By squaring the number of unique paths, we reduce the tendency for this approach to flag all large transactions as
suspicious.

## Virtualization

This example uses a small amount of data - 1000 each of People, Companies, plus their Addresses and holdings (Shares).
Add to this the Transaction data, which is much larger - 23,259 in this example, but much larger in real scenarios.
This can be a problem not only because of the sheer size of the data (its Volume), but also because it is constantly
changing (its Velocity). These two qualities suggest that the transactions could be a candidate for virtualization.

During development of this example, I started with 100% native (materialized, not virtual) data. Once the queries were
developed, I modified them to work with virtual transactions. It turned out that for the queries in this example,
Stardog needed to look at 100% of the transactions in order to identify the paths and compute the scores. The final
compromise was to use virtual transactions for all activity after a recent date, and to roll up all older transactions
into a summary transaction for materialization in Stardog. All three queries are included in the example queries.
