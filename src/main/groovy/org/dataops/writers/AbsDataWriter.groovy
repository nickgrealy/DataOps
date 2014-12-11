package org.dataops.writers

import org.dataops.readers.AbsDataReader
import org.dataops.readers.DataReaderFactory
import org.dataops.utils.URLResolverUtil

abstract class AbsDataWriter<DataWriterImpl extends AbsDataWriter> {

    /**
     * Reads data from the DataReader into the DataWriter.
     *
     * @param reader
     * @param options String schemaName, List<String> tablesNames, String mimeType
     * @return
     */
    abstract DataWriterImpl read(AbsDataReader reader, Map<String, Object> options)

    /**
     * Reads data from the DataReader into the DataWriter.
     *
     * @param reader
     * @param options String schemaName, List<String> tablesNames, String mimeType
     * @return
     */
    DataWriterImpl read(String resource, Map<String, Object> options = [:]) {
        URL url = URLResolverUtil.getURL(resource)
        read(url, options)
        this
    }

    /**
     * Reads data from the DataReader into the DataWriter.
     *
     * @param reader
     * @param options String schemaName, List/Map tablesNames, String mimeType
     * @return
     */
    DataWriterImpl read(URL url, Map<String, Object> options = [:]) {
        def reader = DataReaderFactory.getReader(url, options.mimeType)
        read(reader, options)
        this
    }

}