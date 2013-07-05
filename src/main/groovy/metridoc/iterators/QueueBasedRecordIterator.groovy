package metridoc.iterators

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Created with IntelliJ IDEA on 7/9/13
 * @author Tommy Barker
 */
class QueueBasedRecordIterator extends RecordIterator {

    public static final int DEFAULT_TIMEOUT = 1000 * 60
    BlockingQueue<QueueData> queue = new ArrayBlockingQueue<QueueData>(1)

    /**
     * a timeout for polling.  Generally record processing should be quick, if the timeout is met
     * an exception should occur.  If timeout is less than 1 then it will poll forever.  Default
     * timeout is 1 minute
     */
    long timeout = DEFAULT_TIMEOUT

    @Override
    protected Record computeNext() {

        QueueData data
        if (timeout > 0) {
            data = queue.poll(timeout, TimeUnit.MILLISECONDS)
        } else {
            data = queue.poll()
        }

        if (data == null) {
            throw new TimeoutException("The QueueBasedRecordIterator has not processed a record within $timeout milliseconds")
        }

        if (data.done) {
            return endOfData()
        }

        return data.row.clone() as Record
    }
}

class QueueData {
    /**
     * indicates that all data has been processed.  When done is true record should be null
     */
    boolean done = false
    /**
     * the data that should be sent to next
     */
    Record row

    void setRecord(Record row) {
        this.row = row.clone() as Record
    }
}
