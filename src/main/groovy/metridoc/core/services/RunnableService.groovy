package metridoc.core.services

import metridoc.core.MetridocScript
import org.apache.commons.lang.StringUtils

/**
 * @author Tommy Barker
 */
abstract class RunnableService extends DefaultService {
    private hasRun = false

    /**
     * @deprecated
     * @param step
     */
    void setDefaultTarget(String step) {
        setDefaultStep(step)
    }

    def execute() {

        synchronized (this) {
            if (hasRun) {
                throw new ServiceException("${this.getClass().simpleName} has already run, and can only run once")
            }
            hasRun = true
        }

        def parseTool = includeService(ParseArgsService)
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
        String step = getVariable("target", String)
        step = getVariable("step", String) ?: step
        if (step) {
            setDefaultStep(step)
        }
        String defaultTarget = manager.defaultStep
        if (manager.stepMap.containsKey(defaultTarget)) {
            manager.runDefaultStep()
        }
    }

    abstract configure()
}
