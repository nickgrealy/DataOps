package org.dataops.readers

import groovy.sql.GroovyRowResult
import org.dataops.TestUtils
import org.dataops.writers.JDBCWriter
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class FixedWidthReaderTest {

    static FixedWidthReader reader

    @BeforeClass
    static void setUpClass() {
        URL url = TestUtils.newTmpFileUrl """Strings         IntegersDecimalsBooleansDates     TimesDateTimes CurrenciesPercentages
Nick Man        123       1.23      TRUE1/01/2014 12:001/01/2014 \$1.23     0.50%
John O'Grady    789     0       FALSE   2/02/2014 18:002/02/2014 \$0
Melissa Fielding-123    -1.23   FALSE   31/12/20141:23 31/12/2014-\$1.23    123%
"""
        reader = new FixedWidthReader(url).configure([columnDividers: [16,24,32,40,50,55,65,75]])
    }

    /**
     * Test retrieving the table names.
     */
    @Test
    void testTableNames() {
        assert reader.getTableNames() == ['data']
    }

    /**
     * Test that data types are detected by default.
     */
    @Test
    void testColumnTypes() {
        assert reader.getColumnTypes('table name is ignored') == [
                Strings: java.lang.String,
                Integers: java.math.BigDecimal,
                Decimals: java.math.BigDecimal,
                Booleans: java.lang.Boolean,
                Dates: java.sql.Date,
                Times: java.lang.String,
                DateTimes: java.sql.Date,
                Currencies: java.lang.String,
                Percentages: java.lang.String
        ]
    }

    /**
     * Test reading using the defaults.
     */
    @Test
    void testDefaultReader() {
        def rows = []
        reader.eachRow('table name is ignored', [:]) { rows << it }
        assert rows.size() == 3
        assert rows.first() == [
                Strings:'Nick Man        ',
                Integers:'123     ',
                Decimals:'  1.23  ',
                Booleans:'    TRUE',
                Dates:'1/01/2014 ',
                Times:'12:00',
                DateTimes:'1/01/2014 ',
                Currencies:'$1.23     ',
                Percentages:'0.50%'
        ]
    }

    /**
     * Test overriding the defaults/
     */
    @Test
    void testEachRowConfiguration() {
        def rows = []
        reader.eachRow('table name is ignored', [start: 2, end: 3, columnTypes: [aaa: String, bbb: Double], trim:true]) { rows << it }
        assert rows.size() == 1
        assert rows.first().aaa == "John O'Grady"
        assert rows.first().bbb == 789.0
    }

    /**
     * Test reading a file more than once.
     */
    @Test
    void testMultipleReadThroughs() {
        testDefaultReader()
        testDefaultReader()
    }

    @Test
    void testFilesWithNoHeaders(){
        URL url = TestUtils.newTmpFileUrl """

  1 2
 3 4

5    6
"""
        def reader = new FixedWidthReader(url).configure([start:2, end:4, columnDividers: [3], columnTypes: ['aaa','bbb'], trim: true])
        def data = reader.collect('ignored table')
        println data
        assert data.size() == 2
    }

}
