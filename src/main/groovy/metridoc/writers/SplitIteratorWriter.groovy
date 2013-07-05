package metridoc.writers

import javax.naming.OperationNotSupportedException

/**
 * Created with IntelliJ IDEA on 7/5/13
 * @author Tommy Barker
 */
class SplitIteratorWriter extends DefaultIteratorWriter {

    List<DefaultIteratorWriter> writers = []

    @Override
    protected List<WrittenRecordStat> writeRecord(Map record) {
        def response = []
        writers.each {
            response.addAll(it.writeRecord(record))
        }

        return response
    }

    @Override
    boolean doWrite(int lineNumber, Map record) {
        //do nothing, everything handled in write above
        throw new OperationNotSupportedException("not supported")
    }
}
