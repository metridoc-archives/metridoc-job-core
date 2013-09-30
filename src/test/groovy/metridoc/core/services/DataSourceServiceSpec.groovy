package metridoc.core.services

import org.hibernate.SessionFactory
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 9/30/13
 * @author Tommy Barker
 */
class DataSourceServiceSpec extends Specification {

    void "test that [enableFor] can only be called once"() {
        when:
        def mock = new DataSourceServiceMock()
        mock.enableFor(String)

        then:
        noExceptionThrown()

        when:
        mock.enableFor(String)

        then:
        thrown(IllegalStateException)
    }

    class DataSourceServiceMock extends DataSourceService{

        @Override
        protected void doEnableFor(Class... classes) {
            //do nothing
        }

        @Override
        SessionFactory getSessionFactory() {
            //do nothing
        }
    }
}

