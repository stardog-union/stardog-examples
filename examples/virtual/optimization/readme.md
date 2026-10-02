# Optimizing Virtual Graphs

These are the supporting files for the [Optimizing Virtual Graphs](https://docs.stardog.com/virtual-graphs/optimization) section of the online documentation.

See [examples.sh](examples.sh) for CLI commands for creating the virtual graphs, [examples.rq](examples.rq) for the SPARQL queries.

## Running the Examples

[examples.sh](examples.sh) can start a database in Docker, load the tables into an `examples` database and create the virtual graphs. Start Stardog first, then run one of:

```bash
./examples.sh mysql   # MySQL, matches the query plans in the documentation
./examples.sh mssql   # SQL Server
./examples.sh         # an existing MySQL, configured in examples.properties
```

To try the `functional.dependency.sets` example, uncomment that line in the properties file and rerun the script, or add a virtual graph with a copy of the file.

### MySQL

`./examples.sh mysql` starts a `mysql:8.4` container named `mysql-examples` on port 3306 and uses [examples.properties](examples.properties).

The MySQL JDBC driver does not ship with Stardog. Download it and put it on Stardog's classpath before starting the server:

```bash
mkdir -p ~/stardog-ext
curl -fL -o ~/stardog-ext/mysql-connector-j-9.5.0.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.5.0/mysql-connector-j-9.5.0.jar
export STARDOG_EXT=~/stardog-ext
stardog-admin server start
```

To query the tables directly:

```bash
docker exec -it mysql-examples mysql -uroot -ppssword examples
```

### SQL Server

`./examples.sh mssql` starts a `mcr.microsoft.com/mssql/server:2022-latest` container named `mssql-examples` on port 1433 and uses [examples_mssql.properties](examples_mssql.properties). The SQL Server JDBC driver ships with Stardog. On Apple silicon, Docker runs the image with Rosetta emulation.

The script adapts the MySQL-specific parts:

* The `CREATE DATABASE IF NOT EXISTS` and `USE` lines are dropped from each `.sql` file.
* In `06functions.sql`, the inline `KEY` becomes an `INDEX` and the trailing MySQL queries are skipped.
* SQL Server has no `ANY_VALUE`, so `denormalized3` is created with `MIN(name)` instead.

Plans differ from the documentation in quoting only: `[examples].[dbo].[Roles]` instead of `` `examples`.`Roles` ``.

To query the tables directly:

```bash
docker exec -it mssql-examples /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P 'Examples#2026' -d examples
```

### Load Order

`04rdftype.sql` recreates `Roles` without the `Keanu Reeves` row, so the script loads `03denormalized.sql` after it. To run the `actor1` and `actor2` examples as documented, load `04rdftype.sql` again.

Remove the containers with `docker rm -f mysql-examples mssql-examples`.
