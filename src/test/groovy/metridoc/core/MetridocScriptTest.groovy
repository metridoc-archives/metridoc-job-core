package metridoc.core

import metridoc.core.tools.HibernateTool
import spock.lang.Specification

class MetridocScriptTest extends Specification {

    Script script = new MetridocScriptHelper()

    void "test initializing the targetManager"() {
        when:
        MetridocScript.initializeTargetManagerIfNotThere(script)

        then:
        script.targetManager
    }

    void "target manager can only be initialize once"() {
        when:
        MetridocScript.initializeTargetManagerIfNotThere(script)
        def targetManager = script.targetManager
        MetridocScript.initializeTargetManagerIfNotThere(script)

        then:
        targetManager == script.targetManager
    }

    void "include tool returns the tool that has been instantiated or instantiated in the past"() {
        when:
        def tool = MetridocScript.includeTool(script, HibernateTool)

        then:
        tool
        tool == MetridocScript.includeTool(script, HibernateTool)
    }

    void "test adding tools with arguments"() {
        when:
        def tool
        use(MetridocScript) {
            tool = script.includeTool(HibernateTool, entityClasses: [this.class])
            tool = script.binding.includeTool(HibernateTool, entityClasses: [this.class])
        }

        then:
        noExceptionThrown()
        tool.entityClasses
    }
}

class MetridocScriptHelper extends Script {

    @Override
    Object run() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}