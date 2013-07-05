package metridoc.iterators

import org.junit.After
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 *
 * @author Thomas Barker
 */
class XlsIteratorTest {

    def file = new File("src/test/groovy/metridoc/iterators/locations.xls")
    def iterator = new XlsIterator(inputStream: file.newInputStream())

    @After
    void cleanup() {
        iterator.close()
    }

    @Test
    void "testing a location file we use in another program where we found errors"() {
        assert "LOCATION_ID" == iterator.headers.get(0)
    }

    @Test
    void "testing cell conversion issues we were seeing with formulas"() {
        iterator.next()
        def row = iterator.next()
        assert 2 == row.body.get("LOCATION_ID")
    }

    @Test
    void "if a row has no data, then the row should be null"() {
        assert iterator.sheet.getRow(358)
        assert null == iterator.sheet.getRow(359)
    }

    @SuppressWarnings("GroovyVariableNotAssigned")
    @Test
    void "if there is no more data an error is thrown"() {
        def next
        (1..358).each {
            next = iterator.next()
        }

        assert 359 == next.body.get("LOCATION_ID")
        assert !iterator.hasNext()

        try {
            iterator.next()
            assert false: "exception should have occurred"
        }
        catch (NoSuchElementException ignored) {

        }
    }
}
