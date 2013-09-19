package metridoc.core.tools

import metridoc.core.services.CamelService

/**
 * Each routing method creates a new CamelContext using the binding to fill its registry and shuts it down before
 * the method is called.  This ensures that messages don't leek in after the method is called.  Suppose you consume
 * consume a queue endpoint such as jms or seda, after the method ends more messages could leak in if the context is not
 * shut down
 *
 * @deprecated
 */
class CamelTool extends CamelService {

}