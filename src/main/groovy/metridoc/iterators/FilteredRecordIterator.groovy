package metridoc.iterators

/**
 * Created with IntelliJ IDEA on 5/31/13
 * @author Tommy Barker
 */
class FilteredRecordIterator extends RecordIterator {
    Closure<Boolean> filter
    RecordIterator iterator

    @Override
    protected Record computeNext() {
        assert filter: "filter cannot be null"
        assert iterator != null: "iterator cannot be null"

        while (iterator.hasNext()) {
            def next = iterator.next()
            if (filter.call(next.clone())) {
                return next
            }
        }

        return endOfData()
    }
}
