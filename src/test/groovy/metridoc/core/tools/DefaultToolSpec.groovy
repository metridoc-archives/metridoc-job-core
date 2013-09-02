package metridoc.core.tools

import metridoc.core.MetridocScript
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/13/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("GroovyAccessibility")
class DefaultToolSpec extends Specification {

    def tool = new DefaultToolHelper()

    void "check enabling mergeMetridocConfig"() {
        given:
        def binding = new Binding()

        when:
        use(MetridocScript) {
            binding.includeTool(DefaultToolHelper)
        }

        then:
        binding.configTool.mergeMetridocConfig

        when:
        binding = new Binding()
        binding.args = ["-mergeMetridocConfig=false"] as String[]
        use(MetridocScript) {
            binding.includeTool(DefaultToolHelper)
        }

        then:
        !binding.configTool.mergeMetridocConfig
    }

    void "converting a map to a map just returns the original config"() {
        given:
        Map expected = [bar: "foobar"]

        when:
        def foo = DefaultTool.convertConfig(expected)

        then:
        foo == DefaultTool.convertConfig(expected)
    }

    void "converting a config object flattens it"() {
        given:
        ConfigObject foo = new ConfigObject()
        foo.bar.baz = "foobar"

        when:
        def foobar = DefaultTool.convertConfig(foo)["bar.baz"]

        then:
        "foobar" == foobar
    }

    void "converting a binding just returns the variable map"() {
        given:
        Binding foo = new Binding()
        foo.bar = "bam"

        when:
        def bam = DefaultTool.convertConfig(foo)["bar"]

        then:
        "bam" == bam
    }

    void "get variable directly returns the value if the expected type is null"() {
        given:
        Map config = [bar: "foobar"]

        when:
        def foobar = DefaultTool.getVariableHelper(config, "bar", null)

        then:
        "foobar" == foobar
    }

    void "if the variable does not exist, variable helper returns null"() {
        given:
        Map config = [bar: "foobar"]

        when:
        def blam = DefaultTool.getVariableHelper(config, "blam", null)

        then:
        blam == null
    }

    void "if the variable exists and expected type is provided, then the converted value is provided, otherwise null is returned"() {
        given:
        Map config = [bar: "foobar"]

        when:
        def bar = DefaultTool.getVariableHelper(config, "bar", Integer)

        then:
        null == bar
        "foobar" == DefaultTool.getVariableHelper(config, "bar", String)
    }

    void "argsMap trumps binding when getting a variable"() {
        given:
        def binding = new Binding()
        tool.setBinding(binding)
        binding.argsMap = [bar: "foo"]
        binding.bar = "foobar"

        when:
        def bar = tool.getVariable("bar")

        then:
        "foo" == bar
    }

    void "binding trumps config"() {
        given:
        def config = new ConfigObject()
        config.foo = "bar"
        def binding = new Binding()
        binding.foo = "foobar"
        binding.config = config
        tool.setBinding(binding)

        when:
        def foo = tool.getVariable("foo")

        then:
        "foobar" == foo
    }

    void "if a config is set and a non available variable is searched for, null is returned"() {
        given:
        def config = new ConfigObject()
        def binding = new Binding()
        binding.config = config
        tool.setBinding(binding)

        when:
        def bar = tool.getVariable("bar")

        then:
        null == bar
    }

    void "test including tool with args"() {
        when:
        tool.includeTool(HibernateTool, entityClasses: [this.class])

        then:
        noExceptionThrown()
    }
}


class DefaultToolHelper extends DefaultTool {

}