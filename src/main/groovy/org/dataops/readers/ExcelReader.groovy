package org.dataops.readers

import org.apache.poi.ss.usermodel.*
import org.codehaus.groovy.runtime.NullObject

class ExcelReader extends AbsDataReader {

    static final DataFormatter formatter = new DataFormatter()
    Workbook workbook
    Collection<String> labels
    Row row

    /* Constructors */

    ExcelReader(String resource) {
        super(resource)
    }

    ExcelReader(URL url) {
        super(url)
        url.withInputStream { InputStream it ->
            this.workbook = WorkbookFactory.create(it)
        }
        initMeta()
    }

    /* Methods */

    private def initMeta() {
        Row.metaClass.getAt = { int idx ->
            Cell cell = delegate.getCell(idx)
            return getCellValue(cell)
        }
        Row.metaClass.propertyMissing = { String name ->
            def idx = labels.indexOf(name)
            if (idx < 0) {
                throw new RuntimeException("Label not found! label='$name'")
            }
            delegate[idx]
        }
        Row.metaClass.asMap = { Map<String, Class> dataTypes ->
            def idx = 0
            if (dataTypes) {
                return dataTypes.collectEntries { String colname, Class coltype ->
                    Cell cell = delegate.getCell(idx++)
                    [colname, getCellValue(cell, coltype)]
                }
            } else {
                return labels.collectEntries { String colname ->
                    Cell cell = delegate.getCell(idx++)
                    [colname, getCellValue(cell)]
                }
            }
        }
    }

    def getCellValue(CellValue cell) {
        if (!cell) {
            return null
        }
        def value
        switch (cell.cellType) {
            case Cell.CELL_TYPE_NUMERIC:
                value = cell.numberValue
                break
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.booleanValue
                break
            default:
                value = cell.stringValue
                break
        }
        return value
    }

    static def prepareBigDecimalString(value) {
        value.replaceAll("[^-+0-9eE\\.]", "") // strip currency operators
    }

    def getCellValue(Cell cell, Class targetType = null) {
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator()
        if (!cell) {
            return null
        }
        def value
        if (targetType == String) {
            value = formatter.formatCellValue(cell, evaluator)
            // OR cell.setCellType(Cell.CELL_TYPE_STRING)
        } else {
            switch (cell.cellType) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = cell.dateCellValue
                    } else {
                        def b4 = formatter.formatCellValue(cell, evaluator)
                        def bd = new BigDecimal(prepareBigDecimalString(b4))
                        if (b4.contains('%')) {
                            // percentage
                            value = bd.divide(new BigDecimal(100))
                        } else if (b4.contains('/')) {
                            // fraction
                            value = cell.numericCellValue
                        } else {
                            value = bd
                        }
//                        println "BigDecimal'ing => '$b4' -> '$value'"
                    }
                    break
                case Cell.CELL_TYPE_BOOLEAN:
                    value = cell.booleanCellValue
                    break
                case Cell.CELL_TYPE_FORMULA:
                    value = getCellValue(evaluator.evaluate(cell))
                    break;
                default:
                    value = cell.stringCellValue
                    break
            }
        }
        return value
    }

    Sheet getWorkSheet(idx) {
        if (idx instanceof Sheet) {
            return idx
        }
        def sheet
        if (!idx) idx = 0
        if (idx instanceof Number) {
            sheet = workbook.getSheetAt(idx)
        } else if (idx ==~ /^\d+$/) {
            sheet = workbook.getSheetAt(Integer.valueOf(idx))
        } else {
            // get by sheet name
            sheet = workbook.getSheet(idx)
        }
        assert sheet, "No sheet could be found using identifier='$idx'"
        return sheet
    }

    private static def lcdDataTypes(Class a, Class b) {
        if (a == NullObject) {
            return b
        }
        if (b == NullObject) {
            return a
        }
        return a == b ? a : String
    }

    /* Interface implementation */

    @Override
    List<String> getTableNames() {
        def sheets = []
        int num = workbook.numberOfSheets
        for (int i = 0; i < num; i++) {
            sheets << workbook.getSheetName(i)
        }
        sheets
    }

    @Override
    Map<String, Class> getColumnTypes(String tablename) {
        def sampleData
        eachRow(tablename, [end: 5]) { Map<String, Object> row ->
            def columnToValueMap = row //.asMap()
            def tmp = columnToValueMap.collectEntries {
                // get column -> class mapping
                def clazz = it.value.getClass()
                if (Date.isAssignableFrom(clazz)){
                    clazz = java.sql.Date
                }
                [it.key, clazz]
            }
            if (sampleData) {
                tmp.each { k, v ->
                    sampleData[k] = lcdDataTypes(sampleData[k], v)
                }
            } else {
                sampleData = tmp
            }
        }
        sampleData
    }

    @Override
    void eachRow(String tableName, Map<String, Object> params, Closure closure) {
        int start = params.start ?: (params.columnTypes ? 1 : 0)
        def sheet = getWorkSheet(tableName)
        Iterator<Row> rowIterator = sheet.rowIterator()

        labels = params.columnTypes?.keySet() ?: getHeaders(tableName, start++)
        start.times { rowIterator.next() }
        closure.setDelegate(this)

        int count = 0
        while (rowIterator.hasNext() && (!params.end || (start + count++) < params.end)){
            def dataMap = rowIterator.next().asMap()
            if (params.columnTypes){
                dataMap = doDataTypeConversionIfRequired(params.columnTypes, dataMap)
            }
            closure(dataMap)
        }
    }

    List<String> getHeaders(worksheet, rowIndex = 0) {
        def tmp = []
        row = getWorkSheet(worksheet).getRow(rowIndex)
        for (int i = row.firstCellNum; i < row.lastCellNum; i++) {
            tmp << row[i].toString()
        }
        tmp
    }
}