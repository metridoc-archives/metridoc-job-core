package metridoc.core.tools

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/22/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
class ParseArgsToolTest {

    Binding binding = new Binding()
    ParseArgsTool tool = new ParseArgsTool()

    @Test
    void "test parsing basic arguments"() {
        def argsMap = primeTool(["-foo=bar", "--bar=foo"])
        assert "bar" == argsMap.foo
        assert "foo" == argsMap.bar
    }

    @Test
    void "test args with no equals"() {
        def argsMap = primeTool(["-foo", "--bar", "-foobar=bar"])
        assert argsMap.foo
        assert argsMap.bar
        assert "bar" == argsMap.foobar
    }

    @Test
    void "test parameters"() {
        def argsMap = primeTool(["-foo", "--bar", "blah", "-foobar=bar", "bammo"])
        //do previous tests since this is an expansion
        "test args with no equals"()
        def params = argsMap.params
        assert 2 == params.size()
        assert "blah" == params[0]
        assert "bammo" == params[1]
    }

    @Test
    void "test just the environment parameter"() {
        def argsMap = primeTool(["-env=dev"])
        assert "dev" == argsMap.env
    }

    Map primeTool(List args) {
        binding.args = args as String[]
        tool.setBinding(binding)
        return binding.argsMap
    }
}
