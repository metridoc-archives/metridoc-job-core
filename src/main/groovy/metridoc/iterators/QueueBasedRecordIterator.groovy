package metridoc.iterators

import groovy.transform.ToString

import java.util.concurrent.*

/**
 * Created with IntelliJ IDEA on 7/9/13
 * @author Tommy Barker
 */
class QueueBasedRecordIterator extends RecordIterator {

    //one second time limit for offer
    public static final long DEFAULT_TIMEOUT = 1000
    BlockingQueue<QueueData> queue = new ArrayBlockingQueue<QueueData>(1)
    long offerWait = DEFAULT_TIMEOUT

    @Override
    protected Record computeNext() {

        QueueData data
        try {
            data = queue.take()
            if (data.exception) {
                throw data.exception
            }

            if (data.done) {
                return endOfData()
            }

            return data.record.clone() as Record
        }
        finally {
            if (data) {
                def latch = data.latch
                if (latch) {
                    latch.countDown()
                }
            }
        }
    }

    void offer(QueueData queueData) {
        if (!queue.offer(queueData, offerWait, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException("Could not put $queueData in queue within time limit, Exception probably occurred during processing")
        }
    }
}

@ToString(includePackage = false, includeNames = true)
class QueueData {
    /**
     * indicates that all data has been processed.  When done is true record should be null
     */
    boolean done = false
    /**
     * the data that should be sent to next
     */
    Record record
    CountDownLatch latch
    /**
     * forces an exception to occur
     */
    Throwable exception

    void setRecord(Record record) {
        this.record = record.clone() as Record
    }
}
