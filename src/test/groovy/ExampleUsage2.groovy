import groovy.sql.Sql
import org.dataops.TestUtils
import org.dataops.readers.DataReaderFactory
import org.dataops.readers.FixedWidthReader
import org.dataops.writers.JDBCWriter

import static org.dataops.readers.DataReaderFactory.*

def db = new JDBCWriter(Sql.newInstance('jdbc:postgresql://localhost:5432/test?user=postgres&password=password'))

db.read(new FixedWidthReader('classpath:///ufs3.chk').configure([columnDividers: [32,34], columnTypes: ['md5', 'blank', 'filename']]), [schemaName: 'main', tableNames: ['ufs3']])


println 'Done.'