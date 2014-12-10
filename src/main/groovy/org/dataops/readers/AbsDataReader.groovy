package org.dataops.readers

import org.dataops.utils.URLResolverUtil

abstract class AbsDataReader {

    URL url

    /**
     * Constructor, which will use {@link URLResolverUtil} to resolve the URL.
     * @param resource
     */
    AbsDataReader(String resource) {
        this.url = URLResolverUtil.getURL(resource)
    }

    /**
     * Default constructor.
     * @param inputStream - the data source (file) input stream.
     */
    AbsDataReader(URL url) {
        this.url = url
    }

    protected Map marryDataWithLabels(Collection<String> labels, List<Object> data){
        (0..<Math.min(labels.size(), data.size())).collectEntries { [labels[it], data[it]] }
    }

    /**
     * Returns a list of table names, for all the available data sets.
     * @return by default, returns 'data'
     */
    List<String> getTableNames() {
        return ['data']
    }

    /**
     * Iterates over each row in the data set.
     *
     * @param tableName
     * @param closure
     */
    void eachRow(String tableName, Closure closure){
        eachRow(tableName, [:], closure)
    }

    /**
     * Detects the data types for a sample set of data.
     * @param sampleDataRows
     * @return
     */
    protected List<Class> detectDataTypes(List<List<Object>> sampleDataRows){
        List<Class> dataTypes = []
        def rowIterator = sampleDataRows.iterator()
        rowIterator.next().eachWithIndex { Object entry, int i ->
            dataTypes << detectDataType(entry)
        }
        // TODO if there's more than one row, check them for data type consistency
        dataTypes
    }

    /**
     * Detects the data type for a sample data object.
     * @param entry
     * @return
     */
    protected Class detectDataType(entry){
        if (entry == null){ return String }
        // check current types
        if (entry instanceof BigDecimal){ return BigDecimal }
        if (entry instanceof Boolean){ return Boolean }
        if (entry instanceof Date){ return Date }
        // attempt to construct objects
        try { new BigDecimal(entry); return BigDecimal } catch (Throwable t){}
        try { new Date(entry); return Date } catch (Throwable t){}
        if (['true', 'false'].contains(entry.toString().toLowerCase())){ return Boolean }
        return String
    }

    /* Abstract */

    /**
     * Returns a map of column names to column types.
     * @param tableName
     * @return
     */
    abstract Map<String, Class> getColumnTypes(String tableName)

    /**
     * Iterates over each row in the data set.
     *
     * @param tableName
     * @param params int start, List<String> labels
     * @param closure
     */
    abstract void eachRow(String tableName, Map<String, Object> params, Closure closure)
}