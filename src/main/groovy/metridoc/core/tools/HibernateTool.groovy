package metridoc.core.tools

import metridoc.core.MetridocScript
import metridoc.utils.DataSourceConfigUtil
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

class HibernateTool {
    Properties hibernateProperties = [:]

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
        DataSourceConfigUtil.getHibernateOnlyProperties(configObject)
    }

    void configureEmbeddedDatabase() {
        hibernateProperties."hibernate.current_session_context_class" = "thread"
        hibernateProperties."hibernate.hbm2ddl.auto" = "update"
        hibernateProperties."hibernate.cache.provider_class" = "org.hibernate.cache.NoCacheProvider"
        hibernateProperties."hibernate.dialect" = "org.hibernate.dialect.H2Dialect"
        hibernateProperties."hibernate.connection.driver_class" = "org.h2.Driver"
        hibernateProperties."hibernate.connection.username" = "sa"
        hibernateProperties."hibernate.connection.password" = ""
        hibernateProperties."hibernate.connection.url" = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
    }
}
