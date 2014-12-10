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