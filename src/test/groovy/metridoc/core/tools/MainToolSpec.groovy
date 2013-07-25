package metridoc.core.tools

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class MainToolSpec extends Specification {

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
}


class FooTool extends RunnableTool {

    boolean fooRan = false
    String usage = "foo tool"

    @Override
    def configure() {
        fooRan = true
    }
}