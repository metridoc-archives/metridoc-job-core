package metridoc.core.tools

import metridoc.core.MetridocScript
import org.apache.commons.lang.StringUtils

abstract class RunnableTool extends DefaultTool {

    private hasRun = false

    abstract String getUsage()

    void setDefaultTarget(String target) {
        MetridocScript.getManager(binding).defaultTarget = target
    }

    def execute() {

        synchronized (this) {
            if (hasRun) {
                throw new ToolException("${this.getClass().simpleName} has already run, and can only run once")
            }
            hasRun = true
        }

        def parseTool = includeTool(ParseArgsTool)
        //in case args was set after this was initialized
        parseTool.setBinding(binding)

        def thisToolName = StringUtils.uncapitalize(this.getClass().simpleName)
        if (!binding.hasVariable(thisToolName)) {
            binding.setVariable(thisToolName, this)
        }
        //redo injection in case properties were set after including the tool
        def manager = MetridocScript.getManager(binding)
        manager.handlePropertyInjection(this)
        configure()
        String target = getVariable("target", String)
        if (target) {
            setDefaultTarget(target)
        }
        String defaultTarget = manager.defaultTarget
        if (manager.targetMap.containsKey(defaultTarget)) {
            manager.runDefaultTarget()
        }
    }

    abstract configure()
}
