package org.dataops.readers

import org.junit.Ignore

import static org.dataops.readers.DataReaderFactory.*

/**
 * Created by Nick Grealy on 11/12/2014.
 */
class DataReaderFactoryTest extends GroovyTestCase {

    void testGetByParamMimeType(){
        assert getReader(new URL("http://foo/bar.csv"), MIMETYPE_FIXEDWIDTH) instanceof FixedWidthReader
    }

    @Ignore('This is system dependent!')
    void testGetByDetectedMimeType(){
        // System dependent
    }

    void testGetByFileExtension(){
        assert getReader(new URL("http://foo/bar.csv")) instanceof CsvReader
    }

    void testNoValidDetectionStrategy(){
        def msg = shouldFail(RuntimeException){
            getReader(new URL("http://foo/bar"))
        }
        assert "Could not detect MimeType from file type or extension. Please specify using the 'mimeType' option (or register a custom reader!). url='http://foo/bar'", msg
    }
}
