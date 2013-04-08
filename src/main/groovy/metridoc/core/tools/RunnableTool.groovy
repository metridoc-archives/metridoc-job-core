package metridoc.core.tools

import metridoc.core.MetridocScript
import org.apache.commons.lang.StringUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/18/13
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class RunnableTool extends DefaultTool {

    void setDefaultTarget(String target) {
        MetridocScript.getManager(binding).defaultTarget = target
    }

    def execute() {
        def thisToolName = StringUtils.uncapitalize(this.getClass().simpleName)
        if(!binding.hasVariable(thisToolName)) {
            binding.setVariable(thisToolName, this)
        }
        //redo injection in case properties were set after including the tool
        def manager = MetridocScript.getManager(binding)
        manager.handlePropertyInjection(this)
        configure()
        String defaultTarget = manager.defaultTarget
        if(manager.targetMap.containsKey(defaultTarget)) {
            manager.runDefaultTarget()
        }
    }

    abstract configure()
}
