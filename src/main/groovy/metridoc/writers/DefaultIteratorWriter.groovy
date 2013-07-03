package metridoc.writers

import metridoc.iterators.RowIterator
import org.slf4j.LoggerFactory

import static metridoc.writers.WriteResponse.Type.*

/**
 * Created with IntelliJ IDEA on 6/5/13
 * @author Tommy Barker
 */
abstract class DefaultIteratorWriter implements IteratorWriter<RowIterator> {

    Map<String, Object> firstRow

    int offset = 0

    WriteResponseTotals write(RowIterator rowIterator) {
        assert rowIterator != null: "row iterator cannot be null"
        def totals = new WriteResponseTotals()
        def log = LoggerFactory.getLogger(this.getClass())
        if (!rowIterator.hasNext()) {
            log.warn "iterator does not have anymore values, there is nothing to write"
            return totals
        }
        try {
            if (rowIterator) {
                firstRow = rowIterator.peek()
            }

            rowIterator.eachWithIndex { Map row, int lineNumber ->
                def response = write(lineNumber + offset, row)
                handleResponse(response)
                totals.addAll(response)
            }

            return totals
        }
        finally {
            if (rowIterator instanceof Closeable) {
                silentClose(rowIterator as Closeable)
            }

            if (this instanceof Closeable) {
                silentClose(this as Closeable)
            }
        }
    }

    void handleResponse(List<WriteResponse> writeResponses) {
        def log = LoggerFactory.getLogger(this.getClass())
        writeResponses.each { response ->
            switch (response.type) {
                case INVALID:
                    log.warn "" +
                            "Invalid record\n" +
                            "   --> record: $response.record\n" +
                            "   --> message: $response.throwable.message"
                    break
                case ERROR:
                    log.error "" +
                            "Unexpected exception occurred processing record\n" +
                            "   --> record: $response.record\n" +
                            "   --> message: $response.throwable.message"
                    throw response.throwable
            }
        }
    }

    void silentClose(Closeable closeable) {
        try {
            closeable.close()
        }
        catch (Exception e) {
            def log = LoggerFactory.getLogger(this.getClass())
            log.warn "could not close $closeable properl, ignoring", e
        }
    }

    @SuppressWarnings("GroovyVariableNotAssigned")
    List<WriteResponse> write(Map<String, Object> record) {
        assert record.containsKey("lineNumber") || record.containsKey("line"): "record must contain integer value with name [line] or [lineNumber]"
        int lineNumber
        try {
            lineNumber = (record.lineNumber ?: record.line) as int
        }
        catch (NumberFormatException ignored) {
            throw new IllegalArgumentException("line number [$lineNumber] is not an integer")
        }

        write(lineNumber, record)
    }

    List<WriteResponse> write(int line, Map<String, Object> record) {
        def response = new WriteResponse(scope: this.getClass(), record: record)
        try {
            boolean written = doWrite(line, record)
            if (written) {
                response.type = WRITTEN
            } else {
                response.type = IGNORED
            }
        }
        catch (AssertionError error) {
            response.type = INVALID
            response.throwable = error
        }
        catch (Throwable throwable) {
            response.type = ERROR
            response.throwable = throwable
        }

        return [response]
    }

    void validateState(field, String message) {
        if (field == null) {
            throw new IllegalStateException(message)
        }
    }

    abstract boolean doWrite(int lineNumber, Map<String, Object> record)
}
