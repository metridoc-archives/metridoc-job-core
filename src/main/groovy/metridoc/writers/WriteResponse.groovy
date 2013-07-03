package metridoc.writers

/**
 * Created with IntelliJ IDEA on 7/3/13
 * @author Tommy Barker
 */
class WriteResponse {
    public enum Type {
        IGNORED, INVALID, WRITTEN, ERROR
    }
    Map<String, Object> record
    Type type
    Throwable throwable
    Class<? extends IteratorWriter> scope
}
