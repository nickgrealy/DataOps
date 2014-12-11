package org.dataops.writers

import groovy.sql.GroovyRowResult
import org.dataops.TestUtils
import org.dataops.readers.CsvReader
import org.dataops.readers.DataReaderFactory
import org.dataops.utils.URLResolverUtil
import org.dataops.utils.URLResolverUtilTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.util.concurrent.atomic.AtomicInteger

import static org.dataops.TestUtils.*
import static org.dataops.readers.DataReaderFactory.*

/**
 * Created by Nick on 11/12/2014.
 */
@RunWith(Parameterized)
class JDBCWriterTest {

    @Parameterized.Parameters(name="{index} - {2}")
    static def data() {
        [
                [newTmpFileUrl("aaa,bbb\n1,2\n3,4"), MIMETYPE_CSV, ['mycsv'], [2]].toArray(),
                [newTmpFileUrl("aabb\n1122\n3344\n5566"), MIMETYPE_FIXEDWIDTH, ['myfixed'], [3]].toArray(),
                [URLResolverUtilTest.getExcelClasspath(), MIMETYPE_EXCEL2013, [
                        ('EnvironmentVariables'):'myexcel1',
                        ('CheckDataTypes'):'myexcel2'
                ], [4, 3]].toArray()
        ]
    }

    static JDBCWriter db = new JDBCWriter()

    URL url
    def mimeType
    def tableNames
    def expectedRows

    JDBCWriterTest(URL url, def mimeType, def tableNames, def expectedRows) {
        this.url = url
        this.mimeType = mimeType
        this.tableNames = tableNames
        this.expectedRows = expectedRows
    }

    @Test
    void testWriterConfiguration() {
        db.read(url, [mimeType: mimeType, schemaName: 'myschema', tableNames: tableNames])
        assert expectedRows.size() == tableNames.size()
        if (tableNames instanceof List){
            tableNames.eachWithIndex { Object entry, int i ->
                def rows = db.rows("select * from myschema.${entry}")
                println rows
                assert rows.size() == expectedRows[i], "Expected row size didn't match actual ${rows.size()} for table ${entry}"
            }
        } else if (tableNames instanceof Map){
            tableNames.eachWithIndex { Map.Entry<Object, Object> entry, int i ->
                def rows = db.rows("select * from myschema.${entry.value}")
                println rows
                assert rows.size() == expectedRows[i], "Expected row size didn't match actual ${rows.size()} for table ${entry.value}"
            }
        }
    }

}
