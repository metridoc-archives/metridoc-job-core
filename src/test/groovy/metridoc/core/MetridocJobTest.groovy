package metridoc.core

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/14/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
class MetridocJobTest {

    @Test
    void "make sure that default implementation of create works"() {
        def foo = [foo:"bar", bar: "bam"]
        def job = new JobWithMapConstructor().create(foo)
        assert 'bar' == new JobWithProperties().create(foo).foo
    }



}

class JobWithProperties extends MetridocJob {

    def foo
    def bar

    @Override
    protected doExecute() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}

class JobWithMapConstructor extends MetridocJob{
    Map _config

    JobWithMapConstructor(Map _config) {
        this._config = _config
    }

    @Override
    protected doExecute() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}