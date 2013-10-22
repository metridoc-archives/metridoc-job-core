package metridoc.stream

import groovy.stream.Stream
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class XlsxStreamSpec extends Specification{

    def file = new File("src/test/groovy/metridoc/stream/locations.xlsx")
    def iterator = Stream.fromXlsx(file.newInputStream())

    void "testing basic iteration"() {
        given:
        def iterator = new XlsxStream(inputStream: file.newInputStream())

        when:
        def header = iterator.headers.get(0)

        then:
        "LOCATION_ID" == header

        when:
        def row = iterator.next()

        then:
        1 == row.get("LOCATION_ID")
    }

    void "if there is no more data an error is thrown"() {
        when:
        def next
        (1..358).each {
            next = iterator.next()
        }

        then:
        359 == next.get("LOCATION_ID")
        !iterator.hasNext()
    }
}
