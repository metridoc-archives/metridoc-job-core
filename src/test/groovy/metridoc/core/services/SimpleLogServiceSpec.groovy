package metridoc.core.services

import spock.lang.Specification

import static SimpleLogService.DEFAULT_LOG_LEVEL
import static SimpleLogService.METRIDOC_LOGGER

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class SimpleLogServiceSpec extends Specification {

    void "logging is added after includeTool is run"() {
        when:
        def binding = new Binding()
        binding.includeService(SimpleLogService)

        then:
        System.getProperty(METRIDOC_LOGGER)
        System.getProperty(DEFAULT_LOG_LEVEL)
    }
}
