package org.dataops.readers

import org.dataops.TestUtils
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class CsvReaderTest {

    static URL url
    static CsvReader reader

    @BeforeClass
    static void setUpClass() {
        url = TestUtils.newTmpFileUrl """Strings,Integers,Decimals,Booleans,Dates,Times,DateTimes,Currencies,Percentages
Nick Man,123,1.23,TRUE,1/01/2014,12:00,1/01/2014,\$1.23,0.50%
John O'Grady,789,0,FALSE,2/02/2014,18:00,2/02/2014,\$0
Melissa Fielding,-123,-1.23,FALSE,31/12/2014,1:23,31/12/2014,-\$1.23,123%,foo
"""
        reader = new CsvReader(url)
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
        reader.eachRow('table name is ignored') { rows << it }
        assert rows.size() == 3
        assert rows.first() == [Strings: 'Nick Man', Integers: '123', Decimals: '1.23', Booleans: 'TRUE', Dates: '1/01/2014', Times: '12:00', DateTimes: '1/01/2014', Currencies: '$1.23', Percentages: '0.50%']
    }

    /**
     * Test overriding the defaults/
     */
    @Test
    void testEachRowConfiguration() {
        def rows = []
        reader.eachRow('table name is ignored', [start: 2, end: 3, columnTypes: [aaa: String, bbb: Double]]) { rows << it }
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

    /**
     * Test reading different formats.
     */
    @Test
    void testCornerCases() {
        def rows = []
        URL url = TestUtils.newTmpFileUrl """Application Binaries

e0d123e5f316bef78bfdf5a008837577  OOo_2.0.1_LinuxIntel_install.tar.gz
35d91262b3c3ec8841b54169588c97f7  OOo_2.0.1_LinuxIntel_install_wJRE.tar.gz

"""
        new CsvReader(url).eachRow('table name is ignored', [start: 2, columnTypes: [md5: String, blank: String, filename: String], separator: ' ']) {
            rows << it
        }
        assert rows == [
                [md5: 'e0d123e5f316bef78bfdf5a008837577', blank: '', filename: 'OOo_2.0.1_LinuxIntel_install.tar.gz'],
                [md5: '35d91262b3c3ec8841b54169588c97f7', blank: '', filename: 'OOo_2.0.1_LinuxIntel_install_wJRE.tar.gz']
        ]
    }
}
