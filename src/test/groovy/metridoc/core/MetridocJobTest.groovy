package metridoc.core

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/14/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
class MetridocJobTest {

    def helper = new MetridocJobTestHelper()

    @Test
    void "make sure that default implementation of create works"() {
        def foo = [foo: "bar", bar: "bam"]
        def job = new JobWithMapConstructor().create(foo)
        assert 'bar' == new JobWithProperties().create(foo).foo
    }

    @Test
    void "when a job is interrupted it should throw an exception"() {
        helper.interrupt()
        try {
            helper.profile("do something") {

            }
            assert false: "exception should have occurred"
        } catch (JobInterruptionException e) {
        }
    }

    @Test
    void "test general functionality of including and using targets"() {
        helper.includeTargets(MetridocJobTestTargetHelper)
        helper.defaultTarget = "bar"
        helper.execute([:])
        assert helper.binding.fooRan
        assert helper.targetsRan.contains("foo")
        assert helper.targetsRan.contains("bar")
    }

    @Test
    void "if a default target is set, and it is not there, then an illegal argument exception is thrown"() {
        helper.defaultTarget = "fooBar"
        try {
            helper.execute()
            assert false : "exception should have occurred"
        } catch (IllegalArgumentException e) {
        }

        helper.defaultTarget = "default"
        assert !helper.targetMap.containsKey("default")
        helper.execute()
    }
}

class JobWithProperties extends MetridocJob {

    def foo
    def bar

    @Override
    protected doExecute() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}

class JobWithMapConstructor extends MetridocJob {
    Map _config

    JobWithMapConstructor(Map _config) {
        this._config = _config
    }

    @Override
    protected doExecute() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}

class MetridocJobTestHelper extends MetridocJob {
    boolean doExecuteRouteRan = false


    /**
     * we were having troubles when profiling a route.  The properties of the underlying class were not propogating to the
     * route
     */
    def doExecute() {
        doExecuteRouteRan = true
    }
}

class MetridocJobTestTargetHelper extends Script {

    @Override
    Object run() {
        fooRan = false
        target(foo: "runs foo") {
            fooRan = true
            assert job instanceof MetridocJob
        }

        target(bar: "runs bar") {
            depends("foo")
        }
    }
}