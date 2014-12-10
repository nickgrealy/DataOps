package org.dataops.readers

import com.opencsv.CSVParser
import com.opencsv.CSVReader
import groovy.util.logging.Log

@Log
class CsvReader extends AbsDataReader {

    Map<String, Class> columnTypes

    CsvReader(String resource) {
        super(resource)
    }

    CsvReader(URL url) {
        super(url)
    }

    /**
     * If columnTypes not already set, returns entries from first row.
     * @param tableName
     * @return
     */
    @Override
    Map<String, Class> getColumnTypes(String tableName) {
        if (!columnTypes){
            url.withInputStream {
                // get labels and sampleData
                def reader = new CSVReader(it.newReader())
                List<String> labels = reader.readNext()
                List<List<String>> sampleData = []
                3.times { sampleData << reader.readNext() }
                // detect data types
                def dataTypes = detectDataTypes(sampleData)
                columnTypes = [:]
                for (int i = 0; i < Math.min(labels.size(), dataTypes.size()); i++) {
                    columnTypes << [(labels[i]): dataTypes[i]]
                }
            }
        }
        columnTypes
    }

    /**
     * {@inheritDocs}
     *
     * @param tableName - ignored
     * @param params char separator, char quotechar, char escape, boolean strictQuotes, boolean ignoreLeadingWhiteSpace
     * @param closure
     */
    @Override
    void eachRow(String tableName, Map<String, Object> params, Closure closure) {
        CSVReader csvReader
        int start = params.start ?: (params.labels ? 1 : 0)
        url.withInputStream { InputStream it ->
            csvReader = new CSVReader(
                    (Reader) it.newReader(),
                    (char) params.separator ?: CSVParser.DEFAULT_SEPARATOR,
                    (char) params.quotechar ?: CSVParser.DEFAULT_QUOTE_CHARACTER,
                    (char) params.escape ?: CSVParser.DEFAULT_ESCAPE_CHARACTER,
                    start,
                    (boolean) params.strictQuotes ?: CSVParser.DEFAULT_STRICT_QUOTES,
                    (boolean) params.ignoreLeadingWhiteSpace ?: CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE
            )
            def iterator = csvReader.iterator()
//            if (params.start){
//                (0..<params.start).each { if (iterator.hasNext()){ iterator.next() }}
//            }
            Collection<String> labels = params.labels ?: iterator.next()
            List<String> row
            int count = 0
            while (iterator.hasNext() && (!params.end || (start + count++) < params.end)){
                row = iterator.next()
                if (row.size() > 1 || row != ['']){
                    closure(marryDataWithLabels(labels, row))
                }
            }
        }
    }
}
