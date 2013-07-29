package metridoc.core.tools

import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit

/**
 * Had to create a normal unit test here.  Spock does not like the ExpectedSystemExit rule
 * @author Tommy Barker
 */
class MainToolExitSpec {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    void "exits on help spec"() {
        def binding = new Binding()
        binding.args = ["-h"] as String[]
        exit.expectSystemExitWithStatus(0)
        new MainTool(binding: binding).execute()
    }

}
