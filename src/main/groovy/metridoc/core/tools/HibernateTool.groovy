package metridoc.core.tools

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.ValidatorFactory

class HibernateTool {
    Properties hibernateProperties = new Properties()
    String hibernatePrefix = "hibernate"
    String dataSourcePrefix = "dataSource"
    SessionFactory sessionFactory
    List<Class> entityClasses = []
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()

    List<ConstraintViolation> validate(value) {
        def validator = validatorFactory.validator
        validator.validate(value) as List
    }

    void setBinding(Binding binding) {
        ConfigObject config
        if (binding.hasVariable("config") && binding.config instanceof ConfigObject) {
            config = binding.config
        } else {
            config = new ConfigObject()
        }
        config.putAll(binding.variables)
        setConfig(config)
    }

    void setConfig(ConfigObject config) {
        //defaults first
        hibernateProperties."hibernate.current_session_context_class" = "thread"
        hibernateProperties."hibernate.hbm2ddl.auto" = "update"
        hibernateProperties."hibernate.cache.provider_class" = "org.hibernate.cache.NoCacheProvider"

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
        } catch (Exception e) {
            transaction.rollback()
            throw e
        }
    }

    void withTransaction(Closure closure) {
        if (sessionFactory == null) {
            def configuration = new Configuration()
            configuration.with {
                entityClasses.each {
                    addClass(it)
                }
                addProperties(hibernateProperties)
                sessionFactory = buildSessionFactory()
            }
        }

        def session = sessionFactory.currentSession
        withTransaction(session, closure)
    }

    Properties convertDataSourcePropsToHibernate(ConfigObject configObject) {
        def properties = new Properties()
        def dataSourceConfig = configObject."$dataSourcePrefix"
        if (dataSourceConfig) {
            dataSourceConfig.with {
                if (dialect) {
                    properties."hibernate.dialect" = dialect.toString()
                }
                if (driverClassName) {
                    properties."hibernate.connection.driver_class" = driverClassName.toString()
                }
                if (dbCreate) {
                    properties."hibernate.hbm2ddl.auto" = dbCreate.toString()
                }
                if (url) {
                    properties."hibernate.connection.url" = dbCreate.toString()
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
}
