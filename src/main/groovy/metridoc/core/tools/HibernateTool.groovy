package metridoc.core.tools

import metridoc.core.MetridocScript
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

class HibernateTool {
    @Lazy(soft = true)
    Properties hibernateProperties = {
        def result = new Properties()
        result."hibernate.current_session_context_class" = "thread"
        result."hibernate.hbm2ddl.auto" = "update"
        result."hibernate.cache.provider_class" = "org.hibernate.cache.NoCacheProvider"

        return result
    }()
    String hibernatePrefix = "hibernate"
    String dataSourcePrefix = "dataSource"

    SessionFactory sessionFactory
    Binding binding
    List<Class> entityClasses = []

    @SuppressWarnings("GroovyVariableNotAssigned")
    SessionFactory createSessionFactory() {
        def configuration = new Configuration()
        def result
        configuration.with {
            entityClasses.each {
                addAnnotatedClass(it)
            }
            addProperties(hibernateProperties)
            result = buildSessionFactory()
        }

        return result
    }

    void init() {
        MetridocScript.includeTool(binding, ConfigTool)
        setConfig(binding.config)
    }

    void setConfig(ConfigObject config) {
        hibernateProperties.putAll(convertDataSourcePropsToHibernate(config))
        hibernateProperties.putAll(convertHibernateProperties(config))
        if (config.entityClasses) {
            entityClasses = config.entityClasses
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
        if (!sessionFactory) {
            sessionFactory = createSessionFactory()
        }
        def session = sessionFactory.currentSession
        withTransaction(session, closure)
    }

    SessionFactory getSessionFactory() {
        if (sessionFactory) {
            return sessionFactory
        }

        sessionFactory = createSessionFactory()
    }

    @SuppressWarnings("GroovyMissingReturnStatement")
    Properties convertDataSourcePropsToHibernate(ConfigObject configObject) {
        def properties = new Properties()
        def dataSourceConfig = configObject."$dataSourcePrefix"
        if (dataSourceConfig) {
            dataSourceConfig.with {
                if (dialect) {
                    if (dialect instanceof Class) {
                        properties."hibernate.dialect" = dialect.name
                    }
                    else {
                        properties."hibernate.dialect" = dialect.toString()
                    }
                }
                if (driverClassName) {
                    if (driverClassName instanceof Class) {
                        properties."hibernate.connection.driver_class" = driverClassName.name
                    }
                    else {
                        properties."hibernate.connection.driver_class" = driverClassName.toString()
                    }
                }
                if (dbCreate) {
                    properties."hibernate.hbm2ddl.auto" = dbCreate.toString()
                }
                if (url) {
                    properties."hibernate.connection.url" = url.toString()
                }
                if (username) {
                    properties."hibernate.connection.username" = username.toString()
                }
                if (password) {
                    properties."hibernate.connection.password" = password.toString()
                }
                if (password) {
                    properties."hibernate.connection.password" = password.toString()
                }
            }
        }

        return properties
    }

    Properties convertHibernateProperties(ConfigObject configObject) {
        def result = new Properties()

        configObject.flatten().findAll { it.key.startsWith("${hibernatePrefix}.") }.each {
            result.setProperty(it.key as String, it.value.toString())
        }

        return result
    }

    void configureEmbeddedDatabase() {
        hibernateProperties."hibernate.dialect" = "org.hibernate.dialect.H2Dialect"
        hibernateProperties."hibernate.connection.driver_class" = "org.h2.Driver"
        hibernateProperties."hibernate.connection.username" = "sa"
        hibernateProperties."hibernate.connection.password" = ""
        hibernateProperties."hibernate.connection.url" = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
    }
}
