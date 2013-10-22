package metridoc.stream
/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class StreamResponse {
    private int ignored = 0
    private int written = 0
    private int invalid = 0

    void incrementWritten() {
        written++
    }

    void incrementIgnored() {
        ignored++
    }

    void incrementInvalid() {
        invalid++
    }

    void decrementWritten() {
        written--
    }

    void decrementIgnored() {
        ignored--
    }

    void decrementInvalid() {
        invalid--
    }

    int getIgnored() {
        return ignored
    }

    int getWritten() {
        return written
    }

    int getInvalid() {
        return invalid
    }

    int getTotal() {
        ignored + invalid + written
    }

    @Override
    public String toString() {
        return "StreamResponse{" +
                "ignored=" + ignored +
                ", written=" + written +
                ", invalid=" + invalid +
                ", total=" + total +
                '}';
    }
}
