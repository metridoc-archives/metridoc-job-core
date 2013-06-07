package metridoc.core

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 6/7/13
 * @author Tommy Barker
 */
class MetridocJobSpec extends Specification {

    def job = new MetridocJob() {
        @Override
        def configure() {
            //do nothing
        }
    }

    def "basic to and from spec"() {
        when: "when data is sent and consumed from a seda endpoint"
        def fromCalled = false
        job.with {
            asyncSend("seda:test", "testBody")
            consume("seda:test") {
                assert "testBody" == it
                fromCalled = true
            }
        }

        then: "consumption of the data should happen"
        fromCalled
    }
}
