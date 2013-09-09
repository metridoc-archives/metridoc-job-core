package metridoc.core.tools

import org.junit.Rule
import org.junit.contrib.java.lang.system.StandardOutputStreamLog
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class MainToolSpec extends Specification {

    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog()

    void "if the main tool is run for a none existant tool, error is thrown"() {
        given:
        def mainTool = new MainTool()
        mainTool.defaultTool = "foo"
        mainTool.runnableTools.camelTool = CamelTool

        when:
        mainTool.execute()

        then:
        thrown(AssertionError)
    }

    void "run tool spec"() {
        given: "binding containing args"
        def binding = new Binding()
        binding.args = ["foo"] as String[]

        and: "a tool using that binding"
        def tool = new MainTool(binding: binding)

        and: "the tool contains a simple runnable tool"
        tool.runnableTools = [
                foo: FooTool
        ] as Map

        when:
        tool.execute()

        then:
        binding.fooTool.fooRan
    }

    void "MainTool has to have runnableTools"() {
        when: "execute is called on the tool with no tools set"
        new MainTool().execute()

        then:
        thrown(AssertionError)
    }

    void "MainTool has to have params"() {
        when: "execute is called while having runnableTools but no params"
        new MainTool(runnableTools: [foo: RunnableTool]).execute()

        then:
        thrown(AssertionError)
    }

    void "if a param isn't available then the default tool is run"() {
        when:
        def mainTool = new MainTool(runnableTools: [foo: FooTool], defaultTool: "foo")
        mainTool.execute()

        then:
        mainTool.binding.fooTool.fooRan
        noExceptionThrown()
    }
}


class FooTool extends RunnableTool {

    boolean fooRan = false
    String usage = "foo tool"

    @Override
    def configure() {
        fooRan = true
    }
}