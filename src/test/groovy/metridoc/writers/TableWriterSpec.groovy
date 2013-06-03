package metridoc.writers

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
        def table = new TableWriter().write(Iterators.toRowIterator(iterator))

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
        new TableWriter().write(null)

        then:
        thrown AssertionError
    }
}
