package metridoc.writers

import metridoc.entities.MetridocRecordEntity
import metridoc.iterators.Record
import metridoc.iterators.RecordIterator
import org.hibernate.SessionFactory

/**
 * Created with IntelliJ IDEA on 7/2/13
 * @author Tommy Barker
 * @deprecated
 */
class EntityIteratorWriter extends DefaultIteratorWriter {

    SessionFactory sessionFactory
    Class<? extends MetridocRecordEntity> recordEntityClass

    @Override
    WriteResponse write(RecordIterator recordIterator) {
        def session = sessionFactory.currentSession
        def transaction = session.beginTransaction()
        def response
        try {
            response = super.write(recordIterator)
            if (response.errorTotal) {
                throw response.fatalErrors[0]
            }
            transaction.commit()
            return response
        }
        catch (Throwable e) {
            transaction.rollback()
            response = new WriteResponse()
            response.addError(e)

            return response
        }
        finally {
            if (session.isOpen()) {
                session.close()
            }
        }
    }

    @Override
    boolean doWrite(int lineNumber, Record record) {
        def instance = recordEntityClass.newInstance()
        record.headers.sessionFactory = sessionFactory
        if (instance.acceptRecord(record)) {
            instance.populate(record)
            if (instance.shouldSave()) {
                sessionFactory.currentSession.save(instance)
                return true
            }
        }

        return false
    }
}
