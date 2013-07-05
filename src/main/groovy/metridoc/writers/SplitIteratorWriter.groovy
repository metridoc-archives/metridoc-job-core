package metridoc.writers

import metridoc.iterators.QueueBasedRecordIterator
import metridoc.iterators.QueueData
import metridoc.iterators.Record
import metridoc.iterators.RecordIterator

import javax.naming.OperationNotSupportedException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static metridoc.writers.WrittenRecordStat.Status.*

/**
 * Created with IntelliJ IDEA on 7/5/13
 * @author Tommy Barker
 */
class SplitIteratorWriter extends DefaultIteratorWriter {

    public static final int DEFAULT_TIMEOUT = 3000
    List<DefaultIteratorWriter> writers = []

    int threads = 10
    long queueOfferTimeout = DEFAULT_TIMEOUT
    long queuePollTimeout = DEFAULT_TIMEOUT

    @Override
    WriteResponse write(RecordIterator recordIterator) {
        def service = Executors.newFixedThreadPool(threads)
        List<QueueBasedRecordIterator> queueRowIterators = []
        List<Future<WriteResponse>> futures = []
        writers.each { writer ->
            def queueRowIterator = new QueueBasedRecordIterator(timeout: queuePollTimeout)
            queueRowIterators << queueRowIterator

            futures << service.submit({
                writer.write(queueRowIterator)
            } as Callable)
        }

        recordIterator.each { record ->
            def qData = new QueueData(record: record)
            queueRowIterators.each { queueRowIterator ->
                offerData(queueRowIterator, qData, futures)
            }
        }

        queueRowIterators.each { queueRowIterator ->
            def qData = new QueueData(done: true)
            offerData(queueRowIterator, qData, futures)
        }

        def splitResponse = new SplitWriteResponse()
        futures.each { future ->
            def response = future.get()
            splitResponse.addResponse(response)
        }

        return splitResponse
    }

    protected void offerData(QueueBasedRecordIterator queueRowIterator, QueueData qData, List<Future<WriteResponse>> futures) {
        def dataAdded = queueRowIterator.queue.offer(qData, queueOfferTimeout, TimeUnit.MILLISECONDS)
        if (!dataAdded) {
            futures.each { it.get() }
            throw new IllegalStateException("queue could not take data, maybe the queueOfferTime needs to be increased")
        }
    }

    @Override
    boolean doWrite(int lineNumber, Record record) {
        //do nothing, everything handled in write above
        throw new OperationNotSupportedException("not supported")
    }
}

class SplitWriteResponse extends WriteResponse {

    @Delegate
    private List<WriteResponse> responses = []

    void addResponse(WriteResponse writeResponse) {
        def stats = writeResponse.aggregateStats
        aggregateStats[ERROR] += stats[ERROR]
        aggregateStats[IGNORED] += stats[IGNORED]
        aggregateStats[WRITTEN] += stats[WRITTEN]
        aggregateStats[INVALID] += stats[INVALID]
        responses.add(writeResponse)
    }
}