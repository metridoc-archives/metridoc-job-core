package metridoc.core.tools

import com.google.common.eventbus.EventBus
import groovy.util.logging.Slf4j

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/21/13
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class EventTool {

    Binding binding

    void create(String eventName) {
        if (binding.hasVariable("${eventName}Bus")) {
            log.debug "${eventName}Bus already exists"
        } else {
            binding."${eventName}Bus" = new EventBus(eventName)
        }
    }

    EventBus getBus(String eventName) {
        create(eventName)
        binding."${busName(eventName)}"
    }

    static private String busName(String name) {
        if (name.endsWith("Bus")) return name

        return "${name}Bus"
    }
}
