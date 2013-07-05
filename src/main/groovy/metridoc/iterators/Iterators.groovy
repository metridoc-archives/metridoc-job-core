package metridoc.iterators

import metridoc.writers.*

/**
 * Created with IntelliJ IDEA on 5/31/13
 * @author Tommy Barker
 */
class Iterators {

    public static final ITERATORS = [
            delimited: DelimitedLineIterator,
            sql: SqlIterator,
            xls: XlsIterator,
            xlsx: XlsxIterator,
            csv: CsvIterator
    ]

    public static final WRITERS = [
            sql: DataSourceIteratorWriter,
            table: TableIteratorWriter,
            entity: EntityIteratorWriter,
            split: SplitIteratorWriter
    ]

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

    @SuppressWarnings("GrMethodMayBeStatic")
    static WrappedIterator createIterator(LinkedHashMap properties, Class<RowIterator> iteratorClass) {
        def iterator = iteratorClass.newInstance(properties)
        new WrappedIterator(iterator: iterator)
    }

    static WrappedIterator createIterator(LinkedHashMap properties, String name) {
        createIterator(properties, ITERATORS[name] as Class<RowIterator>)
    }

    static IteratorWriter createWriter(LinkedHashMap properties, String name) {
        createWriter(properties, WRITERS[name] as Class<IteratorWriter>)
    }

    static IteratorWriter createWriter(LinkedHashMap properties, Class<IteratorWriter> writerClass) {
        writerClass.newInstance(properties)
    }
}

class WrappedIterator {
    @Delegate
    RowIterator iterator

    WriteResponse writeTo(LinkedHashMap properties, Class<IteratorWriter> writerClass) {
        def writer = Iterators.createWriter(properties, writerClass)
        writer.write(iterator)
    }

    WriteResponse writeTo(LinkedHashMap properties, String name) {
        def writer = Iterators.createWriter(properties, name)
        writer.write(iterator)
    }

    WriteResponse writeTo(String name) {
        writeTo([:], name)
    }
}

