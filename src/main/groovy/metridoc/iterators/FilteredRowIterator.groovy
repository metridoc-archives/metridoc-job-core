package metridoc.iterators

/**
 * Created with IntelliJ IDEA on 5/31/13
 * @author Tommy Barker
 */
class FilteredRowIterator extends RowIterator {
    Closure<Boolean> filter
    RowIterator iterator

    @Override
    protected Map computeNext() {
        assert filter: "filter cannot be null"
        assert iterator != null: "iterator cannot be null"

        while (iterator.hasNext()) {
            def next = iterator.next()
            if (filter.call(next)) {
                return next
            }
        }

        return endOfData()
    }
}
