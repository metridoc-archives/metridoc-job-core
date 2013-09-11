package metridoc.iterators

import groovy.stream.Stream
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

    static WrappedIterator toRecordIterator(Iterator iterator) {
        def iteratorToWrap = toRowIterator(iterator)
        new WrappedIterator(iterator: iteratorToWrap)
    }

    /**
     * please use {@link Iterators#toRowIterator(java.util.Iterator)}  instead
     *
     * @deprecated
     * @param iterator
     * @return
     */
    static RecordIterator toRowIterator(Iterator iterator) {
        assert iterator: "iterator must not be null or empty"
        new RecordIterator() {
            @Override
            protected Record computeNext() {
                if (iterator.hasNext()) {
                    def next = iterator.next()
                    if (next instanceof Record) {
                        return next
                    }
                    assert next instanceof Map: "$next is neither a Record nor a Map"
                    return new Record(body: next)
                }

                return endOfData()
            }
        }
    }

    static WrappedIterator toRecordIterator(List<Map> iterator) {
        new WrappedIterator(iterator: toRowIterator(iterator))
    }

    /**
     * Please use {@link Iterators#toRecordIterator(java.util.List)}
     *
     * @deprecated
     * @param iterator
     * @return
     */
    static RecordIterator toRowIterator(List<Map> iterator) {
        assert iterator: "iterator must not be null or empty"
        toRowIterator(iterator.iterator())
    }

    /**
     * @deprecated
     * @param iterator
     * @param filter
     * @return
     */
    static RecordIterator toFilteredRowIterator(RecordIterator iterator, Closure<Boolean> filter) {
        assert iterator: "iterator must not be null"
        assert filter: "filter must not be null"

        new FilteredRecordIterator(
                filter: filter,
                iterator: iterator
        )
    }

    /**
     * @deprecated
     * @param rowIterator
     * @param transformer should return null if it should not be collected, otherwise should return a Map
     * @return
     */
    static RecordIterator toFilteredAndTransformedIterator(RecordIterator rowIterator, Closure<Record> transformer) {
        new FilteredAndTransformedRecordIterator(
                iterator: rowIterator,
                transformer: transformer
        )
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    static WrappedIterator createIterator(LinkedHashMap properties, Class<RecordIterator> iteratorClass) {
        def iterator = iteratorClass.newInstance(properties)
        new WrappedIterator(iterator: iterator)
    }

    static WrappedIterator createIterator(LinkedHashMap properties, String name) {
        createIterator(properties, ITERATORS[name] as Class<RecordIterator>)
    }

    static IteratorWriter createWriter(LinkedHashMap properties, String name) {
        createWriter(properties, WRITERS[name] as Class<IteratorWriter>)
    }

    static IteratorWriter createWriter(LinkedHashMap properties, Class<IteratorWriter> writerClass) {
        writerClass.newInstance(properties)
    }

    static IteratorWriter createWriter(String name) {
        createWriter([:], WRITERS[name] as Class<IteratorWriter>)
    }
}

class WrappedIterator {
    @Delegate
    RecordIterator iterator

    WrappedIterator filter(Closure closure) {
        Iterators.toRecordIterator(Stream.from(iterator).filter(closure))
    }

    WrappedIterator map(Closure closure) {
        Iterators.toRecordIterator(Stream.from(iterator).map { Record record ->
            closure.call(record)
            return record
        })
    }

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

