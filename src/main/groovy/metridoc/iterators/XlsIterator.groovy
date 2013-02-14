/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.iterators


import groovy.util.logging.Slf4j
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.DateUtil

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/15/11
 * Time: 3:24 PM
 */
@Slf4j
class XlsIterator extends BaseExcelIterator {

    Sheet sheet

    private void initializeSheetIfNull() {
        if (sheet == null) {
            Workbook workbook = WorkbookFactory.create(getInputStream())
            if (sheetName) {
                sheet = workbook.getSheet(sheetName)
            } else {
                sheet = workbook.getSheetAt(sheetIndex)
            }
        }
    }

    @Override
    Iterator<List> doCreate(InputStream inputStream) {
        def args = [inputStream: inputStream]
        if (parameters) {
            args.putAll(parameters)
        }
        return new XlsIterator(args)
    }



    @Override
    List doNext() {
        def result = []
        def rowNum = getRowNum()

        log.debug("retrieving row {} for sheet {}", rowNum, getSheet().sheetName)
        Row row = getSheet().getRow(rowNum)
        if(row == null) {
            return null
        }

        def lastCellIndex = row.lastCellNum

        (0..(lastCellIndex - 1)).each {
            def cell = row.getCell(it)
            result.add(getCellValue(cell))
        }

        return result
    }

    private static Object getCellValue(Cell cell) {

        if (cell == null) {
            return null
        }

        switch (cell.getCellType()) {

            case Cell.CELL_TYPE_STRING:
                return cell.richStringCellValue.getString()
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.dateCellValue
                } else {
                    return cell.numericCellValue
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.booleanCellValue
            case Cell.CELL_TYPE_FORMULA:
                return cell.cellFormula
            default:
                return null
        }
    }

    Sheet getSheet() {
        initializeSheetIfNull()
        return sheet
    }
}
