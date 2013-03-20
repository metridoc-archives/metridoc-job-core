package metridoc.core.tools

import metridoc.core.TargetManager

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/18/13
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class RunnableTool extends DefaultTool{

    def run() {
        //redo injection in case properties were set after including the tool
        TargetManager.handlePropertyInjection(this)
        return doRun()
    }

    abstract doRun()
}
