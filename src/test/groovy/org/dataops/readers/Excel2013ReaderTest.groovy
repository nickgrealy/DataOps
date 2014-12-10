package org.dataops.readers

import org.dataops.utils.URLResolverUtil
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import java.text.SimpleDateFormat

import static org.dataops.readers.DataReaderFactory.getReaderByUrlString

class Excel2013ReaderTest {

    static ExcelReader reader
    public static final String WORKSHEET_DATATYPES = 'CheckDataTypes'

    @BeforeClass
    static void setUpClass() {
        reader = new ExcelReader(URLResolverUtil.getURL('classpath:///Excel2013/No Security.xlsx'))
    }

    /**
     * Test retrieving the table names.
     */
    @Test
    void testTableNames() {
        assert reader.getTableNames() == ['EnvironmentVariables', 'Credentials', WORKSHEET_DATATYPES]
    }

    /**
     * Test that data types are detected by default.
     */
    @Test
    void testColumnTypes() {
        assert reader.getColumnTypes(WORKSHEET_DATATYPES) == [
                Strings    : String,
                Integers   : BigDecimal,
                Decimals   : BigDecimal,
                Booleans   : Boolean,
                Dates      : Date,
                Times      : Date,
                DateTimes  : Date,
                Currencies : BigDecimal,
                Percentages: BigDecimal,
                Formulas   : String,
                Merged     : String,
                Fractions  : String
        ]
    }

    /**
     * Test reading using the defaults.
     */
    @Test
    void testDefaultReader() {
        def rows = []
        reader.eachRow(WORKSHEET_DATATYPES) { rows << it }
        def format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        assert rows.size() == 3
        assert rows.first() == [
                Strings:'Nick Man',
                Integers:123,
                Decimals:1.23,
                Booleans:true,
                Dates: format.parse('2014-01-01T00:00:00.000+1100'),
                Times: format.parse('1899-12-31T12:00:00.000+1000'),
                DateTimes: format.parse('2014-01-01T12:00:00.000+1100'),
                Currencies:1.23,
                Percentages:0.005,
                Formulas:6.0,
                Merged:'lizards',
                Fractions:1]
    }

    /**
     * Test overriding the defaults/
     */
    @Test
    void testConfiguration() {
        def rows = []
        reader.eachRow(WORKSHEET_DATATYPES, [start: 2, end: 3, labels: ['foobar']]) { rows << it }
        assert rows.size() == 1
        assert rows == [[foobar: "John O'Grady"]]
    }

    /**
     * Test reading a file more than once.
     */
    @Test
    void testMultipleReadThroughs() {
        testDefaultReader()
        testDefaultReader()
    }

    /**
     * Test reading different formats.
     */
    @Ignore("Not yet implemented")
    @Test
    void testCornerCases() {
        // test reading secured files
    }
}
