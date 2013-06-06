package metridoc.writers

/**
 * Created with IntelliJ IDEA on 6/5/13
 * @author Tommy Barker
 */
abstract class DefaultRowWriter implements RowWriter {

    @SuppressWarnings("GroovyVariableNotAssigned")
    void write(Map<String, Object> record) {
        assert record.containsKey("lineNumber") || record.containsKey("line"): "record must contain integer value with name [line] or [lineNumber]"
        int lineNumber
        try {
            lineNumber = (record.lineNumber ?: record.line) as int
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentException("line number [$lineNumber] is not an integer")
        }

        write(lineNumber, record)
    }
}
