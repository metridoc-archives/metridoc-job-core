package metridoc.core.tools

import metridoc.iterators.*
import metridoc.writers.*

/**
 * Created with IntelliJ IDEA on 7/5/13
 * @author Tommy Barker
 */
class IteratorTool extends DefaultTool {

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

    @SuppressWarnings("GrMethodMayBeStatic")
    WrappedIterator createIterator(LinkedHashMap properties, Class<RowIterator> iteratorClass) {
        def iterator = iteratorClass.newInstance(properties)
        new WrappedIterator(iterator: iterator)
    }

    WrappedIterator createIterator(LinkedHashMap properties, String name) {
        createIterator(properties, ITERATORS[name] as Class<RowIterator>)
    }
}

class WrappedIterator {
    @Delegate
    RowIterator iterator

    WriteResponse writeTo(LinkedHashMap properties, Class<IteratorWriter> writerClass) {
        def writer = writerClass.newInstance(properties)
        writer.write(iterator)
    }

    WriteResponse writeTo(LinkedHashMap properties, String name) {
        writeTo([:], IteratorTool.WRITERS[name] as Class<IteratorWriter>)
    }

    WriteResponse writeTo(String name) {
        writeTo([:], name)
    }
}
