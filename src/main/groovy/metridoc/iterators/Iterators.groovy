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

    static RowIterator toFilteredRowIterator(RowIterator iterator, Closure filter) {
        assert iterator: "iterator must not be null"
        assert filter: "filter must not be null"

        new FilteredRowIterator(
                filter: filter,
                iterator: iterator
        )
    }
}
