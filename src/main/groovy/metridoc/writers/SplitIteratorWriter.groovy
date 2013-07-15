package metridoc.writers

import metridoc.iterators.QueueBasedRecordIterator
import metridoc.iterators.QueueRecordIteratorManager
import metridoc.iterators.Record
import metridoc.iterators.RecordIterator

import javax.naming.OperationNotSupportedException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static metridoc.writers.WrittenRecordStat.Status.*

/**
 * Created with IntelliJ IDEA on 7/5/13
 * @author Tommy Barker
 */
class SplitIteratorWriter extends DefaultIteratorWriter {

    List<DefaultIteratorWriter> writers = []

    int threads = 10

    @Override
    WriteResponse write(RecordIterator recordIterator) {
        assert recordIterator: "record iterator should not be null or empty"
        def service = Executors.newFixedThreadPool(threads)
        List<QueueBasedRecordIterator> queueRowIterators = []
        List<Future<WriteResponse>> futures = []
        def iteratorManager = new QueueRecordIteratorManager(wrappedIterator: recordIterator)
        writers.each { writer ->
            def queueRowIterator = iteratorManager.createChild()
            queueRowIterators << queueRowIterator

            futures << service.submit({
                writer.write(queueRowIterator)
            } as Callable)
        }

        //noinspection GroovyAssignabilityCheck
        iteratorManager.each {}

        def splitResponse = new SplitWriteResponse()
        futures.each {
            future ->
                def response = future.get()
                splitResponse.addResponse(response)
        }

        return splitResponse
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
        if (writeResponse.fatalErrors) {
            this.fatalErrors.addAll(writeResponse.fatalErrors)
        }
    }
}