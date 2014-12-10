import groovy.sql.Sql
import org.dataops.writers.JDBCWriter

//def db = new JDBCWriter(Sql.newInstance([url: 'jdbc:h2:file:c:/tmp/ubuntu/compare', user: 'sa', password: 'sa', driver: 'org.h2.Driver']))
//def db = new H2Writer(Sql.newInstance([url: 'jdbc:h2:mem:foobar', user: 'sa', password: 'sa', driver: 'org.h2.Driver']))


def db = new JDBCWriter()
//def db = new JDBCWriter(Sql.newInstance([url: 'jdbc:h2:mem:foobar', user: 'sa', password: 'sa', driver: 'org.h2.Driver']))
db.read('classpath:///Excel2013/No Security.xlsx')

//db.eachRow("select * from data")

println 'Done.'

//['ufs3', 'ufs2', 'ext4', 'ufs1'].each { data ->
//    println "== New Schema: ${data} == "
//    h2db.read(new FixedWidthReader("c:/tmp/ubuntu/${data}.chk").configure([32,34]), [schemaName: data, columnTypes: [
//            md5: String, blank: String, filename: String
//    ]])
//}

//['ufs3', 'ufs2', 'ext4', 'ufs1'].each { data ->
//    println h2db.executeUpdate("create index ${data}idx on ${data}.data(md5)")
//}

//h2db.execute("""
//create or replace view hdd_comparison as
//    select
//""")

//h2db.eachRow( """
//select ext4.filename as ext4dir, ufs3.filename as ufs3dir
//from ext4.data ext4
//inner join ufs3.data ufs3 on ext4.md5 = ufs3.md5
//limit 200
//""") {
//    println it
//}

println "Done."
//
//def props = h2db.sql.rows("select * from datatypes.environmentvariables where application = $app").collectEntries { row ->
//    [row.key, row[env]]
//}
//props.each { println it }

//def tmp = new Properties()
//tmp.putAll(props)
//new File('deleteme.properties').withOutputStream { os ->
//    tmp.store(os, " $app '$env' Environment Variables")
//}

//def h2db = new H2Writer('classpath:///CSV/DataTypes - MSDOS.csv', [
//        schemaName: 'datatypes',
//        tableNames: ['csv'],
//        columnTypes: [
//                'strings': String,
//                'integers': int,
//                'decimals': double,
//                'booleans': boolean,
////                'dates': Date,
////                'times': Date,
////                'datetimes': Date,
////                'currencies': double,
////                'percentages': double,
//        ]
//])

//def h2 = new H2Writer('C:\\workspace\\TeamworksDeploymentScripts\\config\\config\\BPM ATP Environment Configuration.xlsx',
//        [schemaName: 'atp', tableNames: [ 'DeploymentWebService']]) // 'EnvironmentVariables', 'DeploymentWebService',

