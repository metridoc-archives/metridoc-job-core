package metridoc.core.tools

import groovy.sql.Sql

/**
 * @author Tommy Barker
 */
class SqlTool {
    String dataSourceKey = "dataSource"
    Binding binding

    void init() {
        def config = binding.config
        if (config && config instanceof ConfigObject) {
            config.putAll(binding.variables)
        }

        def dataSourceConfig = config."$dataSourceKey"
        assert dataSourceConfig != null: "dataSource config does not exist"
        def userName = dataSourceConfig.username
        def password = dataSourceConfig.password
        def url = dataSourceConfig.url
        def driver = dataSourceConfig.driverClassName

        assert userName: "user name cannot be null"
        assert password: "password cannot be null"
        assert url: "url cannot be null"
        assert driver: "driver cannot be null"
        binding.sql = Sql.newInstance(url, userName, password, driver)
    }
}
