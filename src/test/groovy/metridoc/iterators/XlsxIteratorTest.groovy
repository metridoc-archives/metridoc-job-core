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
        assert "LOCATION_ID" == iterator.headers.get(0)
        def row = iterator.next()
        assert 1 == row.get("LOCATION_ID")
    }

    @SuppressWarnings("GroovyVariableNotAssigned")
    @Test
    void "if there is no more data an error is thrown"() {
        def next
        (1..358).each {
            next = iterator.next()
        }

        assert 359 == next.get("LOCATION_ID")
        assert !iterator.hasNext()

        try {
            iterator.next()
            assert false: "exception should have occurred"
        } catch (NoSuchElementException ignored) {

        }
    }

}
