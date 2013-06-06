package metridoc.writers

import metridoc.iterators.Iterators
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 6/6/13
 * @author Tommy Barker
 */
class TableIteratorWriterSpec extends Specification {

    def "writing to a table specification"() {
        given: "a simple row iterator"
        def rowIterator = Iterators.toRowIterator(
                [
                        [foo: "bar", bar: 5],
                        [foo: "bar", bar: 8],
                        [foo: "asdfbar", bar: 5],
                        [foo: "bafdr", bar: 5],
                        [foo: "baasdfhr", bar: 10]
                ]
        )

        and: "a table writer using the rowIterator"
        def tableWriter = new TableIteratorWriter(rowIterator: rowIterator)

        when: "write is called"
        def table = tableWriter.write()

        then: "a table with correct data is created"
        table.containsRow(0)
        table.containsRow(1)
        table.containsRow(2)
        table.containsRow(3)
        table.containsRow(4)
        def rowMap = table.rowMap()
        5 == rowMap.size()
        "bar" == rowMap[0].foo
        5 == rowMap[0].bar
        "baasdfhr" == rowMap[4].foo
        10 == rowMap[4].bar
    }
}
