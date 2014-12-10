package org.dataops

/**
 * Created by Nick Grealy on 10/12/2014.
 */
class TestUtils {

    static URL newTmpFileUrl(content){
        def tmp = File.createTempFile('foo','bar')
        tmp.deleteOnExit()
        tmp << content
        tmp.toURI().toURL()
    }
}
