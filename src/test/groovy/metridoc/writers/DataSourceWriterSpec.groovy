package metridoc.writers

import groovy.sql.Sql
import metridoc.iterators.Iterators
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.BatchUpdateException
import java.sql.SQLException

import static metridoc.writers.WrittenRecordStat.Status.WRITTEN

class DataSourceWriterSpec extends Specification {

    def dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
    def sql = new Sql(dataSource)
    def iterator = Iterators.toRowIterator([
            [foo: "bar", bar: 5],
            [foo: "baz", bar: 7]
    ])

    def setup() {
        sql.execute("create table foo(foo varchar(3), bar int)")
    }

    def cleanup() {
        dataSource.shutdown()
    }

    def "test basic dataSource writing"() {
        when: "data is written to the foo table"
        def writer = new DataSourceIteratorWriter(dataSource: dataSource, tableName: "foo")
        def results = writer.write(iterator)

        then: "foo will have data from the iterator stored"
        2 == sql.firstRow("select count(*) as total from foo").total
        2 == results.aggregateStats[WRITTEN]
        int[] batchResults = results as int[]
        2 == batchResults.size()
        1 == batchResults[0]
        1 == batchResults[1]
    }

    def "dataSource and tableName must be set"() {
        when: "a dataSource writer is created without a dataSource"
        new DataSourceIteratorWriter().write(iterator)

        then: "an AssertionError is thrown"
        AssertionError error = thrown()
        error.message.startsWith(DataSourceIteratorWriter.DATASOURCE_MESSAGE)

        when: "a dataSource writer writes against null"
        new DataSourceIteratorWriter(dataSource: [:] as DataSource).writeRecord(null)

        then: "an AssertionError is thrown"
        error = thrown()
        error.message.startsWith(DataSourceIteratorWriter.TABLE_NAME_ERROR)


    }

    def "test errors when insert fails"() {
        given:
        def data = [
                [foo: "bar", bar: 5],
                [foo: "baz", bar: 7],
                [foo: "foobar", bar: 7]
        ]
        def iterator1 = Iterators.toRowIterator(data)
        def iterator2 = Iterators.toRowIterator(data)

        when: "a record in a batch is too long"
        new DataSourceIteratorWriter(tableName: "foo", dataSource: dataSource).write(iterator1)

        then: "a batch error occurs"
        thrown BatchUpdateException

        when: "a the table name does not exist"
        new DataSourceIteratorWriter(tableName: "foobar", dataSource: dataSource).write(iterator2)

        then: "an response exception occurs"
        thrown SQLException
    }
}
