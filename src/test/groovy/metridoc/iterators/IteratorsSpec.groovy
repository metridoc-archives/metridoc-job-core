package metridoc.iterators

import com.google.common.collect.Table
import groovy.sql.Sql
import metridoc.utils.DataSourceConfigUtil
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.sql.ResultSet

/**
 * Created with IntelliJ IDEA on 7/5/13
 * @author Tommy Barker
 */
class IteratorsSpec extends Specification {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()
    File foo

    def setup() {
        foo = folder.newFile("foo")
        foo.withPrintWriter {
            it.println("a|b")
            it.println("b|c")
            return it
        }
    }

    void "iterators are created via lookup"() {
        when: "a delimited iterator is built"
        def iterator = Iterators.createIterator("delimited", delimiter: /\|/, file: foo)
        def next1 = iterator.next()
        def next2 = iterator.next()

        then: "we can iterate over the file contents"

        "a" == next1.body[0]
        "b" == next1.body[1]

        "b" == next2.body[0]
        "c" == next2.body[1]

        try {
            iterator.next()
            assert false: "exception should have occurred"
        }
        catch (NoSuchElementException ignored) {
            //do nothing
        }
    }

    void "iterators should also be able to write"() {
        when: "a delimited iterator is built"
        def iterator = Iterators.createIterator("delimited", delimiter: /\|/, file: foo)

        and: "and is written to a guave table"
        def table = iterator.writeTo("table") as Table

        def set = table.columnKeySet()
        then:
        set.contains(0)
        set.contains(1)
    }

    void "test sql iterator extension"() {
        given:
        def dataSource = DataSourceConfigUtil.embeddedDataSource
        def sql = new Sql(dataSource)
        try {
            //just in case dataSource was not closed from another test
            sql.execute("drop table foo")
        }
        catch (Throwable ignore) {

        }
        sql.execute("create table foo (bar varchar(5))")

        when: "fromSql is called with no arguments"
        Iterators.fromSql(null)

        then:
        thrown(AssertionError)

        when: "everything is fine when there is a dataSource"
        sql.query("select * from foo") { ResultSet resultSet ->
            Iterators.fromSql(resultSet)
        }

        then:
        noExceptionThrown()

        cleanup:
        try {
            dataSource.close()
        }
        catch (Throwable ignore) {

        }
    }
}
