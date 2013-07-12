package metridoc.writers

import metridoc.iterators.Record
import metridoc.iterators.RecordIterator
import org.slf4j.LoggerFactory

import static metridoc.writers.WrittenRecordStat.Status.*

/**
 * Created with IntelliJ IDEA on 6/5/13
 * @author Tommy Barker
 */
abstract class DefaultIteratorWriter implements IteratorWriter<RecordIterator> {

    WriteResponse write(RecordIterator recordIterator) {
        assert recordIterator != null: "record iterator cannot be null"
        def totals = new WriteResponse()
        def log = LoggerFactory.getLogger(this.getClass())
        if (!recordIterator.hasNext()) {
            log.warn "iterator does not have anymore values, there is nothing to write"
            return totals
        }
        try {
            def headers = recordIterator.recordHeaders //headers are persisted through the entire write
            recordIterator.eachWithIndex { Record record, int lineNumber ->
                headers.putAll(record.headers)
                record.headers = headers
                def response = writeRecord(lineNumber, record)
                handleResponse(response)
                totals.addAll(response)
            }

            totals.headers = headers
            return totals
        }
        finally {
            if (recordIterator instanceof Closeable) {
                silentClose(recordIterator as Closeable)
            }

            if (this instanceof Closeable) {
                silentClose(this as Closeable)
            }
        }
    }

    protected void handleResponse(List<WrittenRecordStat> writeResponses) {
        def log = LoggerFactory.getLogger(this.getClass())
        writeResponses.each { response ->
            switch (response.status) {
                case INVALID:
                    log.warn "" +
                            "Invalid record\n" +
                            "   --> line: $response.line\n" +
                            "   --> record: $response.record\n" +
                            "   --> message: $response.throwable.message\n" +
                            "   --> scope: $response.scope.simpleName"
                    break
                case ERROR:
                    log.error "" +
                            "Unexpected exception occurred processing record\n" +
                            "   --> line: $response.line\n" +
                            "   --> record: $response.record\n" +
                            "   --> message: $response.throwable.message\n" +
                            "   --> scope: $response.scope.simpleName"
                    throw response.throwable
            }
        }
    }

    protected void silentClose(Closeable closeable) {
        try {
            closeable.close()
        }
        catch (Exception e) {
            def log = LoggerFactory.getLogger(this.getClass())
            log.warn "could not close $closeable properly, ignoring", e
        }
    }

    protected List<WrittenRecordStat> writeRecord(int line, Record record) {
        def response = new WrittenRecordStat(scope: this.getClass(), record: record, line: line)
        try {
            if (record.throwable) {
                throw record.throwable
            }
            boolean written = doWrite(line, record)
            if (written) {
                response.status = WRITTEN
            }
            else {
                response.status = IGNORED
            }
        }
        catch (AssertionError error) {
            response.status = INVALID
            response.throwable = error
        }
        catch (Throwable throwable) {
            response.status = ERROR
            response.throwable = throwable
        }

        return [response]
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void validateState(field, String message) {
        if (field == null) {
            throw new IllegalStateException(message)
        }
    }

    abstract boolean doWrite(int lineNumber, Record record)
}
