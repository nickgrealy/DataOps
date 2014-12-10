package org.dataops

/**
 * Created by Nick Grealy on 10/12/2014.
 */
class TestUtils {

    static URL newTmpFileUrl(content, extension = 'csv'){
        def tmp = File.createTempFile('foo',".$extension")
        tmp.deleteOnExit()
        tmp << content
        tmp.toURI().toURL()
    }
}
