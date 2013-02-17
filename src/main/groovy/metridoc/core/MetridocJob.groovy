package metridoc.core

import metridoc.camel.CamelScript
import metridoc.camel.CamelScriptRegistry
import org.apache.camel.builder.RouteBuilder

/**
 * This is a convenience class to create a non script based job, like in grails or a java application
 */
abstract class MetridocJob implements Job {

    TargetManager targetManager = new TargetManager()

    /**
     * suggested default trigger for a scheduler to use.  Especially useful to trigger a job out of the
     * box without requiring input from the user.  Defaults to never
     */
    Trigger defaultTrigger = Trigger.NEVER

    Map<String, Closure> getTargetMap() {
        return targetManager.targetMap
    }

    @Override
    def execute() {
        execute([:])
    }

    void setDefaultTarget(String defaultTarget) {
        targetManager.defaultTarget = defaultTarget
    }

    String getDefaultTarget() {
        targetManager.defaultTarget
    }

    @Override
    def execute(Map<String, Object> config) {
        def job = create(config)
        job.defaultTarget = defaultTarget
        job.targetManager = targetManager
        job.binding.setVariable("job", job)
        if (defaultTarget) {
            def defaultTargetIsDefined = targetMap.containsKey(defaultTarget)
            if(defaultTargetIsDefined) {
                job.depends(defaultTarget)
            } else {
                Assert.isTrue(defaultTarget == "default", "target $defaultTarget does not exist")
            }
        }
        doExecute()
    }

    /**
     * If job is not run from the command line, use this to fire off an interuption.  This is not as
     * effective as killing a commandline job though.  Basically either the job will have to be aware of
     * the interuption or wait until it is checked in a progress closure
     * @return
     */
    @Override
    void interrupt() {
        targetManager.interrupt()
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

    def target(Map data, Closure closure) {
        targetManager.target(data, closure)
    }

    /**
     * fires off a target by name if it has not been run yet.  If it has run then it is skipped
     *
     * @param targetNames
     * @return
     */
    def depends(String... targetNames) {
        targetManager.depends(targetNames)
    }

    /**
     *
     * @return the binding that is passed to all imported targets
     */
    Binding getBinding() {
        targetManager.binding
    }

    /**
     * This is where all the work is done in an implementing class
     *
     * @return
     */
    protected abstract doExecute()

    /**
     * loads scripts that contain targets to allow for code reuse
     *
     * @param scriptClass
     * @return returns the binding from the script in case global variables need to accessed
     */
    def includeTargets(Class<Script> scriptClass) {
        return targetManager.includeTargets(scriptClass, binding)
    }

    /**
     * the same as {@link #includeTargets(java.lang.Class)}, but a binding can be passed so more global variables can
     * be loaded
     *
     * @param scriptClass
     * @param binding
     * @return the passed binding
     */
    def includeTargets(Class<Script> scriptClass, Binding binding) {
        return targetManager.includeTargets(scriptClass, binding)
    }

    /**
     * includes a raw target map.  Especially useful if you want to include info from another job
     *
     * @param targetMap
     */
    def includeTargets(Map<String, Closure> targetMap) {
        targetManager.includeTargets(targetMap)
    }

    /**
     * imports binding variables from another binding
     *
     * @param binding
     */
    def importBindingVariables(Binding binding) {
        targetManager.importBindingVariables(binding)
    }

    /**
     * profiles a chunk of code stating when it starts and finishes
     * @param description description of the chunk of code
     * @param closure the code to run
     */
    def profile(String description, Closure closure) {
        targetManager.profile(description, closure)
    }

    Set getTargetsRan() {
        targetManager.targetsRan
    }

    /**
     * runs the closure as a route within the {@link metridoc.camel.GroovyRouteBuilder}.  Really handy for database migrations and
     * batch processing
     *
     * @param closure
     * @return
     */
    def runRoute(Closure closure) {
        closure.delegate = this
        CamelScript.runRoute(closure)
    }

    /**
     * instead of using runRoute with a closure, you can pass a RouteBuilder instead.  This can be convenient if someone
     * wants to use their own {@link org.apache.camel.builder.RouteBuilder} or wants to benefit from code completion
     *
     * @param builder
     */
    def runRoute(RouteBuilder builder) {
        def mockClosure = {}
        mockClosure.delegate = builder
        def registry = new CamelScriptRegistry(closure: mockClosure)
        CamelScript.runRouteBuilders(registry, builder)
    }

    def executeTarget(String target) {
        if (targetMap.isEmpty()) {
            execute()
        }
        depends(target)
    }
}
