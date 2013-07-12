package metridoc.writers

import com.google.common.collect.Table
import metridoc.iterators.Iterators
import metridoc.iterators.Record
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 6/6/13
 * @author Tommy Barker
 */
class TableIteratorWriterSpec extends Specification {

    def "writing to a table specification"() {
        given: "a simple record iterator"
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
        def tableWriter = new TableIteratorWriter()

        when: "write is called"
        def table = tableWriter.write(rowIterator).body.table as Table

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

    def "if a record has an error, nothing is written"() {
        given: "a collection of records"
        def assertionRecords = [new Record(throwable: new AssertionError("error"))]
        def assertionRecordIterator = Iterators.toRowIterator(assertionRecords.iterator())
        def runtimeRecords = [new Record(throwable: new RuntimeException("error"))]
        def runtimeRecordIterator = Iterators.toRowIterator(runtimeRecords.iterator())

        and: "a table writer using the rowIterator"
        def tableWriter = new TableIteratorWriter()

        when: "write is called on the assertion failure records"
        def assertionResponse = tableWriter.write(assertionRecordIterator)

        then: "an appropriate response is handed back"
        assertionResponse.aggregateStats[WrittenRecordStat.Status.INVALID] == 1

        when: "write is called on runtime error records"
        tableWriter.write(runtimeRecordIterator)

        then:
        thrown RuntimeException
    }
}
