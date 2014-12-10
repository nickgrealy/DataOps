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
        String headers
        url.withReader(encoding) {
            headers = it.readLine()
        }
        splitHeaders(headers).collectEntries { [(it): String] }
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
            (params.start ?: 0).times { reader.readLine() }
            Collection<String> labels = params.labels ?: splitHeaders(reader.readLine())
            String line
            while ((line = reader.readLine()) != null) {
                def data = split(line, params)
                closure(marryDataWithLabels(labels, data))
            }
        }
    }

    protected List<String> splitHeaders(String row){
        split(row).collect { it.trim() }
    }

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
