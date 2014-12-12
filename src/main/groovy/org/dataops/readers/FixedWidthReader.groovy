package org.dataops.readers

/**
 * Created by Nick Grealy on 10/12/2014.
 */
class FixedWidthReader extends AbsDataReader {

    Properties properties = new Properties([
            encoding: 'UTF-8',
            columnDividers: [],
            columnTypes: (0..99).collect{"column_$it"}
    ])

    FixedWidthReader(String resource) {
        super(resource)
    }

    FixedWidthReader(URL url) {
        super(url)
    }

    FixedWidthReader configure(Map<String, Object> options) {
        properties.putAll(options)
        this
    }

    @Override
    Map<String, Class> getColumnTypes(String tableName) {
        if (properties.columnTypes instanceof List){
            return properties.columnTypes.collectEntries{ [it, String] }
        } else if (properties.columnTypes instanceof Map){
            return properties.columnTypes
        } else {
            Map<String, Class> columnTypes = [:]
            url.withReader(properties.encoding) { reader ->
                // get labels and sampleData
                List<String> labels = splitAndTrim(reader.readLine())
                List<List<String>> sampleData = []
                3.times { sampleData << splitAndTrim(reader.readLine()) }
                // detect data types
                def dataTypes = detectDataTypes(sampleData)
                columnTypes = [:]
                for (int i = 0; i < Math.min(labels.size(), dataTypes.size()); i++) {
                    columnTypes << [(labels[i]): dataTypes[i]]
                }
            }
            return columnTypes
        }
    }

    /**
     *
     * @param tableName
     * @param options boolean trim
     * @param closure
     */
    @Override
    void eachRow(String tableName, Map<String, Object> options, Closure closure) {
        properties.putAll(options)
        url.withReader(properties.encoding) { reader ->
            // skip to start
            int start = properties.start ?: (properties.columnTypes ? 1 : 0)
            start.times { reader.readLine() }
            Collection<String> labels = properties.columnTypes?.keySet() ?: splitAndTrim(reader.readLine())
            String line
            int count = 0
            while ((line = reader.readLine()) != null && (!properties.end || (start + count++) < properties.end)){
                def data = split(line)
                def dataMap = marryDataWithLabels(labels, data)
                if (properties.columnTypes){
                    dataMap = doDataTypeConversionIfRequired(properties.columnTypes, dataMap)
                }
                closure(dataMap)
            }
        }
    }


    void eachRow(String tableName,
                 Integer start,
                 Integer end,
                 Collection<String> labels,
                 Map<String, Class> columnTypes,
                 Closure closure) {

        url.withReader(properties.encoding) { reader ->
            start.times { reader.readLine() }
            labels = labels ?: splitAndTrim(reader.readLine())
            String line
            int count = 0
            while ((line = reader.readLine()) != null && (!end || (start + count++) < end)){
                def dataMap = marryDataWithLabels(labels, split(line))
                dataMap = doDataTypeConversionIfRequired(columnTypes, dataMap)
                closure(dataMap)
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
     * @param options
     * @return
     */
    protected List<String> split(String row) {
        properties.putAll(properties)
        def data = []
        int from = 0
        if (properties.columnDividers.isEmpty()) {
            data << row
        } else {
            for (int i = 0; i < properties.columnDividers.size(); i++) {
                int to = properties.columnDividers[i]
                if (to < row.length()) {
                    String record = row.substring(from, to)
                    data << (properties.trim ? record.trim() : record)
                    from = to
                } else {
                    break
                }
            }
        }
        if (from < row.length()){
            String record = row.substring(from)
            data << (properties.trim ? record.trim() : record)
        }
        data
    }
}
