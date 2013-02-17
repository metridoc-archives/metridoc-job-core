package metridoc.iterators

import org.junit.After
import org.junit.Test
import org.springframework.core.io.ClassPathResource

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/28/12
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
class XlsIteratorCreatorTest {

    def file = new File("src/test/groovy/metridoc/iterators/locations.xls")
    def iterator = new XlsIterator(inputStream: file.newInputStream())

    @After
    void cleanup() {
        iterator.close()
    }

    @Test
    void "testing a location file we use in another program where we found errors"() {
        def row = iterator.next()
        assert "LOCATION_ID" == row.get(0)
    }

    @Test
    void "testing cell conversion issues we were seeing with formulas"() {
        iterator.next()
        def row = iterator.next()
        assert 1 == row.get(0)
    }

    @Test
    void "if a row has no data, then the row should be null"() {
        assert iterator.sheet.getRow(358)
        assert null == iterator.sheet.getRow(359)
    }

    @Test
    void "if there is no more data an error is thrown"() {
        def next
        (1..359).each {
            next = iterator.next()
        }

        assert 359 == next.get(0) as Integer
        assert !iterator.hasNext()

        try {
            iterator.next()
            assert false: "exception should have occurred"
        } catch(NoSuchElementException ex) {

        }
    }
}
