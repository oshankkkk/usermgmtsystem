#!/bin/bash
# Starts SQL Server, waits until it accepts connections, creates the usermgmt database.
/opt/mssql/bin/sqlservr &

for i in $(seq 1 60); do
  /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -Q "SELECT 1" > /dev/null 2>&1 && break
  sleep 2
done

/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" \
  -Q "IF DB_ID('usermgmt') IS NULL CREATE DATABASE usermgmt"

wait
