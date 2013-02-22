package metridoc.core

import metridoc.core.tools.HibernateTool
import org.junit.Test

class MetridocScriptTest {

    Script script = new MetridocScriptHelper()

    @Test
    void "test initializing the targetManager"() {
        MetridocScript.initializeTargetManagerIfNotThere(script)
        assert script.targetManager
    }

    @Test
    void "target manager can only be initialize once"() {
        MetridocScript.initializeTargetManagerIfNotThere(script)
        def targetManager = script.targetManager
        MetridocScript.initializeTargetManagerIfNotThere(script)
        assert targetManager == script.targetManager
    }

    @Test
    void "include tool returns the tool that has been instantiated or instantiated in the past"() {
        def tool = MetridocScript.includeTool(script, HibernateTool)
        assert tool
        assert tool == MetridocScript.includeTool(script, HibernateTool)
    }
}

class MetridocScriptHelper extends Script {

    @Override
    Object run() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}