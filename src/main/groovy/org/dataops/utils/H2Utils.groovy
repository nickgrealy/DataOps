package org.dataops.utils

import groovy.sql.Sql
import groovy.util.logging.Log
import org.apache.poi.ss.usermodel.Row

import java.sql.ResultSet

//import org.slf4j.Logger
//import org.slf4j.LoggerFactory

/**
 * Created by Nick Grealy on 18/11/2014.
 */
@Log
class H2Utils {

    public static final int MAX_COL_LENGTH = 2048

    static List resultSetToList(ResultSet resultSet){
        List list = []
        while (resultSet.next()){
            list << resultSet.toRowResult()
        }
        list
    }

    static List<String> getSchemas(Sql sql){
        resultSetToList(sql.connection.metaData.schemas).collect { it.table_schem?.toLowerCase() }
    }

    static def createSchema(schema, Sql sqlConnection) {
        schema = cleanDbName schema
        def sql = "create schema if not exists $schema".toString()
        log.info sql
        sqlConnection.execute sql
    }

    static def createTable(schema, table, Map<String, Class> columnTypes, Sql sqlConnection) {
        def map = [
                (String) : "varchar(${MAX_COL_LENGTH})",
                (Double) : 'decimal',
                (Integer) : 'integer',
                (Boolean): 'boolean',
                (Date): 'timestamp',
                (BigDecimal) : 'decimal',
                // primitives
                (double) : 'decimal',
                (int) : 'integer',
                (boolean): 'boolean',
                (org.codehaus.groovy.runtime.NullObject): 'varchar(1024) null',
        ]
        if (schema){ schema = cleanDbName schema }
        table = cleanDbName table
        def cols = columnTypes.collect {
            def colname = cleanDbName it.key
            def coltype = map[it.value]
            if (!map.containsKey(it.value)){
                throw new RuntimeException("Unmapped class type. schema=$schema table=$table column='${it.key}' class='${it.value}'")
            }
            def pkmatch = it =~ /^id$|^id_|_id$|_id_/
            def pk = pkmatch.size() > 0 ? 'primary key' : ''
            "$colname $coltype $pk"
        }.join(",\n    "
        )
        def sql = "create table ${schema ? schema+"."+table : table} (\n    ${cols}\n)".toString()
        log.info sql
        sqlConnection.execute sql
    }

    static def cleanDbName(name) {
        name.replaceAll("[^\\w]", "_").toLowerCase()
    }

    /**
     *
     * @param props [url:'', sqlConnection: sqlConn, createSchema:true, createTable:true]
     * @return
     */
//    static def read(Map props) {
//        Sql sqlConnection = props.sqlConnection
//        String url = props.url
//        Boolean createSchemaFlag = props.createSchema ?: true
//        Boolean createTableFlag = props.createTable ?: true
//        assert sqlConnection
//        assert url
//        assert createSchemaFlag
//        assert createTableFlag
//
//        def resource = FileResolverUtil.getInputStreamResource(url)
//        def schema = cleanDbName resource.name
//        def tables = []
//        try {
//            resource.resource.withInputStream { is ->
//                def builder = new ExcelBuilder(is)
//                def sheetNames = builder.workSheets
//                sheetNames.each { String sheetName ->
//                    def table = cleanDbName sheetName
//                    tables << table
//                    // create schema
//                    if (createSchemaFlag) {
//                        createSchema(schema, sqlConnection)
//                    }
//                    // create table
//                    def dataTypes = builder.getColumnTypes(sheetName)
//                    if (createTableFlag) {
//                        createTable(schema, table, dataTypes, sqlConnection)
//                    }
//                    // insert data
//                    def dataSet = sqlConnection.dataSet("${schema}.${table}")
//                    builder.eachRow([labels: true, sheet: sheetName]) { Row row ->
//                        Map map = row.asMap(dataTypes)
//                        dataSet.add(map)
//                    }
//                }
//            }
//        } catch (NullPointerException npe){
//            throw new RuntimeException("URL Resource cannot be opened - url='$url'", npe)
//        }
//        return [schema: schema, tables: tables]
//    }

}
