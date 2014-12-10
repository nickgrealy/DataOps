package org.dataops.readers

import org.dataops.utils.URLResolverUtil

import java.nio.file.Files

/**
 * Created by Nick Grealy on 10/12/2014.
 */
class DataReaderFactory<DataReader extends Class<? extends AbsDataReader>> {

    public static final String MIMETYPE_EXCEL2013 = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                               MIMETYPE_WINCSV = 'application/vnd.ms-excel',
                               MIMETYPE_CSV = 'text/csv',
                               MIMETYPE_FIXEDWIDTH = 'text/enriched'

    static {
        registerReader(MIMETYPE_CSV, 'csv', CsvReader)
        registerReader(MIMETYPE_WINCSV, 'csv', CsvReader)
        registerReader(MIMETYPE_EXCEL2013, 'xlsx', ExcelReader)
        registerReader(MIMETYPE_FIXEDWIDTH, 'txt', FixedWidthReader)
    }

    public static void registerReader(String mimeType, String fileExtension, DataReader readerClass){
        fileExtension = fileExtension?.toLowerCase()
        fileExtensionToReader.put(fileExtension, readerClass)
        mimeTypeToReader.put(mimeType, readerClass)
    }

    protected static final Map<String, DataReader> fileExtensionToReader = [:]
    protected static final Map<String, DataReader> mimeTypeToReader = [:]

    static AbsDataReader getReaderByUrlString(String resource, String mimeType = null) {
        getReader(URLResolverUtil.getURL(resource), mimeType)
    }

    static AbsDataReader getReader(URL url, String mimeType = null){
        Class<? extends AbsDataReader> readerClass

        // get reader by param mimeType
        readerClass = mimeTypeToReader.get(mimeType)

        // get reader by detected mimeType
        if (readerClass == null){
            try {
                readerClass = mimeTypeToReader.get(Files.probeContentType(new File(url.toURI()).toPath()))
            } catch (IllegalArgumentException t){ /* ignore 'URI scheme is not "file"' */ }
        }

        // get reader by file extension
        if (readerClass == null){
            def fileExt = (url =~ /[^\.]+$/).getAt(0).toString().toLowerCase()
            readerClass = fileExtensionToReader.get(fileExt)
        }

        // still no reader?...
        if (readerClass == null){
            throw new RuntimeException("Could not detect MimeType from file type or extension. Please specify using the 'mimeType' option (or register a custom reader!). url='$url'")
        }
        readerClass.newInstance(url)
    }
}
