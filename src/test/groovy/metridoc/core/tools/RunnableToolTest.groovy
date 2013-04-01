package metridoc.core.tools

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 4/1/13
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
class RunnableToolTest {

    @Test
    void "dealing with bug where the runnable tool crashes since it is trying to deal with property injection incorrectly"() {
        def runnableTool = new RunnableTool() {
            @Override
            def doRun() {
                //do nothing
            }
        }

        runnableTool.run()
    }
}
