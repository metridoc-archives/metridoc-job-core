package metridoc.core.services
/**
 * @author Tommy Barker
 */
abstract class DataSourceService extends DefaultService {
    boolean embeddedDataSource
    boolean localMysql

    void init() {
        def config = getVariable("config", ConfigObject)
        if (!config) {
            includeService(ConfigService)
            config = binding.config
        }
        if (config != null) {
            def dataSource = config.dataSource
            if (embeddedDataSource) {
                dataSource.url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
                dataSource.username = "SA"
                dataSource.password = ""
                dataSource.driverClassName = "org.h2.Driver"
                dataSource.dialect = "org.hibernate.dialect.H2Dialect"
            }

            if (localMysql) {
                dataSource.url = "jdbc:mysql://localhost:3306/test"
                dataSource.username = "root"
                if (!dataSource.password) {
                    dataSource.password = ""
                }
                dataSource.driverClassName = "com.mysql.jdbc.Driver"
                dataSource.dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
            }
        }
    }
}
