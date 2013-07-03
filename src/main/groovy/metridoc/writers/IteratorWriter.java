package metridoc.writers;

import java.util.Iterator;

public interface IteratorWriter<T extends Iterator> {
    /**
     * should be a one time operation
     *
     * @param iterator to write
     * @return itself to promote method chaining
     */
    WriteResponseTotals write(T iterator);
}
