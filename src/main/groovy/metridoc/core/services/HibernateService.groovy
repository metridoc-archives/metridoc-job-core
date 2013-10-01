package metridoc.core.services

import groovy.util.logging.Slf4j
import metridoc.utils.DataSourceConfigUtil
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

/**
 * @author Tommy Barker
 */
@Slf4j
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
        this.entityClasses.each {
            configuration.addAnnotatedClass(it)
        }
        configuration.addProperties(hibernateProperties)
        log.info "hibernate service is connecting to ${hibernateProperties.'hibernate.connection.url'}"
        result = configuration.buildSessionFactory()

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
