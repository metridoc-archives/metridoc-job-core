package metridoc.camel

import org.apache.camel.Exchange
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 12/29/12
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
class CamelScriptTest {

    def ownerProperty = true

    @Test
    void "test some basic routing, default route called is direct:start"() {

        def calledMe = false
        CamelScript.runRoute {
            from("direct:start").process {
                calledMe = true
            }
        }

        assert calledMe
    }

    @Test
    void "property from owner should exist in registry"() {
        CamelScript.runRoute {
            from("direct:start").process {Exchange exchange ->
                assert exchange.context.registry.lookup("ownerProperty")
            }
        }
    }
}
