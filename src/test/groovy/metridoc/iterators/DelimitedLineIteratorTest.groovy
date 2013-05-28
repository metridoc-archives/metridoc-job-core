package metridoc.iterators

import org.junit.Test

/**
 * Created with IntelliJ IDEA on 5/29/13
 * @author Tommy Barker
 */
class DelimitedLineIteratorTest {

    def text = "blah|blam\nbloom|blim"
    def inputStream = new ByteArrayInputStream(text.bytes)
    def iterator = new DelimitedLineIterator(inputStream: inputStream, delimiter: /\|/)


    @Test
    void "test basic iteration"() {
        def next = iterator.next()
        assert "blah" == next[0]
        assert "blam" == next[1]
        next = iterator.next()
        assert "bloom" == next[0]
        assert "blim" == next[1]

        try {
            iterator.next()
            assert false : "exception should have occurred"
        } catch (NoSuchElementException ex) {
        }
    }


}
