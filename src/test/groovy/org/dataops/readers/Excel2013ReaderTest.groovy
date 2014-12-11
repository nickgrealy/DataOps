package org.dataops.readers

import org.dataops.utils.URLResolverUtil
import org.dataops.utils.URLResolverUtilTest
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import java.text.SimpleDateFormat

import static org.dataops.readers.DataReaderFactory.getReaderByUrlString

class Excel2013ReaderTest {

    static ExcelReader reader
    public static final String WORKSHEET_DATATYPES = 'CheckDataTypes'
    static def format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    @BeforeClass
    static void setUpClass() {
        reader = new ExcelReader(URLResolverUtilTest.getExcelClasspath())
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
        def types = reader.getColumnTypes(WORKSHEET_DATATYPES)
        assert types == [
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
    void testEachRowConfiguration() {
        def rows = []
        def targetColumnTypes = [
                Strings    : String,
                Integers   : Integer,
                Decimals   : Float,
                Booleans   : Boolean,
                Dates      : java.sql.Date,
                Times      : java.sql.Date,
                DateTimes  : java.sql.Date,
                Currencies : BigDecimal,
                Percentages: BigDecimal,
                Formulas   : Float,
                Merged     : String,
                Fractions  : Float
        ]
        reader.eachRow(WORKSHEET_DATATYPES, [start: 3, end: 4, columnTypes: targetColumnTypes]) { rows << it }
        assert rows.size() == 1
        def record = rows.first()
        assert record.Strings == 'Melissa Fielding'
        assert record.Integers == -123
        assert record.Decimals == -1.23f
        assert record.Booleans == false
        assert record.Dates ==  new java.sql.Date(format.parse('2014-12-31T00:00:00.000+1100').getTime()) // Wed Dec 31 00:00:00 EST 2014
        assert record.Times ==  new java.sql.Date(format.parse('1899-12-31T01:23:00.000+1000').getTime()) // Sun Dec 31 01:23:00 EST 1899
        assert record.DateTimes ==  new java.sql.Date(format.parse('2014-12-31T01:23:45.000+1100').getTime()) // Wed Dec 31 01:23:45 EST 2014
        assert record.Currencies == -1.23
        assert record.Percentages == 1.23
        assert record.Formulas == 62.115f
        assert record.Merged == ''
        assert record.Fractions == 0.008902077151335312f
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
