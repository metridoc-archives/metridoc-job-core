package metridoc.core.tools

import groovy.sql.Sql
import metridoc.core.Assert

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/21/13
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
class SqlTool {
    String dataSourceKey = "dataSource"

    void setBinding(Binding binding) {
        def config = binding.config
        if (config && config instanceof ConfigObject) {
            config.putAll(binding.variables)
        }

        def dataSourceConfig = config."$dataSourceKey"
        Assert.notNull(dataSourceConfig, "dataSource config does not exist")
        def userName = dataSourceConfig.username
        def password = dataSourceConfig.password
        def url = dataSourceConfig.url
        def driver = dataSourceConfig.driverClassName

        Assert.notNull(userName, "user name cannot be null")
        Assert.notNull(password, "password cannot be null")
        Assert.notNull(url, "url cannot be null")
        Assert.notNull(driver, "driver cannot be null")
        binding.sql = Sql.newInstance(url, userName, password, driver)
    }
}
