package metridoc.writers

import com.google.common.collect.TreeBasedTable
import metridoc.iterators.Record
import metridoc.iterators.RecordIterator
import org.apache.commons.lang.ObjectUtils

class TableIteratorWriter extends DefaultIteratorWriter {

    @Override
    WriteResponse write(RecordIterator recordIterator) {
        TreeBasedTable<Integer, Object, Object> table = TreeBasedTable.create()
        assert recordIterator != null: "rowIterator cannot be null"
        def wrappedRowIterator = new RecordIterator() {
            @Override
            protected Record computeNext() {
                if (recordIterator.hasNext()) {
                    def result = recordIterator.next()
                    result.headers.table = table
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
    boolean doWrite(int lineNumber, Record record) {
        def table = record.headers.table
        record.body.each { columnKey, value ->
            table.put(lineNumber, columnKey, value ?: ObjectUtils.NULL)
        }

        return true
    }
}
