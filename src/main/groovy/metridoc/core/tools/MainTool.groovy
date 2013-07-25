package metridoc.core.tools

import groovy.text.SimpleTemplateEngine

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class MainTool extends RunnableTool {
    Map<String, Class<RunnableTool>> runnableTools

    @Override
    def configure() {
        assert runnableTools: "runnableTools cannot be null or empty"
        List params = getVariable("params") as List
        assert params: "params cannot be null or empty"
        includeTool(runnableTools[params[0] as String]).execute()
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
