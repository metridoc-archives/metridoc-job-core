package metridoc.core

import metridoc.core.services.HibernateService
import spock.lang.Specification

class MetridocScriptSpec extends Specification {

    Script script = new MetridocScriptHelper()

    void "test initializing the targetManager"() {
        when:
        MetridocScript.initializeTargetManagerIfNotThere(script)

        then:
        script.stepManager
    }

    void "target manager can only be initialize once"() {
        when:
        MetridocScript.initializeTargetManagerIfNotThere(script)
        def targetManager = script.stepManager
        MetridocScript.initializeTargetManagerIfNotThere(script)

        then:
        targetManager == script.stepManager
    }

    void "include tool returns the tool that has been instantiated or instantiated in the past"() {
        when:
        def tool = MetridocScript.includeService(script, HibernateService)

        then:
        tool
        tool == MetridocScript.includeService(script, HibernateService)
    }

    void "test adding tools with arguments"() {
        when:
        def service
        use(MetridocScript) {
            service = script.includeService(HibernateService, entityClasses: [this.class])
            service = script.binding.includeService(HibernateService, entityClasses: [this.class])
        }

        then:
        noExceptionThrown()
        service.entityClasses
    }

    void "test injection with abstract classes as parents"() {
        given:
        def binding = new Binding()
        def targetManager = new StepManager()
        binding.foo = "bar"
        def bar = new Bar()
        targetManager.binding = binding

        when:
        targetManager.handlePropertyInjection(bar)

        then:
        noExceptionThrown()
        "bar" == bar.foo
    }

    void "test running a step based on method name"() {
        when:
        def helper = new MetridocScriptHelper()

        use(MetridocScript) {
            helper.step(runFoo: "runs foo")
            helper.runSteps("runFoo")
        }

        then:
        noExceptionThrown()
        helper.fooRan

        when: "running via a closure"
        def barRan = false
        helper.bar = {
            barRan = true
        }
        use(MetridocScript) {
            helper.step(bar: "runs bar")
            helper.runSteps("bar")
        }

        then:
        noExceptionThrown()
        barRan

        when: "adding a step that does not have a corresponding method or closure"
        use(MetridocScript) {
            helper.step(noMethod: "adding a bad step")
        }

        then:
        def error = thrown(AssertionError)
        error.message.contains("Could not find a corresponding method or closure for step")
    }
}

class MetridocScriptHelper extends Script {

    boolean fooRan = false

    @Override
    Object run() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    void runFoo() {
        fooRan = true
    }
}

abstract class Foo {
    def foo
}

class Bar extends Foo {

}