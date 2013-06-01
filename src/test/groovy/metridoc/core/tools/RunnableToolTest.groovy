package metridoc.core.tools

import org.junit.Test

class RunnableToolTest {

    @Test
    void "dealing with bug where the runnable tool crashes since it is trying to deal with property injection incorrectly"() {
        def runnableTool = new RunnableTool() {
            @Override
            def configure() {
                //do nothing
            }
        }

        runnableTool.execute()
    }

    @Test
    void "test a basic job withthe runnable tool"() {
        boolean fooRan = false
        def runnableTool = new RunnableTool() {
            @Override
            def configure() {
                target(foo: "run foo") {
                    fooRan = true
                }

                setDefaultTarget("foo")
            }
        }

        runnableTool.execute()

        assert fooRan
    }

    @Test
    void "setting the default target on the commanline will override the default target"() {
        boolean barRan = false
        boolean fooRan = false
        def runnableTool = new RunnableTool() {
            @Override
            def configure() {
                target(foo: "run foo") {
                    fooRan = true
                }

                target(bar: "run bar") {
                    barRan = true
                }

                setDefaultTarget("foo")
            }
        }

        def binding = runnableTool.binding
        binding.setVariable("args", ["-target=bar"] as String[])
        runnableTool.execute()
        assert barRan
        assert !fooRan
    }
}
