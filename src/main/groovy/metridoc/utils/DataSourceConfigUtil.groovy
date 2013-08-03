package metridoc.utils

import org.apache.commons.dbcp.BasicDataSource

import javax.sql.DataSource

/**
 * @author Tommy Barker
 */
class DataSourceConfigUtil {

    static Properties getHibernatePoperties(ConfigObject config) {
        def result = [:]
        result."hibernate.current_session_context_class" = "thread"
        result."hibernate.hbm2ddl.auto" = "update"
        result."hibernate.cache.provider_class" = "org.hibernate.cache.NoCacheProvider"
        result.putAll(config.flatten().findAll { String key, value -> key.startsWith("hibernate") })
        config.dataSource.with {
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

    static Map getHibernateOnlyProperties(ConfigObject config) {
        def properties = getHibernatePoperties(config)
        config.dataSource.with {
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

    static DataSource getDataSource(ConfigObject config) {
        new BasicDataSource(getDataSourceProperties(config))
    }

    static Map getDataSourceProperties(ConfigObject config) {
        def result = [:]
        config.dataSource.with {
            result.username = username
            result.password = password
            result.url = url
            result.driverClassName = driverClassName
        }

        def dataSourceProperties = config.dataSource.properties
        if (dataSourceProperties) {
            result.putAll(dataSourceProperties)
        }
        return result
    }
}
