package metridoc.iterators

/**
 * Created with IntelliJ IDEA on 6/14/13
 * @author Tommy Barker
 */
class FilteredAndTransformedRecordIterator extends RecordIterator {

    RecordIterator iterator
    /**
     * should return null if we should not collect it
     */
    Closure<Record> transformer

    @Override
    protected Record computeNext() {
        assert transformer: "transformer cannot be null"
        assert iterator != null: "iterator cannot be null"

        while (iterator.hasNext()) {
            def next = iterator.next()
            def response = transformer.call(next.clone())

            if (response) {
                return response
            }
        }

        endOfData()
    }
}
