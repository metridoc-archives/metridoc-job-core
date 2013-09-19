package metridoc.core

import metridoc.core.services.HibernateService
import spock.lang.Specification

class MetridocScriptTest extends Specification {

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
}

class MetridocScriptHelper extends Script {

    @Override
    Object run() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}

abstract class Foo {
    def foo
}

class Bar extends Foo {

}