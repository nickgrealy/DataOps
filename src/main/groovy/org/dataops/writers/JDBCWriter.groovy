package org.dataops.writers

import groovy.sql.Sql
import groovy.util.logging.Log
import org.dataops.readers.AbsDataReader
import org.dataops.utils.ISimpleSql
import org.dataops.utils.SimpleSql

import static org.dataops.utils.H2Utils.*

@Log
class JDBCWriter extends AbsDataWriter<JDBCWriter> implements ISimpleSql {

    Sql sql
    SimpleSql simpleSql

    /* Constructors - Default Sql connection */

    JDBCWriter() {
        Class.forName('org.h2.Driver')
        setSql Sql.newInstance([url: 'jdbc:h2:mem:db1', user: 'sa', password: 'sa', driver: 'org.h2.Driver'])
    }

    JDBCWriter(String resource, Map<String, Object> options = [:]) {
        this()
        read(resource, options)
    }

    JDBCWriter(URL url, Map<String, Object> options = [:]) {
        this()
        read(url, options)
    }

    /* Constructors - Sql connection supplied */

    JDBCWriter(Sql sql){
        setSql sql
    }

    JDBCWriter(Sql sql, String resource, Map<String, Object> options = [:]) {
        this(sql)
        read(resource, options)
    }

    JDBCWriter(Sql sql, URL url, Map<String, Object> options = [:]) {
        this(sql)
        read(url, options)
    }

    void setSql(Sql sql) {
        this.sql = sql
        this.simpleSql = new SimpleSql(sql)
    }

    /* DataWriter interface implementation methods */

    // TODO: test null schema
    /**
     *
     * @param reader
     * @param options String schemaName, List<String> tableNames, Map<String, Class> columnTypes
     * @return
     */
    JDBCWriter read(AbsDataReader reader, Map<String, Object> options = [:]) {
        String schemaName = options.schemaName
        List<String> tableNames = reader.tableNames
        if (options.tableNames){
            if (options.tableNames instanceof List){
                tableNames = options.tableNames
            } else if (options.tableNames instanceof Map){
                tableNames = tableNames.intersect(options.tableNames?.keySet() ?: tableNames)
            }
        }
        assert tableNames, "'tableNames' option must be supplied!"
        if (schemaName){
            schemaName = cleanDbName(schemaName)
            createSchema(schemaName, sql)
        }
        // create tables
        tableNames.each { tableName ->
            Map<String, Class> columnTypes = options.columnTypes ?: reader.getColumnTypes(tableName)
            // clean column names
            columnTypes = columnTypes.collectEntries { k,v -> [cleanDbName(k), v]}
            String dbTableName = options.tableNames instanceof Map ? options.tableNames[tableName] : cleanDbName(tableName)
            createTable(schemaName, dbTableName, columnTypes, sql)
            // write data
            def dataSetName = schemaName ? "${schemaName}.${dbTableName}" : dbTableName
            def dataSet = sql.dataSet(dataSetName)
            int recordCount = 0
            reader.eachRow(tableName, [columnTypes: columnTypes]) { Map<String, Object> row ->
                recordCount++
                if (recordCount % 10000 == 0){
                    println "Saving record ${recordCount}..."
                }
                try {
                    // Trim value, if value is string and exceeds max length...
                    row.each{ k,v -> if (v instanceof String && v.length() > MAX_COL_LENGTH){ row[k] = v.substring(0, MAX_COL_LENGTH) }}
                    dataSet.add(row)
                } catch (Throwable t){
                    throw new RuntimeException("Exception occurred saving data='$row'", t)
                }
            }
        }
        this
    }

    /* ISimpleSql interface implementation methods */

    @Override
    def rows(Object query) {
        simpleSql.rows(query)
    }

    @Override
    def execute(Object query) {
        simpleSql.execute(query)
    }

    @Override
    def executeUpdate(Object query) {
        simpleSql.executeUpdate(query)
    }

    @Override
    def executeInsert(Object query) {
        simpleSql.executeInsert(query)
    }

    @Override
    def eachRow(Object query, Closure closure) {
        simpleSql.eachRow(query, closure)
    }
}
