package org.dataops.readers

import org.dataops.utils.URLResolverUtil

import java.nio.file.Files
import java.nio.file.Path

/**
 * Created by Nick Grealy on 10/12/2014.
 */
class DataReaderFactory {

    public static final String MIMETYPE_EXCEL2013 = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                               MIMETYPE_WINCSV = 'application/vnd.ms-excel',
                               MIMETYPE_CSV = 'text/csv'

    /**
     * A register of mimetypes to their associated readers.
     */
    public static final Map<String, Class<? extends AbsDataReader>> registeredReaders = [
            (MIMETYPE_CSV) : CsvReader,
            // as detected under windows
            (MIMETYPE_WINCSV) : CsvReader,
            (MIMETYPE_EXCEL2013) : ExcelReader,
    ]

    static AbsDataReader getReaderByUrlString(String resource, String mimeType = null) {
        getReader(URLResolverUtil.getURL(resource), mimeType)
    }


    static AbsDataReader getReader(URL url, String mimeType = null){
        if (!mimeType){
            mimeType = Files.probeContentType(new File(url.toURI()).toPath())
        }
        if (!registeredReaders.containsKey(mimeType)){
            throw new RuntimeException("No reader for mimeType='$mimeType' has been registered.")
        }
        Class<? extends AbsDataReader> readerClass = registeredReaders.get(mimeType)
        println "-> Retrieved Reader '$readerClass' by mimeType '$mimeType'"
        readerClass.newInstance(url)
    }
}
