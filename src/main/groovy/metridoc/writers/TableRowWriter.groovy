package metridoc.writers

import com.google.common.collect.Table
import org.apache.commons.lang.ObjectUtils

/**
 * Created with IntelliJ IDEA on 6/5/13
 * @author Tommy Barker
 */
class TableRowWriter extends DefaultRowWriter {
    Table table

    @Override
    void write(int line, Map<String, Object> record) {
        assert table != null: "table cannot be null"

        record.each { String columnKey, value ->
            table.put(line, columnKey, value ?: ObjectUtils.NULL)
        }
    }
}
