package metridoc.iterators

import com.google.common.collect.AbstractIterator

/**
 * Created with IntelliJ IDEA on 5/29/13
 * @author Tommy Barker
 * @deprecated
 *
 * The class that all {@link Iterator} should extend
 */
abstract class RecordIterator extends AbstractIterator<Record> {

    /**
     * persisted in each record.  Can be set in the beginning or changed through the iteration
     */
    final Map<String, Object> recordHeaders = [:]
}
