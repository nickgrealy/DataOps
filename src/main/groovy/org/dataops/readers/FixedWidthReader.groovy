package org.dataops.readers

/**
 * Created by Nick Grealy on 10/12/2014.
 */
class FixedWidthReader extends AbsDataReader {

    List<Integer> columnDividers = []
    String encoding = 'UTF-8'

    FixedWidthReader(String resource) {
        super(resource)
    }

    FixedWidthReader(URL url) {
        super(url)
    }

    FixedWidthReader configure(List<Integer> columnDividers, String encoding = 'UTF-8') {
        this.columnDividers = columnDividers
        this.encoding = encoding
        this
    }

    @Override
    Map<String, Class> getColumnTypes(String tableName) {
        List<String> labels
        Map<String, Class> columnTypes = [:]
        url.withReader(encoding) { reader ->
            // get labels and sampleData
            labels = splitAndTrim(reader.readLine())
            List<List<String>> sampleData = []
            3.times { sampleData << splitAndTrim(reader.readLine()) }
            // detect data types
            def dataTypes = detectDataTypes(sampleData)
            columnTypes = [:]
            for (int i = 0; i < Math.min(labels.size(), dataTypes.size()); i++) {
                columnTypes << [(labels[i]): dataTypes[i]]
            }
        }
        columnTypes
    }

    /**
     *
     * @param tableName
     * @param params boolean trim
     * @param closure
     */
    @Override
    void eachRow(String tableName, Map<String, Object> params, Closure closure) {
        url.withReader(encoding) { reader ->
            // skip to start
            int start = params.start ?: 0
            start.times { reader.readLine() }
            Collection<String> labels = params.labels ?: splitAndTrim(reader.readLine())
            String line
            int count = 0
            while ((line = reader.readLine()) != null && (!params.end || (start + count++) < params.end)){
                def data = split(line, params)
                closure(marryDataWithLabels(labels, data))
            }
        }
    }

    /**
     * Splits the row AND trims.
     * @param row
     * @return
     */
    protected List<String> splitAndTrim(String row){
        split(row).collect { it.trim() }
    }

    /**
     * Splits the row based on the configured column dividers.
     * @param row
     * @param params
     * @return
     */
    protected List<String> split(String row, Map<String, Object> params = [:]) {
        def data = []
        int from = 0
        if (columnDividers.isEmpty()) {
            data << row
        } else {
            for (int i = 0; i < columnDividers.size(); i++) {
                int to = columnDividers[i]
                if (to < row.length()) {
                    String record = row.substring(from, to)
                    data << (params.trim ? record.trim() : record)
                    from = to
                } else {
                    break
                }
            }
        }
        if (from < (row.length()-1)){
            String record = row.substring(from)
            data << (params.trim ? record.trim() : record)
        }
        data
    }
}
