package metridoc.core.tools

import org.junit.Test


/**
 * @author Tommy Barker
 *
 */
class HibernateToolTest {

    @Test
    void "set Binding should not fail if config property does not exist"() {
        def tool = new HibernateTool()
        tool.binding = new Binding()
    }

    @Test
    void "test converting to hibernate properties from dataSource properties"() {
        def configObject = new ConfigObject()
        configObject.dataSource.dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
        configObject.dataSource.driverClassName = "com.mysql.jdbc.Driver"
        configObject.dataSource.dbCreate = "create-drop"

        def properties = new HibernateTool().convertDataSourcePropsToHibernate(configObject)
        assert "org.hibernate.dialect.MySQL5InnoDBDialect" == properties.get("hibernate.dialect")
        assert "com.mysql.jdbc.Driver" == properties.get("hibernate.connection.driver_class")
        assert "create-drop" == properties.get("hibernate.hbm2ddl.auto")

        //what if we use classes instead of strings?
        configObject.dataSource.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
        configObject.dataSource.driverClassName = com.mysql.jdbc.Driver
        configObject.dataSource.dbCreate = "create-drop"

        properties = new HibernateTool().convertDataSourcePropsToHibernate(configObject)
        assert "org.hibernate.dialect.MySQL5InnoDBDialect" == properties.get("hibernate.dialect")
        assert "com.mysql.jdbc.Driver" == properties.get("hibernate.connection.driver_class")
        assert "create-drop" == properties.get("hibernate.hbm2ddl.auto")
    }

    @Test
    void "test adding straight hibernate properties"() {
        def configObject = new ConfigObject()
        configObject.hibernate.hbm2ddl.auto = "create-drop"

        def properties = new HibernateTool().convertDataSourcePropsToHibernate(configObject)
        assert "create-drop" == properties.get("hibernate.hbm2ddl.auto")
    }

    @Test
    void "test configuring a basic embedded database"() {
        def tool = new HibernateTool(embeddedDataSource: true)
        tool.init()
        assert "org.hibernate.dialect.H2Dialect" == tool.hibernateProperties."hibernate.dialect"
        assert "org.h2.Driver" == tool.hibernateProperties."hibernate.connection.driver_class"
    }
}

