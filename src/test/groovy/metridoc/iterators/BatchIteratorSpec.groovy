package metridoc.iterators

import spock.lang.Specification

/**
 * @author Tommy Barker
 */
class BatchIteratorSpec extends Specification {

    void "BatchIterator can receive an empty iterator"() {
        when:
        new BatchIterator(Iterators.toRecordIterator([].iterator()), 50)

        then:
        noExceptionThrown()
    }
}
