package metridoc.core

import metridoc.core.tools.CamelTool
import metridoc.core.tools.RunnableTool

/**
 * Created with IntelliJ IDEA on 6/6/13
 * @author Tommy Barker
 *
 * A helpful class that wraps around the scripting and routing libraries.  Generally extended by Grails services
 */
abstract class MetridocJob extends RunnableTool {

    /**
     * specific to grails.  Makes sure that all extensions are of prototype scope
     */
    static scope = "prototype"

    @Delegate
    @Lazy
    CamelTool camelTool = {
        includeTool(CamelTool)
    }()

    @Override
    def execute() {
        def result = super.execute()
        camelTool.camelContext.stop()
        return result
    }
}
