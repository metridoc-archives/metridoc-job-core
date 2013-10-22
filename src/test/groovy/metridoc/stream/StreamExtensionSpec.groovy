package metridoc.stream

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class StreamExtensionSpec extends Specification {

    void "test iterator mapping"() {
        when:
        StreamResponse result = [0,1,2,3].toStream().map {
            it * 2
        }.process{}

        then:
        4 == result.written
        4 == result.total
    }

    void "test filtering"() {
        when:
        StreamResponse result = [0,1,2,3].toStream().filter {
            it % 2 == 0
        }.process{}

        then:
        2 == result.written
        4 == result.total
        2 == result.ignored
    }

    void "test validation"() {
        when:
        StreamResponse result = [0,1,2,3,4].toStream().filter {
            it % 2 == 0
        }.map{it * 2}.validate {assert it != 8}.process{}

        then:
        2 == result.written
        5 == result.total
        2 == result.ignored
        1 == result.invalid
    }
}
