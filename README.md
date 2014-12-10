# DataOps

The goal of this project is to provide an extensible framework for reading and/or writing data, so that any two
data sources can be linked.

It attempts to use convention over configuration (configuration being the fallback).

This library currently provides the ability to:
- read Excel2013, CSV and FixedWidth files
- write to any JDBC database connection (comes with a H2 datbase out of the box)

## Examples:

    def db = new H2Writer(Sql.newInstance([url: 'jdbc:h2:mem:foobar', user: 'sa', password: 'sa', driver: 'org.h2.Driver']))
---