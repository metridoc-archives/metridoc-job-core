package metridoc.writers

import metridoc.iterators.Record

/**
 * Created with IntelliJ IDEA on 7/3/13
 * @author Tommy Barker
 * @deprecated
 */
class WrittenRecordStat {
    public enum Status {
        IGNORED, INVALID, WRITTEN, ERROR, TOTAL
    }
    Record record
    Status status
    Throwable fatalError
    AssertionError validationError
    Class scope
    int line
}
