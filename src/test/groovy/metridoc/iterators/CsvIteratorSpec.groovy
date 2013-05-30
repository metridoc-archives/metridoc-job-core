package metridoc.iterators

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 5/30/13
 * @author Tommy Barker
 */
class CsvIteratorSpec extends Specification {

    CsvIterator iterator
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
        setupSimpleIterator()
        iterator.headers = ["c", "b", "a"]

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

    void "errors are thrown when header size and result size dont match"() {
        given:
        setupSimpleIterator()
        iterator.headers = ["a", "b"]

        when:
        iterator.next()

        then:
        thrown IllegalStateException
    }

    void setupSimpleIterator() {
        def inputStream = new ByteArrayInputStream(simpleData.bytes)
        iterator = new CsvIterator(inputStream: inputStream)
    }
}
