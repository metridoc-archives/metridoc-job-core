package metridoc.core.tools

import org.junit.Test

import javax.validation.constraints.NotNull

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/21/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
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
    }

    @Test
    void "test adding straight hibernate properties"() {
        def configObject = new ConfigObject()
        configObject.hibernate.hbm2ddl.auto = "create-drop"

        def properties = new HibernateTool().convertHibernateProperties(configObject)
        assert "create-drop" == properties.get("hibernate.hbm2ddl.auto")
    }

    @Test
    void "test the validator"() {
        def tool = new HibernateTool()
        assert 0 == tool.validate(new BeanForValidation(foo: "bar")).size()
        def violations = tool.validate(new BeanForValidation())
        assert 1 == violations.size()
    }
}

class BeanForValidation {
    @NotNull
    String foo
}