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

import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xssf.eventusermodel.XSSFReader
import org.apache.poi.xssf.model.SharedStringsTable
import org.apache.poi.xssf.usermodel.XSSFRichTextString

import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader

import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/16/11
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
class XlsxIterator extends BaseExcelIterator {

    private static log = LoggerFactory.getLogger(XlsxIterator)
    XMLStreamReader reader

    SharedStringsTable stringLookup
    XSSFReader xssfReader

    XSSFReader getXssfReader() {
        if (xssfReader) {
            return xssfReader
        }

        OPCPackage pkg = OPCPackage.open(inputStream)
        xssfReader = new XSSFReader(pkg)
    }

    SharedStringsTable getStringLookup() {

        if (stringLookup) {
            return stringLookup
        }

        stringLookup = getXssfReader().getSharedStringsTable()
    }

    XMLStreamReader getReader() {

        if (reader) {
            return reader
        }

        def sheetReference = getSheetReference()
        def sheet = getXssfReader().getSheet(sheetReference)
        reader = XMLInputFactory.newInstance().createXMLStreamReader(sheet)
    }

    /**
     *
     * @return a fresh workbook reader.  Calling this multiple times will return a different, fresh one starting at the
     * top of the workbook document
     */
    XMLStreamReader getWorkbookReader() {
        def workbook = getXssfReader().getWorkbookData()
        return XMLInputFactory.newInstance().createXMLStreamReader(workbook)
    }

    private static closeXmlStreamReader(XMLStreamReader xmlReader) {
        try {
            xmlReader.close()
        } catch (Exception e) {
            log.warn("An exception occurred closing the xml reader", e)
        }
    }

    private List<XlsxCell> getNextRow(XMLStreamReader reader) {
        getToNextRow(reader)

        if (reader.hasNext()) {
            return getRow(reader)
        }

        return null
    }

    private static void getToNextRow(XMLStreamReader reader) {
        while (!atRowOrEnd(reader)) {
            reader.next()
        }
    }

    private static boolean atRowOrEnd(XMLStreamReader reader) {
        boolean atEnd = !reader.hasNext()
        boolean atRow = false
        if (reader.startElement) {
            atRow = reader.localName == "row"
        }
        return atEnd || atRow
    }

    List convertRowToList(List<XlsxCell> row) {
        def result = []
        def width = getRowWidth(row)

        def cellIterator = row.iterator()
        def currentCell = cellIterator.next()

        (0..(width - 1)).each {zeroBasedColumnIndex ->
            def cellIndex = convertColumnToNumber(currentCell.reference)

            if((zeroBasedColumnIndex + 1) == cellIndex) {
                result.add(currentCell.formattedValue)
                if(cellIterator.hasNext()) {
                    currentCell = cellIterator.next()
                }
            } else {
                result.add(null)
            }
        }

        return result
    }

    private static Map<String, Object> convertRowToHash(List<XlsxCell> row, LinkedHashSet<String> columns) {
        def result = [:]

        def rowIterator = row.iterator()
        int columnIndex = 0
        def currentCell = rowIterator.next()

        columns.each {
            columnIndex++
            int cellIndex = convertColumnToNumber(currentCell.reference)
            if (columnIndex == cellIndex) {
                result.put(it, currentCell.formattedValue)
                if (rowIterator.hasNext()) {
                    currentCell = rowIterator.next()
                }
            } else {
                result.put(it, null)
            }
        }

        return result
    }

