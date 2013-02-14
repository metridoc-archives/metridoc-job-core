package metridoc.core

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/13/13
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class MetridocJob implements Job {

    String defaultTarget = "default"
    def Map<String, Closure> targetMap = Collections.synchronizedMap([:])
    private Set _targetsRan = [] as Set
    boolean interrupt = false

    @Override
    def execute(Map<String, Object> config) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    def interrupt() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }



    protected abstract MetridocJob create(Map<String, Object> config)
    protected abstract doExecute()
}
