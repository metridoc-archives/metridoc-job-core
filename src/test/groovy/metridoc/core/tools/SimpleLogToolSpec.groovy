package metridoc.core.tools

import metridoc.core.MetridocScript
import spock.lang.Specification

import static SimpleLogTool.DEFAULT_LOG_LEVEL
import static SimpleLogTool.METRIDOC_LOGGER

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class SimpleLogToolSpec extends Specification {

    void "logging is added after includeTool is run"() {
        when:
        def binding = new Binding()
        use(MetridocScript) {
            binding.includeTool(SimpleLogTool)
        }

        then:
        System.getProperty(METRIDOC_LOGGER)
        System.getProperty(DEFAULT_LOG_LEVEL)
    }
}
