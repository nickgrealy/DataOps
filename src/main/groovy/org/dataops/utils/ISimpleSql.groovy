package org.dataops.utils

/**
 * Warning: introduces potential for security vulnerabilities.
 */
interface ISimpleSql {

    def rows(query)

    def execute(query)

    def executeUpdate(query)

    def executeInsert(query)

    def eachRow(query, Closure closure)

}
