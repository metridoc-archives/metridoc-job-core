package metridoc.stream

import groovy.stream.Stream
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class CsvStreamSpec extends Specification {

    Stream<Map> iterator
    def simpleData = """a,b,c
"foo",5,
"bar",5,"baz\""""

    void "test basic csv file"() {
        given:
        setupSimpleIterator()

        expect:
        while (iterator.hasNext()) {
            def next = iterator.next()
            value1 == next.a
            value2 == next.b
            value3 == next.c
        }

        where:
        value1 | value2 | value3
        "foo"  | 5      | null
        "bar"  | 5      | "baz"
    }

    void "can provide headers instead of assuming the top line is a header"() {
        given:
        setupSimpleIterator(["c", "b", "a"])

        expect:
        while (iterator.hasNext()) {
            def next = iterator.next()
            value1 == next.a
            value2 == next.b
            value3 == next.c
        }

        where:
        value1 | value2 | value3
        "c"    | "b"    | "a"
        null   | 5      | "foo"
        "baz"  | 5      | "bar"
    }

    void "full workflow with each"() {
        when:
        int count = 0
        Stream.fromCsv(getData()).map{
            count++
        }.each{}

        then:
        noExceptionThrown()
        2 == count
    }

    void "errors are thrown when header size and result size dont match"() {
        given:
        setupSimpleIterator(["a", "b"])

        when:
        iterator.next()

        then:
        thrown IllegalStateException
    }

    InputStream getData() {
        new ByteArrayInputStream(simpleData.bytes)
    }

    void setupSimpleIterator(headers = null) {
        iterator = Stream.fromCsv(getData(), headers)
    }
}
