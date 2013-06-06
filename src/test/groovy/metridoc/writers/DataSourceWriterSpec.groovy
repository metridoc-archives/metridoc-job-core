package metridoc.writers

import groovy.sql.Sql
import metridoc.iterators.Iterators
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.BatchUpdateException
import java.sql.SQLException

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
        def writer = new DataSourceWriter(dataSource: dataSource, tableName: "foo", rowIterator: iterator)
        def results = writer.write()

        then: "foo will have data from the iterator stored"
        2 == sql.firstRow("select count(*) as total from foo").total
        2 == results.size()
        1 == results[0]
        1 == results[1]
    }

    def "dataSource and tableName must be set"() {
        when: "a dataSource writer is created without a dataSource"
        new DataSourceWriter().write()

        then: "an AssertionError is thrown"
        AssertionError error = thrown()
        error.message.startsWith(DataSourceWriter.DATASOURCE_MESSAGE)

        when: "a dataSource writer is created without a tableName"
        new DataSourceWriter(dataSource: [:] as DataSource).write()

        then: "an AssertionError is thrown"
        error = thrown()
        error.message.startsWith(DataSourceWriter.TABLE_NAME_ERROR)

        when: "a dataSource writer is created without a rowIterator"
        new DataSourceWriter(dataSource: [:] as DataSource, tableName: "foo").write()

        then: "an AssertionError is thrown"
        error = thrown()
        error.message.startsWith(DataSourceWriter.ROW_ITERATOR_ERROR)
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
        new DataSourceWriter(tableName: "foo", dataSource: dataSource, rowIterator: iterator1).write()

        then: "a batch error occurs"
        thrown BatchUpdateException

        when: "a the table name does not exist"
        new DataSourceWriter(tableName: "foobar", dataSource: dataSource, rowIterator: iterator2).write()

        then: "an response exception occurs"
        thrown SQLException
    }
}
