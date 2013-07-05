package metridoc.writers

import com.google.common.collect.Table
import com.google.common.collect.TreeBasedTable
import metridoc.iterators.RowIterator
import org.apache.commons.lang.ObjectUtils

class TableIteratorWriter extends DefaultIteratorWriter {

    @Override
    WriteResponse write(RowIterator rowIterator) {
        TreeBasedTable<Integer, Object, Object> table = TreeBasedTable.create()
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
        response.response.table = table

        return response
    }

    @Override
    boolean doWrite(int lineNumber, Map record) {
        def table = record.remove("table") as Table
        record.each { columnKey, value ->
            table.put(lineNumber, columnKey, value ?: ObjectUtils.NULL)
        }

        return true
    }
}
