package metridoc.utils

import org.apache.commons.dbcp.BasicDataSource

import javax.sql.DataSource

/**
 * @author Tommy Barker
 */
class DataSourceConfigUtil {

    public static final String DEFAULT_DATASOURCE = "dataSource"

    static Properties getHibernatePoperties(ConfigObject config, String dataSourceName) {
        String dataSourceNameUsed = dataSourceName ?: DEFAULT_DATASOURCE
        def result = [:]
        result."hibernate.current_session_context_class" = "thread"
        result."hibernate.hbm2ddl.auto" = "update"
        result."hibernate.cache.provider_class" = "org.hibernate.cache.NoCacheProvider"
        result.putAll(config.flatten().findAll { String key, value -> key.startsWith("hibernate") })
        config."${dataSourceNameUsed}".with {
            if (dbCreate) {
                result."hibernate.hbm2ddl.auto" = dbCreate.toString()
            }

            if (logSql) {
                result."hibernate.show_sql" = logSql.toString()
            }

            if (formatSql) {
                result."hibernate.format_sql" = formatSql.toString()
            }

            if (dialect) {
                if (dialect instanceof Class) {
                    result."hibernate.dialect" = dialect.name
                }
                else {
                    result."hibernate.dialect" = dialect.toString()
                }
            }
        }

        return result as Properties
    }

    static Properties getHibernatePoperties(ConfigObject config) {
        getHibernatePoperties(config, DEFAULT_DATASOURCE)
    }

    static Map getHibernateOnlyProperties(ConfigObject config, String dataSourceName) {
        def dataSourceNameUsed = dataSourceName ?: DEFAULT_DATASOURCE
        def properties = getHibernatePoperties(config)
        config."${dataSourceNameUsed}".with {
            if (url) {
                properties."hibernate.connection.url" = url.toString()
            }
            if (username) {
                properties."hibernate.connection.username" = username.toString()
            }
            if (password) {
                properties."hibernate.connection.password" = password.toString()
            }
            if (driverClassName) {
                if (driverClassName instanceof Class) {
                    properties."hibernate.connection.driver_class" = driverClassName.name
                }
                else {
                    properties."hibernate.connection.driver_class" = driverClassName.toString()
                }
            }
        }

        return properties
    }

    static Map getHibernateOnlyProperties(ConfigObject config) {
        getHibernateOnlyProperties(config, DEFAULT_DATASOURCE)
    }

    static DataSource getDataSource(ConfigObject config, String dataSourceName) {
        def dataSourceNameUsed = dataSourceName ?: DEFAULT_DATASOURCE
        def dataSourceProperties = getDataSourceProperties(config, dataSourceNameUsed)
        new BasicDataSource(dataSourceProperties)
    }

    static DataSource getDataSource(ConfigObject config) {
        getDataSource(config, DEFAULT_DATASOURCE)
    }

    static Map getDataSourceProperties(ConfigObject config, String dataSourceName) {
        def dataSourceNameUsed = dataSourceName ?: DEFAULT_DATASOURCE
        def result = [:]
        config."${dataSourceNameUsed}".with {
            result.username = username
            result.password = password
            result.url = url
            result.driverClassName = driverClassName
        }

        def dataSourceProperties = config."${dataSourceNameUsed}".properties
        if (dataSourceProperties) {
            result.putAll(dataSourceProperties)
        }
        return result
    }

    static getEmbeddedDataSource() {
        def dataSource = new BasicDataSource()
        dataSource.username = "sa"
        dataSource.password = ""
        dataSource.url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        dataSource.driverClassName= "org.h2.Driver"

        return dataSource
    }

    static Map getDataSourceProperties(ConfigObject config) {
        getDataSourceProperties(config, DEFAULT_DATASOURCE)
    }
}
