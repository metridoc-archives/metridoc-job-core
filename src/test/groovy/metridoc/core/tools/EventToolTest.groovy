package metridoc.core.tools

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/21/13
 * Time: 6:40 PM
 * To change this template use File | Settings | File Templates.
 */
class EventToolTest {

    @Test
    void "busName converts an eventName to have [Bus] at the end, or does nothing if it already ends with [Bus]"() {
        assert "fooBus" == EventTool.busName("foo")
        assert "barBus" == EventTool.busName("barBus")
    }

    @Test
    void "get bus generates a bus if it doesn't already exist"() {
        assert new EventTool(binding: new Binding()).getBus("foo")
    }
}
