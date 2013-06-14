package metridoc.iterators

/**
 * Created with IntelliJ IDEA on 6/14/13
 * @author Tommy Barker
 */
class FilteredAndTransformedRowIterator extends RowIterator {

    RowIterator iterator
    /**
     * should return null if we should not collect it
     */
    Closure<Map> transformer

    @Override
    protected Map computeNext() {
        assert transformer: "transformer cannot be null"
        assert iterator != null: "iterator cannot be null"

        while (iterator.hasNext()) {
            def next = iterator.next()
            Map row = transformer.call(next)

            if (row) return row
        }

        endOfData()
    }
}
