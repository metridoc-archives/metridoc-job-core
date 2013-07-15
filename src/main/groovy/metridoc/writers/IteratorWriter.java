package metridoc.writers;

import java.util.Iterator;

public interface IteratorWriter<T extends Iterator> {
    /**
     * Should iterate over the iterator and write all the records.  With the exception of checking
     * if the iterator is null, this method should never return an exception, but instead be wrapped
     * in the {@link WriteResponse}.  If an error does occur, the writer has the option of discontinuing
     * writing.  {@link AssertionError} is considered an invalid record and the writer should continue
     * to write regardless.
     *
     * @param iterator to write
     * @return response with data related to record written success for failure
     */
    WriteResponse write(T iterator);
}
