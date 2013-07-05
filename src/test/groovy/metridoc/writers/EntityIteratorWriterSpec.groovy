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

    def "test basic entity writing workflow"() {
        given: "an embedded database"
        def hTool = new HibernateTool(entityClasses: [EntityHelper])
        hTool.configureEmbeddedDatabase()

        and: "some data"
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
}


@Entity
class EntityHelper extends MetridocRecordEntity {

    String foo

    @Override
    void validate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
