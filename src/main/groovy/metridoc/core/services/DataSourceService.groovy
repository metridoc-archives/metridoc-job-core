package metridoc.core.services

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
    private AtomicBoolean enableForRan = new AtomicBoolean(false)

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

    void enableFor(Class... entities) {
        if(!enableForRan.get()) {
            doEnableFor(entities)
        }
        else {
            throw IllegalStateException("Could not run [enableFor] since it has already ran")
        }
    }

    abstract protected void doEnableFor(Class... classes)
    abstract SessionFactory getSessionFactory()
    abstract void withTransaction(Closure closure)
}
