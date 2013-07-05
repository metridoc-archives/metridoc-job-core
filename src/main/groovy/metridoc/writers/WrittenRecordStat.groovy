package metridoc.writers

/**
 * Created with IntelliJ IDEA on 7/3/13
 * @author Tommy Barker
 */
class WrittenRecordStat {
    public enum Status {
        IGNORED, INVALID, WRITTEN, ERROR
    }
    Map<String, Object> record
    Status status
    Throwable throwable
    Class scope
}
