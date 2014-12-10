# DataOps
---

The goal of this project is to provide an extensible framework for reading and/or writing data, so that any two
data sources can be linked.

It uses convention over configuration (configuration being the fallback).

This library currently provides the ability to:

- read Excel2013, CSV and FixedWidth files
- write to any JDBC database connection (comes with a H2 database [OOTB](http://en.wikipedia.org/wiki/Out_of_the_box_feature))

---

# Example Usage 1

Reading in a CSV file into an inmemory database connection.

**_test.csv_**

    Name,Age
    Nick,30
    Bob,27
    Jack,55

**_example.groovy_**

    import org.dataops.writers.JDBCWriter

    def minAge = 29
    new JDBCWriter()
            .read('file:/test.csv')
            .eachRow("select * from data where age > $minAge"){
        println "$it.name is $it.age years old."
    }

**_console output_**

    Nick is 30 years old.
    Jack is 55 years old.

**_console err_**

    INFO: create table data (
        name varchar(2048) ,
        age decimal
    )

---

# Example Usage 2

Reading a CSV file from the classpath into a custom database connection/schema.

**_example.groovy_ (snippet)**

    new JDBCWriter(Sql.newInstance([url: 'jdbc:h2:mem:foobar', user: 'sa', password: 'sa', driver: 'org.h2.Driver']))
            .read('classpath:///test.csv', [schemaName: 'csv', mimeType: DataReaderFactory.MIMETYPE_CSV])
            .eachRow("select * from csv.data where age > $minAge"){
        // do something
    }

**_console err_**

    INFO: create schema if not exists csv
    INFO: create table csv.data (
        name varchar(2048) ,
        age decimal
    )

---

# References

We make use of:

- [Apache POI](http://poi.apache.org/) - for parsing Excel files
- [OpenCSV](http://opencsv.sourceforge.net/) - for parsing CSV files

---