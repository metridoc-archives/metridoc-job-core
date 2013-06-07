package metridoc.core

import camelscript.CamelGLite
import camelscript.ResponseException
import metridoc.core.tools.CamelTool
import metridoc.core.tools.RunnableTool
import org.apache.camel.Exchange

import java.util.concurrent.Future

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

    CamelTool getCamelTool() {
        def result = getVariable("camelTool", CamelTool)
        if (result == null) {
            result = includeTool(CamelTool)
        }

        return result
    }

    @Override
    void setBinding(Binding binding) {
        super.setBinding(binding)
        includeTool(CamelTool)
    }

    @Override
    def execute() {
        def result = super.execute()
        camelTool.close()
        return result
    }

    CamelGLite bind(object) {
        camelTool.bind(object)
    }

    CamelGLite bind(String name, object) {
        camelTool.bind(name, object)
    }

    CamelGLite consume(String endpoint, Closure closure) {
        camelTool.consume(endpoint, closure)
    }

    void consumeForever(String endpoint, Closure closure) {
        camelTool.consumeForever(endpoint, closure)
    }

    void consumeForever(String endpoint, long wait, Closure closure) {
        camelTool.consumeForever(endpoint, wait, closure)
    }

    CamelGLite consumeNoWait(String endpoint, Closure closure) {
        camelTool.consumeNoWait(endpoint, closure)
    }

    void consumeWait(String endpoint, long wait, Closure closure) {
        camelTool.consumeWait(endpoint, wait, closure)
    }

    void consumeTillDone(String endpoint, long wait = 5000L, Closure closure) {
        camelTool.consumeTillDone(endpoint, wait, closure)
    }

    public <T> T convertTo(Class<T> convertion, valueToConvert) {
        camelTool.convertTo(convertion, valueToConvert)
    }

    Exchange send(String endpoint, body) throws ResponseException {
        camelTool.send(endpoint, body)
    }

    Exchange send(String endpoint, body, Map headers) throws ResponseException {
        camelTool.send(endpoint, body, headers)
    }

    Future<Exchange> asyncSend(String endpoint, body) {
        camelTool.asyncSend(endpoint, body)
    }

    Future<Exchange> asyncSend(String endpoint, body, Map headers) {
        camelTool.asyncSend(endpoint, body, headers)
    }
}
