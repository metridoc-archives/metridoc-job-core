package metridoc.sql

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.Specification

class SqlPlusSpec extends Specification {

    def dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()

    def cleanup() {
        dataSource.shutdown()
    }

    def "test corner cases"() {
        given:
        def sql = new SqlPlus(dataSource)

        when: "running a batch insert with an empty list"
        sql.runBatch("foo", [])

        then: "then there will be no error"
        notThrown Throwable

        when: "running a batch insert with an empty hash"
        sql.runBatch("foo", [:])

        then: "then there will be no error"
        notThrown Throwable
    }
}
