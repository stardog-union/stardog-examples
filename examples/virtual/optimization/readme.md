# Optimizing Virtual Graphs

These are the supporting files for the [Optimizing Virtual Graphs](https://docs.stardog.com/virtual-graphs/optimization) section of the online documentation.

See [examples.sh](examples.sh) for CLI commands for creating the virtual graphs, [examples.rq](examples.rq) for the SPARQL queries.

## Running on SQL Server

The `.sql` files are written for MySQL, and the query plans in the documentation show MySQL SQL. To run the examples on SQL Server instead, start Stardog and run:

```bash
./examples.sh mssql
```

This starts a `mcr.microsoft.com/mssql/server:2022-latest` container named `mssql-examples` on port 1433, loads the tables into an `examples` database and creates the virtual graphs with [examples_mssql.properties](examples_mssql.properties). The SQL Server JDBC driver ships with Stardog. On Apple silicon, Docker runs the image with Rosetta emulation.

The script adapts the MySQL-specific parts:

* The `CREATE DATABASE IF NOT EXISTS` and `USE` lines are dropped from each `.sql` file.
* `04rdftype.sql` recreates `Roles` without the `Keanu Reeves` row, so `03denormalized.sql` is loaded after it.
* In `06functions.sql`, the inline `KEY` becomes an `INDEX` and the trailing MySQL queries are skipped.
* SQL Server has no `ANY_VALUE`, so `denormalized3` is created with `MIN(name)` instead.

Plans differ from the documentation in quoting only: `[examples].[dbo].[Roles]` instead of `` `examples`.`Roles` ``. To try the `functional.dependency.sets` example, uncomment that line in `examples_mssql.properties` and rerun the script, or add a virtual graph with a copy of the file.

To query the tables directly:

```bash
docker exec -it mssql-examples /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P 'Examples#2026' -d examples
```

Remove the container with `docker rm -f mssql-examples`.
