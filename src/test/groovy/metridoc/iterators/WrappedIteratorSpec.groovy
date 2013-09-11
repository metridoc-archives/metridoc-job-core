package metridoc.iterators

import spock.lang.Specification

/**
 * @author Tommy Barker
 */
class WrappedIteratorSpec extends Specification {

    void "test filter"() {
        given:
        def records = [
                [foo: "bar"],
                [foo: "bar"],
                [foo: "bam"],
                [foo: "blam"],
                [foo: "blam"],
        ]
        def recordIterator = Iterators.toRecordIterator(records)

        when:
        //let's try a chained filter
        def size = recordIterator
                .filter { it.body.foo == "bam" || it.body.foo == "blam" }
                .filter { it.body.foo == "blam" }
                .collect().size()

        then:
        2 == size
    }

    void "let's try a complex transformation"() {
        given:
        def records = [
                [foo: "bar"],
                [foo: "bar"],
                [foo: "bam"],
                [foo: "blam"],
                [foo: "blam"],
        ]
        def recordIterator = Iterators.toRecordIterator(records)

        when:
        //let's try a chained filter
        def size = recordIterator
                .map { it.body.foo = "blam" }
                .filter { it.body.foo == "blam" }
                .collect().size()

        then:
        5 == size
    }
}
