package metridoc.writers

import metridoc.iterators.Record

/**
 * Created with IntelliJ IDEA on 7/3/13
 * @author Tommy Barker
 */
class WrittenRecordStat {
    public enum Status {
        IGNORED, INVALID, WRITTEN, ERROR
    }
    Record record
    Status status
    Throwable throwable
    Class scope
    int line
}
