package metridoc.core.services

import groovy.util.logging.Slf4j

/**
 * @author Tommy Barker
 */
@Slf4j
class MainService extends RunnableService {

    Map<String, Class<? extends RunnableService>> runnableServices = [:]
    String defaultService

    /**
     * @deprecated
     * @param runnableTools
     */
    void setRunnableTools(Map<String, Class<? extends RunnableService>> runnableTools) {
        this.runnableServices = runnableTools
    }

    Map<String, Class<? extends RunnableService>> getRunnableTools() {
        return runnableServices
    }

    String getDefaultTool() {
        return defaultService
    }

    /**
     * @deprecated
     * @param defaultTool
     */
    void setDefaultTool(String defaultTool) {
        this.defaultService = defaultTool
    }

    public static void main(String[] args) {
        Binding binding = new Binding()
        binding.args = args
        def mainTool = new MainService(binding: binding)
        mainTool.execute()
    }

    @SuppressWarnings("GroovyVariableNotAssigned")
    @Override
    def configure() {
        assert runnableServices: "runnableServices cannot be null or empty"
        List params = getVariable("params") as List
        assert params || defaultService: "params cannot be null or empty, or a defaultTool must be specified"
        String toolToRun = params ? params[0] : defaultService
        log.info "running $toolToRun"
        assert runnableServices.containsKey(toolToRun): "[$toolToRun] does not exist"
        def tool = includeService(runnableServices[toolToRun])
        tool.execute()
    }
}
