package metridoc.core

import metridoc.core.tools.DefaultTool
import metridoc.core.tools.HibernateTool
import metridoc.core.tools.Tool
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/16/13
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
class TargetManagerTest {

    def targetManager = new TargetManager()

    @Test
    void "when a job is interrupted it should throw an exception"() {
        targetManager.interrupt()

        try {
            targetManager.profile("do something") {

            }
            assert false: "exception should have occurred"
        } catch (JobInterruptionException e) {
        }
    }

    @Test
    void "test general functionality of including and using targets"() {
        targetManager.includeTargets(MetridocJobTestTargetHelper)
        targetManager.defaultTarget = "bar"
        targetManager.runDefaultTarget()
        assert targetManager.binding.fooRan
        assert targetManager.targetsRan.contains("foo")
        assert targetManager.targetsRan.contains("bar")
    }

    @Test
    void "include tool returns the tool it instantiates or has already instantiated"() {
        def tool = targetManager.includeTool(HibernateTool)
        assert tool
        assert tool instanceof HibernateTool
        assert tool == targetManager.includeTool(HibernateTool)
    }

    @Test
    void "test target manager interruption"() {
        assert !targetManager.interrupted
        targetManager.interrupt()
        assert targetManager.interrupted
        assert targetManager.binding.interrupted
    }

    @Test
    void "if the binding has an interrupted value set to true, then it is interrupted"() {
        targetManager.binding.interrupted = true
        assert targetManager.interrupted
    }

    @Test
    void "test property injection"() {
        def binding = targetManager.binding
        binding.bar = "foo"
        binding.bam = "foo"
        binding.foobar = "55" //requires conversion
        binding.blammo = "55" //does not exist in tool
        binding.something = "foobar" //wrong type

        targetManager.includeTool(FooToolHelper)
        FooToolHelper helper = binding.fooToolHelper
        assert "foo" == helper.bar
        assert "foo" == helper.bam
        assert 55 == helper.foobar
        assert null == helper.something
    }

    @Test
    void "injection uses getVariable if the class extends DefaultTool"() {
        def binding = targetManager.binding
        binding.config = new ConfigObject()
        binding.config.foo = "bar"
        def helper = targetManager.includeTool(FooBarToolHelper)
        assert helper.foo == "bar"
    }

    class FooToolHelper implements Tool{
        def bar
        String bam
        Integer foobar
        Integer something

        @Override
        void setBinding(Binding binding) {

        }
    }

    class FooBarToolHelper extends DefaultTool{
        String foo
    }

    class MetridocJobTestTargetHelper extends Script {

        @Override
        Object run() {
            fooRan = false
            target(foo: "runs foo") {
                fooRan = true
                assert targetManager instanceof TargetManager
            }

            target(bar: "runs bar") {
                depends("foo")
            }
        }
    }
}
