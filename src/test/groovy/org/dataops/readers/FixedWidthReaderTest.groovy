package org.dataops.readers

import org.dataops.TestUtils
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class FixedWidthReaderTest {

    static FixedWidthReader reader

    @BeforeClass
    static void setUpClass() {
        URL url = TestUtils.newTmpFileUrl """Strings         IntegersDecimalsBooleansDates     TimesDateTimes CurrenciesPercentages
Nick Man        123       1.23      TRUE1/01/2014 12:001/01/2014 \$1.23     0.50%
John O'Grady    0       0       FALSE   2/02/2014 18:002/02/2014 \$0
Melissa Fielding-123    -1.23   FALSE   31/12/20141:23 31/12/2014-\$1.23    123%
"""
        reader = new FixedWidthReader(url).configure([16,24,32,40,50,55,65,75])
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
                Strings: String,
                Integers: BigDecimal,
                Decimals: BigDecimal,
                Booleans: Boolean,
                Dates: Date,
                Times  : String,
                DateTimes: Date,
                Currencies: String,
                Percentages: String
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
    void testConfiguration() {
        def rows = []
        reader.eachRow('table name is ignored', [start: 2, end: 3, labels: ['foobar'], trim:true]) { rows << it }
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

}
