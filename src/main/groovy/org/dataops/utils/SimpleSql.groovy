package org.dataops.utils

import groovy.sql.Sql

/**
 * Warning: introduces potential for security vulnerabilities.
 */
class SimpleSql implements ISimpleSql {

    Sql sql

    SimpleSql(Sql sql) {
        this.sql = sql
    }

    def rows(query){
        sql.rows(query.toString())
    }

    def execute(query){
        sql.execute(query.toString())
    }

    def executeUpdate(query){
        sql.executeUpdate(query.toString())
    }

    def executeInsert(query){
        sql.executeInsert(query.toString())
    }

    def eachRow(query, Closure closure){
        sql.eachRow(query.toString(), closure)
    }
}
