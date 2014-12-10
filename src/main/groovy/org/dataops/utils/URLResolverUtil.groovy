package org.dataops.utils

class URLResolverUtil {

    static URL getURL(String resource){
        URL url
        if (resource.startsWith('classpath://')) {
            url = ClassLoader.getResource(resource.substring(12))
        } else {
            try {
                url = new URL(resource)
            } catch (MalformedURLException e){
                def file = new File(resource)
                assert file.exists(), "Could not find file by resource='$resource'"
                url = file.toURI().toURL()
            }
        }
        assert url, "Could not find url by resource='$resource'"
        url
    }

    static String getFileName(URL  url){
        def path = url.toURI().path
        return path.substring(path.lastIndexOf('/') + 1)
    }

}
