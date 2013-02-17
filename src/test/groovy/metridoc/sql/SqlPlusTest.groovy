package metridoc.sql

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 12/17/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
class SqlPlusTest {

    /**
     * there was a reference ot method that didn't exist, just running the private method to force a compilation error
     */
    @Test
    void "fixing the static reference to should log in sqlplus"() {
        SqlPlus.logBatch([1,2] as int[], true)
    }
}
