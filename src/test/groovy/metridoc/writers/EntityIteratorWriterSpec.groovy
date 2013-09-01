package metridoc.writers

import metridoc.core.tools.HibernateTool
import metridoc.entities.MetridocRecordEntity
import metridoc.iterators.Iterators
import org.hibernate.Session
import spock.lang.Specification

import javax.persistence.Entity

import static metridoc.writers.WrittenRecordStat.Status.*

/**
 * Created with IntelliJ IDEA on 7/3/13
 * @author Tommy Barker
 */
class EntityIteratorWriterSpec extends Specification {

    def hTool = new HibernateTool(entityClasses: [EntityHelper], embeddedDataSource: true)


    def "test basic entity writing workflow"() {
        given: "some valid data"
        def data = [
                [foo: "asd"],
                [foo: "sdf"],
                [foo: "fgd"],
                [foo: "dfgh"]
        ]
        def rowIterator = Iterators.toRowIterator(data)

        when: "the data is written"
        def writer = new EntityIteratorWriter(sessionFactory: hTool.sessionFactory, recordEntityClass: EntityHelper)
        def response = writer.write(rowIterator)

        then: "appropriate data is returned"
        4 == response.aggregateStats[WRITTEN]
        0 == response.aggregateStats[IGNORED]
        0 == response.aggregateStats[ERROR]
        0 == response.aggregateStats[INVALID]

        hTool.withTransaction { Session session ->
            4 == session.createQuery("from EntityHelper").list().size()
        }
    }

    def "test validation errors"() {
        given: "some invalid data"
        def data = [
                [foo: "asd"],
                [foo: "sdf"],
                [foo: "invalid"],
                [foo: "dfgh"]
        ]
        def rowIterator = Iterators.toRowIterator(data)

        when: "data is written"
        def writer = new EntityIteratorWriter(sessionFactory: hTool.sessionFactory, recordEntityClass: EntityHelper)
        def response = writer.write(rowIterator)

        then: "three records are written and one is invalid"
        1 == response.invalidTotal
        3 == response.writtenTotal
        4 == response.getTotal()
    }

    def "test errors"() {
        given: "bad data"
        def data = [
                [foo: "asd"],
                [foo: "sdf"],
                [foo: "error"],
                [foo: "dfgh"]
        ]
        def rowIterator = Iterators.toRowIterator(data)

        when: "data is written"
        def writer = new EntityIteratorWriter(sessionFactory: hTool.sessionFactory, recordEntityClass: EntityHelper)
        def response = writer.write(rowIterator)
        def throwables = response.fatalErrors

        then: "one error is recorded into the response"
        1 == response.errorTotal
        1 == response.getTotal()
        1 == throwables.size()
        throwables[0] instanceof RuntimeException
    }
}


@Entity
class EntityHelper extends MetridocRecordEntity {

    String foo

    @Override
    void validate() {
        assert foo != "invalid"
        if (foo == "error") {
            throw new RuntimeException("error")
        }
    }
}
