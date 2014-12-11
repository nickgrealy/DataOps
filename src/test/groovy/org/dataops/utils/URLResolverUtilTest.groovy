package org.dataops.utils

class URLResolverUtilTest extends GroovyTestCase {



    static URL getExcelClasspath(){
        URLResolverUtil.getURL('classpath:///Excel2013/No Security.xlsx')
    }

    void testGetFileResource() {
        def absPath = new File(getExcelClasspath().toURI()).absolutePath
        URLResolverUtil.getURL("file:/$absPath")
    }

    void testGetClasspathResource() {
        assert getExcelClasspath()
    }

    void testGetResource_MissingFile() {
        def msg = shouldFail(AssertionError) {
             URLResolverUtil.getURL('foo/bar.txt')
        }
        assertEquals "Could not find file by resource='foo/bar.txt'. Expression: file.exists()", msg
    }

    void testGetResource_MissingClasspath() {
        def msg = shouldFail(AssertionError) {
             URLResolverUtil.getURL('classpath:///foo/bar.txt')
        }
        assertEquals "Could not find url by resource='classpath:///foo/bar.txt'. Expression: url. Values: url = null", msg
    }

}
