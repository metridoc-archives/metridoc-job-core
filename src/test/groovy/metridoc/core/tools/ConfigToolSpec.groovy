package metridoc.core.tools

import metridoc.core.MetridocScript
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 7/2/13
 * @author Tommy Barker
 */
class ConfigToolSpec extends Specification {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    def "test in script adhoc configuration"() {
        when: "an adhoc configuration is created"
        def variable = new ConfigTool().addConfig {
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
        def variable = new ConfigTool().addConfig(file).getVariable("foo.bar")

        then: "the variable can be extracted"
        "foobar" == variable
    }

    def "test that the correct binding is used"() {
        given:
        def binding = new Binding()

        when:
        use(MetridocScript) {
            def configTool = binding.includeTool(ConfigTool)
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
            configTool = binding.includeTool(ConfigTool)
        }
        def config = binding.config

        then:
        5 == config.foo.bar
    }
}
