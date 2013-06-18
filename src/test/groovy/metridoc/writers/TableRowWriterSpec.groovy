package metridoc.writers

import com.google.common.collect.TreeBasedTable
import org.apache.commons.lang.ObjectUtils
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 6/18/13
 * @author Tommy Barker
 */
class TableRowWriterSpec extends Specification {

    def "table row writer must substitute a null representation for values to avoid error"() {
        given: "a record with a null value"
        def record = [foo: "bar", bar: null]

        and: "a row writer"
        def rowWriter = new TableRowWriter(table: TreeBasedTable.create())

        when: "a record is written"
        rowWriter.write(0, record)

        then: "the null value is replaced with a null representation and no error is thrown"
        notThrown(NullPointerException)
        ObjectUtils.NULL == rowWriter.table.row(0).bar
    }
}
