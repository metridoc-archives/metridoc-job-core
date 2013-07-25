package metridoc.core

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 6/7/13
 * @author Tommy Barker
 */
class MetridocJobSpec extends Specification {

    def "basic to and from spec"() {
        given:
        def metridocJob = new HelperJob()

        when: "when data is sent and consumed from a seda endpoint by calling execute"
        metridocJob.execute()

        then: "consumption of the data should happen"
        metridocJob.fromCalled
    }
}

class HelperJob extends MetridocJob {

    boolean fromCalled = false
    String usage = "helper job"

    @Override
    def configure() {
        asyncSend("seda:test", "testBody")
        consume("seda:test") {
            assert "testBody" == it
            fromCalled = true
        }
    }
}