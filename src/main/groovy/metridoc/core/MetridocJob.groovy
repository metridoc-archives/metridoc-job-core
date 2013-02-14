package metridoc.core

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/13/13
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class MetridocJob implements Job {

    static final String DEFAULT_TARGET = "default"
    String defaultTarget = DEFAULT_TARGET
    def Map<String, Closure> targetMap = Collections.synchronizedMap([:])
    private Set _targetsRan = [] as Set
    private boolean interrupt = false

    /**
     * suggested default trigger for a scheduler to use.  Especially useful to trigger a job out of the
     * box without requiring input from the user.  Defaults to never
     */
    Trigger defaultTrigger = Trigger.NEVER

    @Override
    def execute(Map<String, Object> config) {
        create(config).doExecute()
    }

    /**
     * If job is not run from the command line, use this to fire off an interuption.  This is not as
     * effective as killing a commandline job though.  Basically either the job will have to be aware of
     * the interuption or wait until it is checked in a progress closure
     * @return
     */
    @Override
    void interrupt() {
        interrupt = true
    }

    /**
     * called first thing before execute.  This ensures that a new object is called per run since each
     * job instance is stateful.  Either the config can auto fill properties, or the implementing job can
     * have a
     * constructor that takes a {@link Map}
     *
     * @param config
     * @return
     */
    protected MetridocJob create(Map<String, Object> config) {
        this.getClass().newInstance(config)
    }

    protected abstract doExecute()
}
