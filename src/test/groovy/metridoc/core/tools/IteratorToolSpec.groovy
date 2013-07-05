package metridoc.core.tools

import com.google.common.collect.Table
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 7/5/13
 * @author Tommy Barker
 */
class IteratorToolSpec extends Specification {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()
    File foo

    def setup() {
        foo = folder.newFile("foo")
        foo.withPrintWriter {
            it.println("a|b")
            it.println("b|c")
            return it
        }
    }

    void "iterators are created via lookup"() {
        when: "a delimited iterator is built"
        def iterator = new IteratorTool().createIterator("delimited", delimiter: /\|/, file: foo)
        def next1 = iterator.next()
        def next2 = iterator.next()

        then: "we can iterate over the file contents"

        "a" == next1[0]
        "b" == next1[1]

        "b" == next2[0]
        "c" == next2[1]

        try {
            iterator.next()
            assert false: "exception should have occurred"
        }
        catch (NoSuchElementException ignored) {
            //do nothing
        }
    }

    void "iterators should also be able to write"() {
        when: "a delimited iterator is built"
        def iterator = new IteratorTool().createIterator("delimited", delimiter: /\|/, file: foo)

        and: "and is written to a guave table"
        def table = iterator.writeTo("table") as Table

        def set = table.columnKeySet()
        then:
        set.contains(0)
        set.contains(1)
    }
}
