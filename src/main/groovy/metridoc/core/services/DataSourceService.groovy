package metridoc.core.services

import org.hibernate.Session
import org.hibernate.SessionFactory

import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Tommy Barker
 */
abstract class DataSourceService extends DefaultService {
    boolean embeddedDataSource
    boolean localMysql
    boolean localMySql
    ConfigObject config = new ConfigObject()
    String dataSourcePrefix = "dataSource"
    private boolean enableForRan = false

    void init() {
        def dataSource = config."$dataSourcePrefix"
        if (embeddedDataSource) {
            dataSource.url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
            dataSource.username = "SA"
            dataSource.password = ""
            dataSource.driverClassName = "org.h2.Driver"
            dataSource.dialect = "org.hibernate.dialect.H2Dialect"
        }

        if (localMysql || localMySql) {
            dataSource.url = "jdbc:mysql://localhost:3306/test"
            dataSource.username = "root"
            if (!dataSource.password) {
                dataSource.password = ""
            }
            dataSource.driverClassName = "com.mysql.jdbc.Driver"
            dataSource.dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
        }
    }

    synchronized void enableFor(Class... entities) {
        if(!enableForRan) {
            doEnableFor(entities)
            enableForRan = true
        }
        else {
            throw new IllegalStateException("Could not run [enableFor] since it has already ran")
        }
    }

    static void withTransaction(Session session, Closure closure) {
        def transaction = session.beginTransaction()
        try {
            closure.call(session)
            transaction.commit()
        }
        catch (Exception e) {
            transaction.rollback()
            throw e
        }
    }

    void withTransaction(Closure closure) {
        assert sessionFactory : "session factory has not been set yet"
        def session = sessionFactory.currentSession
        withTransaction(session, closure)
    }

    abstract protected void doEnableFor(Class... classes)
    abstract SessionFactory getSessionFactory()
}
