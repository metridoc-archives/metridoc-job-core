package metridoc.writers

import metridoc.iterators.RowIterator
import org.slf4j.LoggerFactory

/**
 * Created with IntelliJ IDEA on 6/5/13
 * @author Tommy Barker
 */
abstract class DefaultIteratorWriter<T> implements IteratorWriter<T> {

    Map<String, Object> firstRow

    int offset = 0

    @Delegate
    RowIterator rowIterator

    abstract RowWriter getRowWriter()

    abstract T getResponse()

    synchronized T write() {
        assert rowIterator != null: "row iterator cannot be null"
        def log = LoggerFactory.getLogger(this.getClass())
        if (!rowIterator.hasNext()) {
            log.warn "iterator does not have anymore values, there is nothing to write"
            return null
        }
        try {
            if (rowIterator) {
                firstRow = rowIterator.peek()
            }

            rowIterator.eachWithIndex { Map row, int lineNumber ->
                rowWriter.write(lineNumber + offset, row)
            }
        } finally {
            if (rowIterator instanceof Closeable) {
                silentClose(rowIterator as Closeable)
            }

            if (rowWriter instanceof Closeable) {
                silentClose(rowWriter)
            }

            if (this instanceof Closeable) {
                silentClose(this as Closeable)
            }
        }

        return response
    }

    void silentClose(Closeable closeable) {
        try {
            closeable.close()
        } catch (Exception e) {
            def log = LoggerFactory.getLogger(this.getClass())
            log.warn "could not close $closeable properl, ignoring", e
        }
    }
}
