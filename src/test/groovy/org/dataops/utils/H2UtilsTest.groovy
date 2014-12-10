package org.dataops.utils

import groovy.mock.interceptor.MockFor

class H2UtilsTest extends GroovyTestCase {

    void testTrue(){
        assert true
    }

//    MockFor mockSql
//
//    void setUp(){
//        mockSql = new MockFor(Sql)
//    }
//
//    void testCreateSchema() {
//        def schema = 'foobar'
//        mockSql.demand.execute { assert it == "create schema if not exists $schema" }
//        mockSql.use {
//            createSchema(schema, new Sql())
//        }
//    }
//
//    void testCleanDbName(){
//        assertEquals 'abc_1234567890________________________________', cleanDbName('abc`1234567890-=~!@#$%^&*()_+[]\\;\',./{}|:"<>? ')
//    }
//
//    void testRead_BadUrlResource(){
//        def msg = shouldFail(RuntimeException){
//            H2Writer.read('classpath://foobar')
//        }
//        assertEquals 'URL Resource cannot be opened - url=\'classpath://foobar\'', msg
//    }

}
