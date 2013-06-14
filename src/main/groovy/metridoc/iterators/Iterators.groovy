package metridoc.iterators

/**
 * Created with IntelliJ IDEA on 5/31/13
 * @author Tommy Barker
 */
class Iterators {

    static RowIterator toRowIterator(Iterator<Map> iterator) {
        assert iterator: "iterator must not be null or empty"
        new RowIterator() {
            @Override
            protected Map computeNext() {
                if (iterator.hasNext()) {
                    return iterator.next()
                }

                return endOfData()
            }
        }
    }

    static RowIterator toRowIterator(List<Map> iterator) {
        assert iterator: "iterator must not be null or empty"
        toRowIterator(iterator.iterator())
    }

    static RowIterator toFilteredRowIterator(RowIterator iterator, Closure<Boolean> filter) {
        assert iterator: "iterator must not be null"
        assert filter: "filter must not be null"

        new FilteredRowIterator(
                filter: filter,
                iterator: iterator
        )
    }

    /**
     *
     * @param rowIterator
     * @param transformer should return null if it should not be collected, otherwise should return a Map
     * @return
     */
    static RowIterator toFilteredAndTransformedIterator(RowIterator rowIterator, Closure<Map> transformer) {
        new FilteredAndTransformedRowIterator(
                iterator: rowIterator,
                transformer: transformer
        )
    }
}
