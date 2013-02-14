package metridoc.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.Exchange

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/5/12
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class ManagedExceptionRouteBuilder extends RouteBuilder{

    Throwable routeException
    Closure exceptionHandler

    abstract void doConfigure()

    @Override
    void configure() {

        use(CamelExtensions) {

            onException(Throwable.class).process {Exchange exchange ->
                def exception = exchange.getException()
                if (exception) {
                    handleException(exception, exchange)
                } else {
                    exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT)
                    if (exception) {
                        handleException(exception, exchange)
                    } else {
                        exception = new RuntimeException("An unknown exception occurred, if the log is not " +
                                "sufficient to determine what happened consider setting logging level to DEBUG")
                        handleException(exception, exchange)
                    }
                }
            }

            doConfigure()
        }
    }

    Throwable getFirstException() {
        return routeException
    }

    void handleException(Throwable throwable, Exchange exchange) {
        if (exceptionHandler) {
            exceptionHandler.call(throwable, exchange)
        } else {

            if (routeException) {
                log.error("an additional exception occurred", throwable)
                return //only catch and throw the first one
            }

            routeException = throwable
        }
    }
}
