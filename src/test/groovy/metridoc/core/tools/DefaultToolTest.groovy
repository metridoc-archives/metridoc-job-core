package metridoc.core.tools

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/13/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("GroovyAccessibility")
class DefaultToolTest {

    def tool = new DefaultToolHelper()

    @Test
    void "converting a map to a map just returns the original config"() {
        Map foo = [bar: "foobar"]
        //if a map then it is already done
        assert foo == DefaultTool.convertConfig(foo)
    }

    @Test
    void "converting a config object flattens it"() {
        ConfigObject foo = new ConfigObject()
        foo.bar.baz = "foobar"
        assert "foobar" == DefaultTool.convertConfig(foo)["bar.baz"]
    }

    @Test
    void "conerting a binding just returns the variable map"() {
        Binding foo = new Binding()
        foo.bar = "bam"

        assert "bam" == DefaultTool.convertConfig(foo)["bar"]
    }

    @Test
    void "get variable directly returns the value if the expected type is null"() {
        Map config = [bar: "foobar"]
        assert "foobar" == DefaultTool.getVariableHelper(config, "bar", null)
    }

    @Test
    void "if the variable does not exist, variable helper returns null"() {
        Map config = [bar: "foobar"]
        assert null == DefaultTool.getVariableHelper(config, "blam", null)
    }

    @Test
    void "if the variable exists and expected type is provided, then the converted value is provided, otherwise null is returned"() {
        Map config = [bar: "foobar"]
        assert null == DefaultTool.getVariableHelper(config, "bar", Integer)
        assert "foobar" == DefaultTool.getVariableHelper(config, "bar", String)
    }

    @Test
    void "argsMap trumps binding when getting a variable"() {
        def binding = new Binding()
        tool.setBinding(binding)
        binding.argsMap = [bar: "foo"]
        binding.bar = "foobar"

        assert "foo" == tool.getVariable("bar")
    }

    @Test
    void "binding trumps config"() {
        def config = new ConfigObject()
        config.foo = "bar"
        def binding = new Binding()
        binding.foo = "foobar"
        binding.config = config
        tool.setBinding(binding)
        assert "foobar" == tool.getVariable("foo")
    }

    @Test
    void "if a config is set and a non available variable is searched for, null is returned"() {
        def config = new ConfigObject()
        def binding = new Binding()
        binding.config = config
        tool.setBinding(binding)

        assert null == tool.getVariable("bar")
    }
}


class DefaultToolHelper extends DefaultTool {

}