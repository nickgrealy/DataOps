import groovy.sql.Sql
import org.dataops.TestUtils
import org.dataops.readers.DataReaderFactory
import org.dataops.writers.JDBCWriter

def minAge = 30

def csvUrl = TestUtils.newTmpFileUrl("""Name,Age
Nick,30
Bob,27
Jack,55
""")

// Example Usage 1

new JDBCWriter()
        .read(csvUrl)
        .eachRow("select * from data where age > $minAge"){
        println "$it.name is $it.age years old."
}

// >> Jack is 55 years old.

// Example Usage 2

new JDBCWriter(Sql.newInstance([url: 'jdbc:h2:mem:foobar', user: 'sa', password: 'sa', driver: 'org.h2.Driver']))
        .read(csvUrl, [schemaName: 'csv', mimeType: DataReaderFactory.MIMETYPE_CSV])
        .eachRow("select * from csv.data where age <= $minAge"){
        println "$it.name is $it.age years old."
}

// >> Nick is 30 years old.
// >> Bob is 27 years old.

println 'Done.'