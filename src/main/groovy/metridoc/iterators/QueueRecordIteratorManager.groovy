package metridoc.iterators

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA on 7/15/13
 * @author Tommy Barker
 */
class QueueRecordIteratorManager extends RecordIterator {

    private final List<QueueBasedRecordIterator> children = []
    RecordIterator wrappedIterator

    @Override
    protected Record computeNext() {
        assert wrappedIterator != null: "wrappedIterator cannot be null"
        assert children: "there should be at least one child"

        def latch = new CountDownLatch(children.size())

        try {
            if (wrappedIterator.hasNext()) {
                def record = wrappedIterator.next()
                sendDataToChildren(latch: latch, record: record)
                return record
            }
            else {
                sendDataToChildren(latch: latch, done: true)
                return endOfData()
            }
        }
        catch (Throwable throwable) {
            sendDataToChildren(latch: latch, done: true)
            throw throwable
        }
    }

    private sendDataToChildren(LinkedHashMap data) {
        def queueData = new QueueData(data)
        children.each { child ->
            child.offer(queueData)
        }
        data.latch.await()
    }

    RecordIterator createChild() {
        def child = new QueueBasedRecordIterator()
        children << child

        return child
    }
}