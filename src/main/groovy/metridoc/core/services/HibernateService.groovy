package metridoc.core.services

import metridoc.utils.DataSourceConfigUtil
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

/**
 * @author Tommy Barker
 */
class HibernateService extends DataSourceService {

    Properties hibernateProperties = [:]

    SessionFactory sessionFactory
    private List<Class> entityClasses = []

    @SuppressWarnings("GroovyVariableNotAssigned")
    SessionFactory createSessionFactory() {
        if (!hibernateProperties) {
            init()
        }
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

    @Override
    void init() {
        super.init()
        def hibernateProperties = convertDataSourcePropsToHibernate(config)
        this.hibernateProperties.putAll(hibernateProperties)
    }

    @Override
    void doEnableFor(Class... classes) {
        entityClasses = classes
        sessionFactory = createSessionFactory()
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
        DataSourceConfigUtil.getHibernateOnlyProperties(configObject, dataSourcePrefix)
    }
}
