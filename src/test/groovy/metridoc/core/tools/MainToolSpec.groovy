package metridoc.core.tools

import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.StandardOutputStreamLog
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class MainToolSpec extends Specification {

    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog()
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

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
        new MainTool(exitOnError: false).execute()

        then:
        thrown(AssertionError)
    }

    void "MainTool has to have params"() {
        when: "execute is called while having runnableTools but no params"
        new MainTool(runnableTools: [foo: RunnableTool], exitOnError: false).execute()

        then:
        thrown(AssertionError)
    }

    void "usage spec"() {
        when: "there are no runnableTools"
        def usage = new MainTool().getUsage()

        then:
        !usage.contains("Possible Jobs")

        when: "there are runnableTools"
        usage = new MainTool(runnableTools: [foo: RunnableTool]).getUsage()

        then:
        usage.contains("Possible Jobs")


    }

    void "-h cli usage spec"() {
        given:
        def binding = new Binding()
        binding.args = ["-h"] as String[]

        when:
        new MainTool(binding: binding, exitOnHelp: false).execute()

        then:
        log.log.contains("<job>")
        noExceptionThrown()
    }

    void "-h foo cli usage spec"() {
        given:
        def binding = new Binding()
        binding.args = ["-h", "foo"] as String[]

        when:
        new MainTool(runnableTools: [foo: FooTool], binding: binding).execute()

        then:
        log.log.contains("foo tool")
        noExceptionThrown()
    }

    void "-h bar cli usage, where bar does not exist spec"() {
        given:
        def binding = new Binding()
        binding.args = ["-h", "bar"] as String[]

        when:
        new MainTool(runnableTools: [foo: FooTool], binding: binding, exitOnHelp: false).execute()

        then:
        log.log.contains("<job>")
        noExceptionThrown()
    }

    void "exits on help spec"() {
        given:
        def binding = new Binding()
        binding.args = ["-h"] as String[]

        when:
        exit.expectSystemExitWithStatus(0)
        new MainTool(binding: binding).execute()

        then:
        noExceptionThrown()
    }

    void "if a param isn't available then wthe default tool is run"() {
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