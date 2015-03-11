# DataOps
---

The goal of this project is to provide an extensible Java/Groovy framework for reading and/or writing data, so that
disparate data sources can be linked.

It uses convention over configuration (configuration being the fallback).

This library currently provides the ability to:

- read Excel2013, CSV and FixedWidth files
- write to any JDBC database connection (comes with a H2 database [OOTB](http://en.wikipedia.org/wiki/Out_of_the_box_feature))

---

# Distribution

There are no public JARs available yet, simply because I haven't got anywhere to host them. If there's interest, I'll
gladly publish the library (to [search.maven.org](search.maven.org)?).

---

## Example Usage: Reading a CSV file into an inmemory database connection

**_test.csv_**

    Name,Age
    Nick,30
    Bob,27
    Jack,55

**_example.groovy_**

```Groovy
import org.dataops.writers.JDBCWriter

def minAge = 29
new JDBCWriter()
        .read('/test.csv')
        .eachRow("select * from data where age > $minAge"){
    println "$it.name is $it.age years old."
}
```

**_console output_**

    Nick is 30 years old.
    Jack is 55 years old.

**_console err_**

    INFO: create table data (
        name varchar(2048) ,
        age decimal
    )

---

## Example Usage: Reading a CSV file into a custom database connection/schema

**_example.groovy_ (snippet)**

```Groovy
new JDBCWriter(Sql.newInstance('jdbc:h2:mem:test'))
        .read('/test.txt', [schemaName: 'csv'])
        .eachRow("select * from csv.data where age > $minAge"){
        // do something
}
```

**_console err_**

    INFO: create schema if not exists csv
    INFO: create table csv.data (
        name varchar(2048) ,
        age decimal
    )

---

## Example Usage: Reading files from different locations

**_example.groovy_ (snippet)**

```Groovy
def db = new JDBCWriter()
db.read('/test.csv')                // read from system
db.read('file:/test.csv')           // read from system (using url syntax)
db.read('classpath:///test.csv')    // read from classpath (using url syntax)
db.read('http://foo/bar/test.csv')  // read from http
// or you can just use a URL
db.read(new URL('http://foo/bar/test.csv'))
```

---

## Example Usage: Using the DataOps.bat file (GroovySH)

**_Run bin\DataOps.bat_**

```Groovy
db = new JDBCWriter()
db.read '/test.csv'
db.rows 'select * from data'
```

---

## Example Usage: Different JDBC Connection strings.

DataOps comes with H2, MySQL and PostgreSQL drivers OOTB. Below are some examples on how to connect to them:

```Groovy
new JDBCWriter(Sql.newInstance('jdbc:h2:mem:test'))
new JDBCWriter(Sql.newInstance('jdbc:h2:file:/test'))
new JDBCWriter(Sql.newInstance('jdbc:postgresql://localhost:5432/test?user=postgres&password=password'))
new JDBCWriter(Sql.newInstance([
       url: 'jdbc:h2:mem:foobar',
       user: 'sa',
       password: 'sa',
       driver: 'org.h2.Driver']))
```

---

# TODO

- **_DONE_** - Build executable jar, integrated with Groovy Shell.
- Ability to configure tableName mappings in options
- Ability to configure creation of schemas/tables
- Document "How to write your own Reader/Writer" (extend AbsDataReader/AbsDataWriter and register it.)
- Test reading files from URLs (e.g. http)
- Extend reader formats (Read html tables from web sites? Xml? Xls<2013? Other JDBC databases? etc???)
- Introduce batch mode (pipe data into a table from command line)

---

# References

We make use of:

- [Apache POI](http://poi.apache.org/) - for parsing Excel files
- [OpenCSV](http://opencsv.sourceforge.net/) - for parsing CSV files

---

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/nickgrealy/dataops/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

