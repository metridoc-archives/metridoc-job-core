package metridoc.core.services

import metridoc.core.MetridocScript
import metridoc.utils.DataSourceConfigUtil
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 7/2/13
 * @author Tommy Barker
 */
class ConfigServiceSpec extends Specification {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    def "test in script adhoc configuration"() {
        when: "an adhoc configuration is created"
        def variable = new ConfigService().addConfig {
            foo.bar = "foobar"
        }.getVariable("foo.bar")

        then: "the variable can be extracted"
        "foobar" == variable
    }

    def "test from file adhoc configuration"() {
        given: "a configuration file"
        def file = folder.newFile("foobar")
        file.withPrintWriter {
            it.println("foo.bar = \"foobar\"")
        }

        when: "add file to configuration"
        def variable = new ConfigService().addConfig(file).getVariable("foo.bar")

        then: "the variable can be extracted"
        "foobar" == variable
    }

    def "test that the correct binding is used"() {
        given:
        def binding = new Binding()

        when:
        use(MetridocScript) {
            def configTool = binding.includeService(ConfigService)
            configTool.binding.foo = "bar"
        }

        then:
        binding.hasVariable("foo")
    }

    void "test config cli args"() {
        given:
        Binding binding = new Binding()
        binding.args = ["-config.foo.bar=5"] as String[]

        when:
        def configTool
        use(MetridocScript) {
            configTool = binding.includeService(ConfigService)
        }
        def config = binding.config

        then:
        5 == config.foo.bar
    }

    void "test instantiating dataSource"() {
        given:
        def config = new ConfigObject()
        DataSourceConfigUtil.addEmbeddedDataSource(config)
        DataSourceConfigUtil.addEmbeddedDataSource(config, "foo", "foo")
        config.dataSource_blah = "bad dataSource"

        when:
        def tool = new ConfigService()
        tool.initiateDataSources(config)

        then:
        tool.binding.dataSource
        tool.binding.sql
        tool.binding.dataSource_foo
        tool.binding.sql_foo
    }

    void "config from flag overrides home config"() {
        given:
        def metridocConfig = folder.newFile("MetridocConfig.groovy")
        def flagConfig = folder.newFile("FlagConfig.groovy")

        metridocConfig.withPrintWriter {
            it.println("foo = \"bar\"")
        }

        flagConfig.withPrintWriter {
            it.println("foo = \"foobar\"")
        }

        def configTool = new ConfigService(metridocConfigLocation: metridocConfig.path)
        def binding = new Binding()
        binding.args = ["-config=${flagConfig.path}"] as String[]

        when:
        configTool.binding = binding

        then:
        "foobar" == configTool.getVariable("foo")
    }
}
