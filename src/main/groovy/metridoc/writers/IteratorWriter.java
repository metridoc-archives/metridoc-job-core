package metridoc.writers;

import java.util.Iterator;

public interface IteratorWriter<T extends Iterator> {
    /**
     * Should iterate over the iterator and write all the records
     *
     * @param iterator to write
     * @return itself to promote method chaining
     */
    WriteResponse write(T iterator);
}
