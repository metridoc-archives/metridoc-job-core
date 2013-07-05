package metridoc.iterators

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 5/29/13
 * @author Tommy Barker
 */
class DelimitedLineIteratorSpec extends Specification {

    def text = "blah|blam\nbloom|blim"
    def inputStream = new ByteArrayInputStream(text.bytes)
    def iterator = new DelimitedLineIterator(inputStream: inputStream, delimiter: /\|/)

    void "test basic iteration"() {

        when:
        def next = iterator.next()

        then:
        "blah" == next.body[0]
        "blam" == next.body[1]

        when:
        next = iterator.next()

        then:
        "bloom" == next.body[0]
        "blim" == next.body[1]

        when:
        iterator.next()

        then:
        thrown NoSuchElementException
    }

    void "delimiter must be set"() {
        when:
        iterator.delimiter = null
        iterator.next()

        then:
        thrown IllegalArgumentException
    }

    void "headers size must be the same as each line"() {
        given:
        iterator.headers = ["foo"]
        iterator.delimitTill = 0

        when:
        iterator.next()

        then:
        thrown IllegalStateException
    }
}
