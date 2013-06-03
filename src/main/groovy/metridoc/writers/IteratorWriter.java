package metridoc.writers;

import metridoc.iterators.RowIterator;

public interface IteratorWriter<T> {
    T write(RowIterator rowIterator);
}
