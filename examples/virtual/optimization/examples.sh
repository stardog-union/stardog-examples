#!/bin/bash
#
# Usage: ./examples.sh          create the virtual graphs over an existing MySQL (examples.properties)
#        ./examples.sh mysql    load the tables into a MySQL Docker container and
#                               create the virtual graphs over it (examples.properties)
#        ./examples.sh mssql    load the tables into a SQL Server Docker container and
#                               create the virtual graphs over it (examples_mssql.properties)

set -e
cd "$(dirname "$0")"

PROPS=examples.properties
V3=03denormalized_v3.sms

if [ "$1" = "mysql" ]; then
  if ! docker ps -a --format '{{.Names}}' | grep -qx mysql-examples; then
    docker run -d --name mysql-examples -e MYSQL_ROOT_PASSWORD=pssword -p 3306:3306 mysql:8.4
  fi
  docker start mysql-examples > /dev/null

  mysql() {
    docker exec -i mysql-examples mysql -uroot -ppssword "$@" 2> >(grep -v 'Using a password' >&2)
  }

  until mysql -e "SELECT 1" > /dev/null 2>&1; do sleep 2; done

  # 04rdftype.sql recreates Roles without the Keanu Reeves row, so 03denormalized.sql is loaded after it
  for f in 01uniquekeys.sql 01foreignkeys.sql 02denormalized_songs.sql 04rdftype.sql 03denormalized.sql 05predicates.sql 06functions.sql; do
    mysql < "$f" > /dev/null
  done
elif [ "$1" = "mssql" ]; then
  PROPS=examples_mssql.properties
  SA_PASSWORD='Examples#2026'

  if ! docker ps -a --format '{{.Names}}' | grep -qx mssql-examples; then
    docker run -d --platform linux/amd64 --name mssql-examples \
      -e ACCEPT_EULA=Y -e "MSSQL_SA_PASSWORD=$SA_PASSWORD" -p 1433:1433 \
      mcr.microsoft.com/mssql/server:2022-latest
  fi
  docker start mssql-examples > /dev/null

  sqlcmd() {
    docker exec -i mssql-examples /opt/mssql-tools18/bin/sqlcmd -C -b -S localhost -U sa -P "$SA_PASSWORD" "$@"
  }

  until sqlcmd -Q "SELECT 1" > /dev/null 2>&1; do sleep 2; done
  sqlcmd -Q "IF DB_ID('examples') IS NULL CREATE DATABASE examples"

  # Drop the MySQL-only CREATE DATABASE/USE lines. 04rdftype.sql recreates Roles without
  # the Keanu Reeves row, so 03denormalized.sql is loaded after it.
  for f in 01uniquekeys.sql 01foreignkeys.sql 02denormalized_songs.sql 04rdftype.sql 03denormalized.sql 05predicates.sql; do
    sed '1,2d' "$f" | sqlcmd -d examples > /dev/null
  done
  # MySQL inline KEY becomes INDEX; the trailing MySQL SELECTs are skipped
  sed -e '1,2d' -e 's/^\( *\)KEY (/\1INDEX ix_title_year (/' -e '/^SELECT/,$d' 06functions.sql | sqlcmd -d examples > /dev/null

  # SQL Server has no ANY_VALUE
  V3=$(mktemp)
  sed 's/ANY_VALUE(/MIN(/' 03denormalized_v3.sms > "$V3"
fi

stardog-admin virtual add -o -f sms2 -n uniques $PROPS 01uniquekeys.sms

stardog-admin virtual add -o -f sms2 -n fk_example $PROPS 01foreignkeys.sms

stardog-admin virtual add -o -f sms2 -n denormalized1 $PROPS 03denormalized_v1.sms

stardog-admin virtual add -o -f sms2 -n denormalized2 $PROPS 03denormalized_v2.sms

stardog-admin virtual add -o -f sms2 -n denormalized3 $PROPS $V3

stardog-admin virtual add -o -f sms2 -n denormalized4 $PROPS 03denormalized_v4.sms

stardog-admin virtual add -o -f sms2 -n denormalized5 $PROPS 03denormalized_v5.sms

stardog-admin virtual add -o -f sms2 -n actor1 $PROPS 04rdftype_v1.sms

stardog-admin virtual add -o -f sms2 -n actor2 $PROPS 04rdftype_v2.sms

stardog-admin virtual add -o -f sms2 -n predicates $PROPS 05predicates.sms

stardog-admin virtual add -o -f sms2 -n templates $PROPS 05templates.sms

stardog-admin virtual add -o -f sms2 -n remakes $PROPS 06functions.sms

stardog-admin virtual add -o -f sms2 -n datasets1_v1 $PROPS 05datasets1_v1.sms

stardog-admin virtual add -o -f sms2 -n datasets2_v1 $PROPS 05datasets2_v1.sms

stardog-admin virtual add -o -f sms2 -n datasets1_v2 $PROPS 05datasets1_v2.sms

stardog-admin virtual add -o -f sms2 -n datasets2_v2 $PROPS 05datasets2_v2.sms
