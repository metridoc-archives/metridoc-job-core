package metridoc.writers

import com.google.common.collect.TreeBasedTable
import metridoc.iterators.RowIterator
import org.apache.commons.lang.ObjectUtils

class TableIteratorWriter extends DefaultIteratorWriter {

    @Override
    WriteResponseTotals write(RowIterator rowIterator) {
        TreeBasedTable<Integer, String, Object> table = TreeBasedTable.create()
        assert rowIterator: "rowIterator cannot be null"
        def wrappedRowIterator = new RowIterator() {
            @Override
            protected Map computeNext() {
                if (rowIterator.hasNext()) {
                    def result = rowIterator.next()
                    result.table = table
                    return result
                }

                endOfData()
            }
        }
        def response = super.write(wrappedRowIterator)
        response.data.table = table

        return response
    }

    @Override
    boolean doWrite(int lineNumber, Map<String, Object> record) {
        def table = record.remove("table")
        record.each { String columnKey, value ->
            table.put(lineNumber, columnKey, value ?: ObjectUtils.NULL)
        }

        return true
    }
}
