package metridoc.core

import groovy.util.logging.Slf4j

/**
 *
 */
@Slf4j
class TargetManager {
    static final String DEFAULT_TARGET = "default"
    String defaultTarget = DEFAULT_TARGET
    Map<String, Closure> targetMap = [:]
    Set<String> targetsRan = []
    private boolean interrupted = false
    private Binding _binding

    Binding getBinding() {
        if (_binding) return _binding

        _binding = new Binding()
        _binding.targetManager = this
        return _binding
    }
/**
     * If job is not run from the command line, use this to fire off an interuption.  This is not as
     * effective as killing a commandline job though.  Basically either the job will have to be aware of
     * the interuption or wait until it is checked in a progress closure
     * @return
     */
    @Override
    void interrupt() {
        interrupted = true
    }

    def target(Map data, Closure closure) {
        closure.delegate = this //required for imported targets
        Assert.isTrue(data.size() == 1, "the map in target can only have one variable, which is the name and the description of the target")
        def key = (data.keySet() as List<String>)[0]
        String description = data[key]
        def closureToRun = {
            profile(description, closure)
        }
        targetMap.put(key, closureToRun)
    }

    /**
     * fires off a target by name if it has not been run yet.  If it has run then it is skipped
     *
     * @param targetNames
     * @return
     */
    def depends(String... targetNames) {
        targetNames.each { targetName ->
            Closure target = targetMap.get(targetName)
            Assert.isTrue(target != null, "target $targetName does not exist")

            def targetHasNotBeenCalled = !targetsRan.contains(targetName)
            if (targetHasNotBeenCalled) {
                target.delegate = this
                target.resolveStrategy = Closure.DELEGATE_FIRST
                target.call()
                targetsRan.add(targetName)
            }
        }
    }

    /**
     * loads scripts that contain targets to allow for code reuse
     *
     * @param scriptClass
     * @return returns the binding from the script in case global variables need to accessed
     */
    def includeTargets(Class<Script> scriptClass) {
        return includeTargets(scriptClass, binding)
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

        binding.setVariable("target") { Map description, Closure closure ->
            target(description, closure)
        }
        Script script = scriptClass.newInstance()
        script.binding = binding
        script.run()

        return binding
    }

    /**
     * includes a raw target map.  Especially useful if you want to include info from another job
     *
     * @param targetMap
     */
    def includeTargets(Map<String, Closure> targetMap) {
        targetMap.putAll(targetMap)
    }

    /**
     * imports binding variables from another binding
     *
     * @param binding
     */
    def importBindingVariables(Binding binding) {
        binding.variables.putAll(binding.variables)
    }

    /**
     * profiles a chunk of code stating when it starts and finishes
     * @param description description of the chunk of code
     * @param closure the code to run
     */
    def profile(String description, Closure closure) {
        if (interrupted) {
            throw new JobInterruptionException(this.getClass().name)
        }
        def start = System.currentTimeMillis()
        log.info "profiling [$description] start"
        closure.call()
        def end = System.currentTimeMillis()
        log.info "profiling [$description] finished ${end - start} ms"
        if (interrupted) {
            throw new JobInterruptionException(this.getClass().name)
        }
    }

    def runDefaultTarget() {
        depends(defaultTarget)
    }
}
