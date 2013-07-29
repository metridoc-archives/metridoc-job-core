package metridoc.core.tools

import camelscript.CamelGLite

/**
 * Each routing method creates a new CamelContext using the binding to fill its registry and shuts it down before
 * the method is called.  This ensures that messages don't leek in after the method is called.  Suppose you consume
 * consume a queue endpoint such as jms or seda, after the method ends more messages could leak in if the context is not
 * shut down
 */
class CamelTool {

    Binding binding
    @Delegate
    CamelGLite delegate

    void init() {
        delegate = new CamelGLite(binding)
    }
}