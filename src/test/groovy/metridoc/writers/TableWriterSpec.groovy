package metridoc.writers

import com.google.common.collect.Table
import metridoc.iterators.Iterators
import spock.lang.Specification

class TableWriterSpec extends Specification {

    void "test basic table writing"() {
        given:
        def iterator = [
                [foo: "bar", bar: 5],
                [foo: "baz", bar: 7]
        ]

        when:
        def table = new TableIteratorWriter().write(Iterators.toRowIterator(iterator)).response.table as Table

        then:
        4 == table.size()
        2 == table.rowKeySet().size()
        "bar" == table.row(0).foo
        "baz" == table.row(1).foo
        5 == table.row(0).bar
        7 == table.row(1).bar
    }

    void "error thrown if iterator is null"() {
        when:
        new TableIteratorWriter().write(null)

        then:
        thrown AssertionError
    }
}
