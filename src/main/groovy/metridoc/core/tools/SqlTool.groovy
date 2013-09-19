package metridoc.core.tools

import groovy.sql.Sql

/**
 * A lotof this stuff is already handled but the config tool, really don't need this anymore
 *
 * @author Tommy Barker
 * @deprecated
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
