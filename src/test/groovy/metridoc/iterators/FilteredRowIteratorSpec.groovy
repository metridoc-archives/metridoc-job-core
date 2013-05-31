package metridoc.iterators

import spock.lang.Specification

import static metridoc.iterators.Iterators.toFilteredRowIterator
import static metridoc.iterators.Iterators.toRowIterator
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric

/**
 * Created with IntelliJ IDEA on 5/31/13
 * @author Tommy Barker
 */
class FilteredRowIteratorSpec extends Specification {

    void "test basic filtering"() {
        given: "an iterator of maps with random data which will be filtered out"
        def iterator = []
        (0..10).each {
            Map row = (0..5).collectEntries { index ->
                [index, randomAlphanumeric(10)]
            }
            iterator.add(row)
        }

        and: "two records that will not be filtered out"
        iterator.add((0..5).collectEntries { index ->
            [index, index]
        })
        iterator.add((0..5).collectEntries { index ->
            [index, index]
        })

        and: "construct a filtered iterator from it"
        def filteredIterator = toFilteredRowIterator(toRowIterator(iterator)) { Map row ->
            row[0] == 0
        }

        when: "I call the iterator twice"
        def next1 = filteredIterator.next()
        def next2 = filteredIterator.next()

        then: "I get the correct output"
        0 == next1[0]
        0 == next2[0]
        next1 == next2

        when: "I call the iterator a third time"
        filteredIterator.next()

        then: "An error will be thrown since there are no more elements"
        thrown NoSuchElementException
    }
}
