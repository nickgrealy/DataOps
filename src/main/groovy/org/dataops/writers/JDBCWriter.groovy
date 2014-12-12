package org.dataops.writers

import groovy.sql.Sql
import groovy.util.logging.Log
import org.dataops.readers.AbsDataReader
import org.dataops.utils.ISimpleSql
import org.dataops.utils.SimpleSql

import java.sql.SQLException

import static org.dataops.utils.H2Utils.*

@Log
class JDBCWriter extends AbsDataWriter<JDBCWriter> implements ISimpleSql {

    Sql sql
    SimpleSql simpleSql
    Properties properties = new Properties(
//            [
//            schemaName: null, // String
//            tableNames: null, // List/Map
//            columnTypes: null // Map
//    ]
    )

    /* Constructors */

    JDBCWriter() {
        Class.forName('org.h2.Driver')
        setSql Sql.newInstance([url: 'jdbc:h2:mem:db1', user: 'sa', password: 'sa', driver: 'org.h2.Driver'])
    }

    JDBCWriter(Sql sqlConnection){
        setSql sqlConnection
    }

    JDBCWriter(String sqlConnection){
        setSql Sql.newInstance(sqlConnection)
    }

    /* Methods */

    JDBCWriter configure(Map<String, Object> options){
        properties.putAll(options)
        this
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
    JDBCWriter read(AbsDataReader reader, Map<String, Object> optionsx = [:]) {
        properties.putAll(optionsx)
        String schemaName = properties.schemaName
        List<String> tableNames = reader.tableNames
        if (properties.tableNames){
            if (properties.tableNames instanceof List){
                tableNames = properties.tableNames
            } else if (properties.tableNames instanceof Map){
                tableNames = tableNames.intersect(properties.tableNames?.keySet() ?: tableNames)
            }
        }
        assert tableNames, "'tableNames' option must be supplied!"
        if (schemaName){
            schemaName = cleanDbName(schemaName)
            try {
                createSchema(schemaName, sql)
            } catch (SQLException e){
                println "Caught exception creating schema: $e.message"
            }
        }
        // create tables
        tableNames.each { tableName ->
            Map<String, Class> columnTypes = properties.columnTypes ?: reader.getColumnTypes(tableName)
            // clean column names
            columnTypes = columnTypes.collectEntries { k,v -> [cleanDbName(k), v]}
            String dbTableName = properties.tableNames instanceof Map ? properties.tableNames[tableName] : cleanDbName(tableName)
            try {
                createTable(schemaName, dbTableName, columnTypes, sql)
            } catch (SQLException e){
                println "Caught exception creating table: $e.message"
            }
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
