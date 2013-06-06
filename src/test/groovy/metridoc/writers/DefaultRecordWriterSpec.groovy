package metridoc.writers

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 6/5/13
 * @author Tommy Barker
 */
class DefaultRecordWriterSpec extends Specification {

    def "error is thrown if record does not contain an integer value with name line or lineNumber"() {
        given: "a basic implementation"
        def writeCalled = 0
        def writer = new DefaultRowWriter() {
            @Override
            void write(int line, Map<String, Object> record) {
                writeCalled++
            }
        }

        when: "a record is passed with no line or lineNumber variable"
        writer.write([foo: "bar"])

        then: "an AssertionError occurrs"
        thrown AssertionError
        0 == writeCalled

        when: "a record is passed with a line variable that is not an integer"
        writer.write([lineNumber: "bar"])

        then: "an AssertionError is thrown"
        thrown IllegalArgumentException
        0 == writeCalled

        when: "a correct record is written"
        writer.write(lineNumber: 1)

        then: "the record is written"
        1 == writeCalled
    }

}