    private static Map<String, String> getAttributeMap(XMLStreamReader reader) {
        int attributeCount = reader.attributeCount
        def result = [:]
        for (int i = 0; i < attributeCount; i++) {
            result.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i))
        }

        return result
    }

    static String getSheetReference(XMLStreamReader workbookReader, Closure closure) {
        def run = true

        try {
            while (run) {
                if (workbookReader.startElement) {
                    String localName = workbookReader.localName
                    if (localName == "sheet") {
                        def attributeMap = getAttributeMap(workbookReader)
                        String result = closure.call(attributeMap)
                        if (result) {
                            return result
                        }
                    }
                }

                boolean hasNext = workbookReader.hasNext()
                if (!hasNext) {
                    run = false
                } else {
                    workbookReader.next()
                }
            }
        } finally {
            closeXmlStreamReader(workbookReader)
        }
        return null
    }

    private static String getSheetReferenceByName(XMLStreamReader workbookReader, String name) {
        return getSheetReference(workbookReader) {Map attributeMap ->
            def sheetName = attributeMap.name
            if (sheetName == name) {
                return attributeMap.id
            }
        }
    }

    private static String getSheetReferenceByIndex(XMLStreamReader workbookReader, int index) {
        return getSheetReference(workbookReader) {Map attributeMap ->
            def oneBaseIndex = index + 1
            def sheetId = Integer.valueOf(attributeMap.sheetId)
            if (oneBaseIndex == sheetId) {
                return attributeMap.id
            }
        }
    }

    private void addCellToHash(XlsxCell cell, int columnIndex, Map<String, Object> result) {

        def value = null

        if (cell != null) {
            value = cell.formattedValue
        }

        def columnsAsList = columns as List
        result.put(columnsAsList[columnIndex], value)
    }

    private static int getRowWidth(List<XlsxCell> row) {
        int result = 0

        row.each {XlsxCell cell ->
            int columnNumber = convertColumnToNumber(cell.reference)
            if (columnNumber > result) {
                result = columnNumber
            }
        }

        return result
    }

    private List<XlsxCell> getRow(XMLStreamReader reader) {
        boolean gettingCells = true
        def result = []

        def cell
        while (gettingCells) {
            if (reader.startElement) {
                def name = reader.localName

                switch (name) {
                    case "c":
                        cell = new XlsxCell(stringLookup: getStringLookup())
                        def attributes = getAttributeMap(reader)
                        cell.reference = attributes.r
                        cell.type = attributes.t
                        break
                    case "v":
                        def attributes = getAttributeMap(reader)
                        cell.value = Double.valueOf(reader.getElementText())
                        result.add(cell)
                        break
                }
            }

            if (reader.endElement) {
                def name = reader.localName
                if (name == "row") {
                    gettingCells = false
                }
            }
            reader.next()
        }

        return result
    }

    String getSheetReference() {
        if (sheetName) {
            return getSheetReferenceByName(getWorkbookReader(), sheetName)
        }

        return getSheetReferenceByIndex(getWorkbookReader(), sheetIndex)
    }

    @Override
    List doNext() {
        def row = getNextRow(getReader())

        row ? convertRowToList(row) : null
    }

    private static LinkedHashSet<String> getColumnsFromRowValues(List<XlsxCell> row) {
        int width = getRowWidth(row)
        Map<Integer, XlsxCell> rowMap = [:]

        row.each {cell ->
            rowMap.put(cell.columnIndex, cell)
        }

        LinkedHashSet<String> result = [] as SortedSet
        (1..width).each {
            boolean hasItem = rowMap.containsKey(it)

            if (!hasItem) {
                result.add(it.toString())
            } else {
                String value = String.valueOf(rowMap.get(it).formattedValue)
                result.add(value)
            }
        }

        return result
    }

    @Override
    Iterator<List> doCreate(InputStream inputStream) {
        def args = [inputStream: inputStream]

        if (parameters) {
            args.putAll(parameters)
        }

        return new XlsxIterator(args)
    }
}

class XlsxCell {
    String reference
    String type
    double value
    SharedStringsTable stringLookup

    def getFormattedValue() {
        def result = value
        if (type == "s") {
            int reference = value
            def entry = stringLookup.getEntryAt(reference)
            result = new XSSFRichTextString(entry).toString();
        }

        return result
    }

    int getColumnIndex() {
        XlsxIterator.convertColumnToNumber(reference)
    }
}
