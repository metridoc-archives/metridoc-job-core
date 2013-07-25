package metridoc.core.tools

import groovy.text.SimpleTemplateEngine
import org.slf4j.LoggerFactory

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class MainTool extends RunnableTool {
    Map<String, Class<RunnableTool>> runnableTools
    boolean stacktrace = false
    boolean exitOnError = true

    @Override
    def configure() {
        try {
            includeTool(LogTool)
            assert runnableTools: "runnableTools cannot be null or empty"
            List params = getVariable("params") as List
            assert params: "params cannot be null or empty"
            includeTool(runnableTools[params[0] as String]).execute()
        }
        catch (Throwable throwable) {
            if (exitOnError) {
                if (stacktrace) {
                    LoggerFactory.getLogger(MainTool).error("error occurred running job", throwable)
                }
                else {
                    LoggerFactory.getLogger(MainTool).error(throwable.message)
                }
            }
            else {
                //let someone else take care of it
                throw throwable
            }
        }
    }

    String usage = '\n' +
            '<run method> <job> options\n' +
            '<% if(runnableTools) { %>\n' +
            'Possible Jobs\n' +
            '=============\n' +
            '<% runnableTools.keySet().each { %>\n' +
            '<% println " --> $it" %>\n' +
            '<% } %>\n' +
            '<% } %>\n'


    String getUsage() {
        def templateBinding = [runnableTools: runnableTools]
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(usage)
        template.make(templateBinding).toString()
    }
}
