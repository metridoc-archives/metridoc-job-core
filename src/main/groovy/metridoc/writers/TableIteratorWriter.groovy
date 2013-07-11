package metridoc.writers

import com.google.common.collect.TreeBasedTable
import metridoc.iterators.Record
import metridoc.iterators.RecordIterator
import org.apache.commons.lang.ObjectUtils

class TableIteratorWriter extends DefaultIteratorWriter {

    @Override
    WriteResponse write(RecordIterator recordIterator) {
        assert recordIterator != null: "rowIterator cannot be null"
        recordIterator.recordHeaders.table = TreeBasedTable.create()
        def response = super.write(recordIterator)
        response.body.table = response.headers.table

        return response
    }

    @Override
    boolean doWrite(int lineNumber, Record record) {
        def headers = record.headers
        def table = headers.table
        record.body.each { columnKey, value ->
            table.put(lineNumber, columnKey, value ?: ObjectUtils.NULL)
        }

        return true
    }
}
