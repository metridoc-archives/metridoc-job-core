package metridoc.writers

import metridoc.core.StepManager
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 7/5/13
 * @author Tommy Barker
 */
class WriteResponseSpec extends Specification {

    def response = new WriteResponse(body: [foo: "bar", bar: 5, foobar: 1.0, foobaz: 2.0])

    def "asType should return a representation of the response based on info stored in response"() {
        expect:
        b == response.asType(a)

        where:
        a       | b
        String  | "bar"
        Integer | 5
    }

    def "asType throws a GroovyCastException if there are multiple possibilities or no possibilities"() {
        when: "asType called on double"
        response as Double

        then: "an error is thrown"
        thrown GroovyCastException

        when: "asType is called when there is no possibility"
        response as StepManager //pick a random object

        then: "an error is thrown"
        thrown GroovyCastException
    }
}
