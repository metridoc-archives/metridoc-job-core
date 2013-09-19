package metridoc.core.services

import com.mysql.jdbc.Driver
import metridoc.core.MetridocScript
import org.hibernate.dialect.MySQL5InnoDBDialect
import org.junit.Test

/**
 * @author Tommy Barker
 *
 */
class HibernateServiceTest {

    @Test
    void "set Binding should not fail if config property does not exist"() {
        def tool = new HibernateService()
        tool.binding = new Binding()
    }

    @Test
    void "test converting to hibernate properties from dataSource properties"() {
        def configObject = new ConfigObject()
        configObject.dataSource.dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
        configObject.dataSource.driverClassName = "com.mysql.jdbc.Driver"
        configObject.dataSource.dbCreate = "create-drop"

        def properties = new HibernateService().convertDataSourcePropsToHibernate(configObject)
        assert "org.hibernate.dialect.MySQL5InnoDBDialect" == properties.get("hibernate.dialect")
        assert "com.mysql.jdbc.Driver" == properties.get("hibernate.connection.driver_class")
        assert "create-drop" == properties.get("hibernate.hbm2ddl.auto")

        //what if we use classes instead of strings?
        configObject.dataSource.dialect = MySQL5InnoDBDialect
        configObject.dataSource.driverClassName = Driver
        configObject.dataSource.dbCreate = "create-drop"

        properties = new HibernateService().convertDataSourcePropsToHibernate(configObject)
        assert "org.hibernate.dialect.MySQL5InnoDBDialect" == properties.get("hibernate.dialect")
        assert "com.mysql.jdbc.Driver" == properties.get("hibernate.connection.driver_class")
        assert "create-drop" == properties.get("hibernate.hbm2ddl.auto")
    }

    @Test
    void "test adding straight hibernate properties"() {
        def configObject = new ConfigObject()
        configObject.hibernate.hbm2ddl.auto = "create-drop"

        def properties = new HibernateService().convertDataSourcePropsToHibernate(configObject)
        assert "create-drop" == properties.get("hibernate.hbm2ddl.auto")
    }

    @Test
    void "test configuring a basic embedded database"() {
        def tool = new HibernateService(embeddedDataSource: true)
        tool.init()
        assert "org.hibernate.dialect.H2Dialect" == tool.hibernateProperties."hibernate.dialect"
        assert "org.h2.Driver" == tool.hibernateProperties."hibernate.connection.driver_class"
    }

    @Test
    void "test injection params"() {
        def binding = new Binding()
        binding.args = [
                "--mergeMetridocConfig=false",
                "--localMysql"
        ] as String[]

        use(MetridocScript) {
            def tool = binding.includeService(HibernateService)
            assert !tool.mergeMetridocConfig
            assert tool.localMysql
            def config = binding.config
            assert "jdbc:mysql://localhost:3306/test" == config.dataSource.url
        }
    }
}

