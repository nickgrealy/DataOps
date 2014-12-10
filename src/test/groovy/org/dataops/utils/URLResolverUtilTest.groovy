package org.dataops.utils

class URLResolverUtilTest extends GroovyTestCase {


    public static final String CLASSPATH_RESOURCE = 'classpath:///Excel2013/No Security.xlsx'

    void testGetFileResource() {
        def absPath = new File(URLResolverUtil.getURL(CLASSPATH_RESOURCE).toURI()).absolutePath
        URLResolverUtil.getURL("file:/$absPath")
    }

    void testGetClasspathResource() {
        assert URLResolverUtil.getURL(CLASSPATH_RESOURCE)
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
