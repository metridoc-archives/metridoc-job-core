package metridoc.writers

import com.google.common.collect.Table
import metridoc.iterators.Iterators
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 7/5/13
 * @author Tommy Barker
 */
class SplitIteratorWriterSpec extends Specification {

    InputStream data

    def setup() {
        def text = "a|b\nc|d"
        data = new ByteArrayInputStream(text.bytes)
    }

    def "splitIteratorWriter allows for writing to multiple writers"() {
        given: "a delimited iterator"
        def iterator = Iterators
                .createIterator("delimited", delimiter: /\|/, inputStream: data)

        when: "written to two writers"
        def writer1 = Iterators.createWriter("table", name: "table1")
        def writer2 = Iterators.createWriter("table", name: "table2")
        def result = iterator.writeTo("split", writers: [writer1, writer2])

        then: "we should be able to get appropriate data"
        result[0] as Table
        result[1] as Table
    }
}
