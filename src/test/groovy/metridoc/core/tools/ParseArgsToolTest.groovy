package metridoc.core.tools

import metridoc.core.MetridocScript
import spock.lang.Specification

/**
 * @author Tommy Barker
 */
class ParseArgsToolTest extends Specification {

    Binding binding = new Binding()
    ParseArgsTool tool

    void "test parsing basic arguments"() {
        when:
        def argsMap = primeTool(["-foo=bar", "--bar=foo"])

        then:
        "bar" == argsMap.foo
        "foo" == argsMap.bar
    }

    void "test args with no equals"() {
        when:
        def argsMap = primeTool(["-foo", "--bar", "-foobar=bar"])

        then:
        testArgsWithNoEquals(argsMap)
    }

    void "test parameters"() {
        when:
        def argsMap = primeTool(["-foo", "--bar", "blah", "-foobar=bar", "bammo"])

        then:
        //do previous tests since this is an expansion
        testArgsWithNoEquals(argsMap)
        def params = argsMap.params
        2 == params.size()
        "blah" == params[0]
        "bammo" == params[1]
    }

    void testArgsWithNoEquals(Map argsMap) {
        assert argsMap.foo
        assert argsMap.bar
        assert "bar" == argsMap.foobar
    }

    void "test just the environment parameter"() {
        when:
        def argsMap = primeTool(["-env=dev"])

        then:
        "dev" == argsMap.env
    }

    void "ParseArgs should be able to accept a Map"() {
        given:
        Binding binding = new Binding()
        binding.args = ["foo", "bar"]

        when:
        use(MetridocScript) {
            binding.includeTool(ParseArgsTool)
        }

        then:
        def params = binding.argsMap.params
        params.contains("foo")
        params.contains("bar")
    }

    Map primeTool(List args) {
        binding.args = args as String[]
        tool = MetridocScript.includeTool(binding, ParseArgsTool)
        return binding.argsMap
    }
}
