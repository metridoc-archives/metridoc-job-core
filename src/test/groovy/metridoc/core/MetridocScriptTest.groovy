package metridoc.core

import org.junit.Test

class MetridocScriptTest {

    def script = new MetridocScriptHelper()

    @Test
    void "test initializing the targetManager"() {
        MetridocScript.initializeTargetManagerIfNotThere(script)
        assert script.targetManager
    }

    @Test
    void "target manager can only be initialize once"() {
        MetridocScript.initializeTargetManagerIfNotThere(script)
        def targetManager =  script.targetManager
        MetridocScript.initializeTargetManagerIfNotThere(script)
        assert targetManager == script.targetManager
    }
}

class MetridocScriptHelper extends Script {

    @Override
    Object run() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}