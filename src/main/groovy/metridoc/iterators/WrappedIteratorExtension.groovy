package metridoc.iterators

import metridoc.core.tools.HibernateTool
import metridoc.writers.EntityIteratorWriter
import metridoc.writers.WriteResponse
import org.hibernate.SessionFactory

import javax.sql.DataSource

/**
 * Created with IntelliJ IDEA on 9/11/13
 * @author Tommy Barker
 */
class WrappedIteratorExtension {

    public static WriteResponse toDataSource(WrappedIterator iterator, DataSource dataSource, String tableName) {
        def writer = Iterators.createWriter([dataSource: dataSource, tableName: tableName], "sql")
        writer.write(iterator.wrappedIterator)
    }

    public static WriteResponse toGuavaTable(WrappedIterator iterator) {
        def writer = Iterators.createWriter("guavaTable")
        writer.write(iterator.wrappedIterator)
    }

    public static WriteResponse toEntity(WrappedIterator iterator, Class entity, SessionFactory sessionFactory) {
        Iterators.createWriter(sessionFactory: sessionFactory, recordEntityClass: entity, "entity").write(iterator.wrappedIterator)
    }

    public static WriteResponse toEntity(WrappedIterator iterator, Class entity, HibernateTool hibernateTool) {
        Iterators.createWriter(sessionFactory: hibernateTool.sessionFactory, recordEntityClass: entity, "entity").write(iterator.wrappedIterator)
    }
}
