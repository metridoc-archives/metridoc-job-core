package metridoc.iterators

import org.junit.Test

/**
 * Created with IntelliJ IDEA on 5/29/13
 * @author Tommy Barker
 */
class XlsxIteratorTest {

    def file = new File("src/test/groovy/metridoc/iterators/locations.xlsx")
    def iterator = new XlsxIterator(inputStream: file.newInputStream())

    @Test
    void "testing basic iteration"() {
        def row = iterator.next()
        assert "LOCATION_ID" == row.get(0)
        row = iterator.next()
        assert 1 == row.get(0)
    }

    @Test
    void "if there is no more data an error is thrown"() {
        def next
        (1..359).each {
            next = iterator.next()
        }

        assert 359 == next.get(0)
        assert !iterator.hasNext()

        try {
            iterator.next()
            assert false: "exception should have occurred"
        } catch(NoSuchElementException ex) {

        }
    }

}
