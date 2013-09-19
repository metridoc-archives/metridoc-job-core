package metridoc.core.services

import metridoc.core.MetridocScript
import spock.lang.Specification

/**
 * @author Tommy Barker
 */
class ParseArgsServiceTest extends Specification {

    Binding binding = new Binding()
    ParseArgsService service

    void "test parsing basic arguments"() {
        when:
        def argsMap = primeService(["-foo=bar", "--bar=foo"])

        then:
        "bar" == argsMap.foo
        "foo" == argsMap.bar
    }

    void "test args with no equals"() {
        when:
        def argsMap = primeService(["-foo", "--bar", "-foobar=bar"])

        then:
        testArgsWithNoEquals(argsMap)
    }

    void "test parameters"() {
        when:
        def argsMap = primeService(["-foo", "--bar", "blah", "-foobar=bar", "bammo"])

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
        def argsMap = primeService(["-env=dev"])

        then:
        "dev" == argsMap.env
    }

    void "ParseArgs should be able to accept a Map"() {
        given:
        Binding binding = new Binding()
        binding.args = ["foo", "bar"]

        when:
        use(MetridocScript) {
            binding.includeService(ParseArgsService)
        }

        then:
        def params = binding.argsMap.params
        params.contains("foo")
        params.contains("bar")
    }

    Map primeService(List args) {
        binding.args = args as String[]
        service = MetridocScript.includeService(binding, ParseArgsService)
        return binding.argsMap
    }
}
