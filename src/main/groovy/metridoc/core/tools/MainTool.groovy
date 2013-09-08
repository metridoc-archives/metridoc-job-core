package metridoc.core.tools

import groovy.util.logging.Slf4j

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
@Slf4j
class MainTool extends RunnableTool {
    Map<String, Class<? extends RunnableTool>> runnableTools = [:]
    String defaultTool

    public static void main(String[] args) {
        Binding binding = new Binding()
        binding.args = args
        def mainTool = new MainTool(binding: binding)
        mainTool.execute()
    }

    @SuppressWarnings("GroovyVariableNotAssigned")
    @Override
    def configure() {
        assert runnableTools: "runnableTools cannot be null or empty"
        List params = getVariable("params") as List
        assert params || defaultTool: "params cannot be null or empty, or a defaultTool must be specified"
        String toolToRun = params ? params[0] : defaultTool
        log.info "running $toolToRun"
        assert runnableTools.containsKey(toolToRun): "[$toolToRun] does not exist"
        def tool = includeTool(runnableTools[toolToRun])
        tool.execute()
    }
}
