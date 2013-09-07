package metridoc.utils

import spock.lang.Specification

/**
 * @author Tommy Barker
 */
class DataSourceConfigUtilSpec extends Specification {
    private ConfigObject config = new ConfigObject()

    void "hibernate properties spec"() {
        setup:
        config.hibernate.jdbc.batch_size = 5
        config.dataSource.dbCreate = "create-drop"
        config.dataSource.logSql = true
        config.dataSource.formatSql = true
        config.dataSource.dialect = "foo.bar"
        def value
        def hibernateProperties = DataSourceConfigUtil.getHibernatePoperties(config)

        when:
        value = hibernateProperties["hibernate.jdbc.batch_size"]

        then:
        noExceptionThrown()
        5 == value

        when:
        value = hibernateProperties["hibernate.hbm2ddl.auto"]

        then:
        noExceptionThrown()
        "create-drop" == value

        when:
        value = hibernateProperties["hibernate.show_sql"]

        then:
        noExceptionThrown()
        "true" == value

        when:
        value = hibernateProperties["hibernate.format_sql"]

        then:
        noExceptionThrown()
        "true" == value

        when:
        value = hibernateProperties["hibernate.dialect"]

        then:
        noExceptionThrown()
        "foo.bar" == value

        when:
        value = hibernateProperties["hibernate.current_session_context_class"]

        then:
        noExceptionThrown()
        "thread" == value

        when:
        value = hibernateProperties["hibernate.cache.provider_class"]

        then:
        noExceptionThrown()
        "org.hibernate.cache.NoCacheProvider" == value
    }

    void "hibernate only will convert the connection properties to hibernate properties"() {
        setup:
        config.dataSource.username = "foo"
        config.dataSource.password = "bar"
        config.dataSource.url = "foo://foo.com"
        def value

        when:
        value = DataSourceConfigUtil.getHibernateOnlyProperties(config)["hibernate.connection.username"]

        then:
        "foo" == value

        when:
        value = DataSourceConfigUtil.getHibernateOnlyProperties(config)["hibernate.connection.password"]

        then:
        "bar" == value

        when:
        value = DataSourceConfigUtil.getHibernateOnlyProperties(config)["hibernate.connection.url"]

        then:
        "foo://foo.com" == value
    }

    void "DataSource spec"() {
        setup:
        def dataSource
        def properties = [
                username: "sa",
                password: "",
                url: "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000",
                driverClassName: "org.hibernate.dialect.H2Dialect",
                properties: [
                        maxActive: 50
                ]
        ]
        def config = new ConfigObject()
        config.dataSource = properties

        when:
        dataSource = DataSourceConfigUtil.getDataSource(config)
        dataSource.getConnection()

        then:
        noExceptionThrown()
        50 == dataSource.maxActive

        when: "using a different dataSource name"
        config.dataSource_alt = properties
        config.dataSource_alt.properties.maxActive = 5
        dataSource = DataSourceConfigUtil.getDataSource(config, "dataSource_alt")
        dataSource.getConnection()

        then:
        noExceptionThrown()
        5 == dataSource.maxActive
    }
}
