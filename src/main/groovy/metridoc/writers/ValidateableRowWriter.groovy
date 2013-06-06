package metridoc.writers

import groovy.util.logging.Slf4j

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created with IntelliJ IDEA on 6/5/13
 * @author Tommy Barker
 */
@Slf4j
abstract class ValidateableRowWriter extends DefaultRowWriter implements Closeable {

    List<Map<String, Object>> invalidRecords = Collections.synchronizedList([])
    AtomicInteger validRecords = new AtomicInteger(0)
    RowWriter wrappedRecordWriter

    @Override
    void write(int line, Map<String, Object> record) {
        assert wrappedRecordWriter != null: "the wrapped record cannot be null"
        if (validate(line, record)) {
            wrappedRecordWriter.write(line, record)
            validRecords.getAndIncrement()
        }
    }

    /**
     *
     * validates a record
     */
    boolean validate(int line, Map<String, Object> record) {
        try {
            doValidate(line, record)
            return true
        } catch (AssertionError assertionError) {
            log.warn "invalid record at line $line with message ${assertionError.message}"
            invalidRecords.add(record)
        }

        return false
    }

    /**
     * Use {@code assert} statements to validate and check record.  Should also supply a message to make
     * it more readable and performant
     *
     * @param line
     * @param record
     * @throws AssertionError if not valid (ie use {@code assert})
     */
    abstract void doValidate(int line, Map<String, Object> record)

    @Override
    void close() throws IOException {
        log.info("There were ${invalidRecords.size()} and ${validRecords.get()} valid records")
        if (wrappedRecordWriter instanceof Closeable) {
            wrappedRecordWriter.close()
        }
    }
}
