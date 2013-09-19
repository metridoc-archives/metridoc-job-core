package metridoc.core

import metridoc.core.tools.ParseArgsTool
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory

import java.lang.reflect.Field

class TargetManager {
    static final String DEFAULT_TARGET = "default"
    String defaultTarget = DEFAULT_TARGET
    Map<String, Closure> targetMap = [:]
    Set<String> targetsRan = []
    private boolean _interrupted = false
    private Binding _binding

    Binding getBinding() {
        if (_binding) return _binding

        _binding = new Binding()
        _binding.targetManager = this
        return _binding
    }

    void setBinding(Binding _binding) {
        this._binding = _binding
    }

    /**
     * If job is not run from the command line, use this to fire off an interuption.  This is not as
     * effective as killing a commandline job though.  Basically either the job will have to be aware of
     * the interuption or wait until it is checked in a progress closure
     * @return
     */
    void interrupt() {
        interrupted = true
        getBinding().interrupted = true
    }

    boolean getInterrupted() {
        def bindingInterrupted = binding.hasVariable("interrupted") ? binding.interrupted : false
        return _interrupted || bindingInterrupted
    }

    void setInterrupted(boolean interrupted) {
        this._interrupted = interrupted
    }

    def target(Map data, Closure closure) {
        closure.delegate = this //required for imported targets
        assert data.size() == 1: "the map in target can only have one variable, which is the name and the description of the target"
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
    @SuppressWarnings("UnnecessaryQualifiedReference")
    def depends(String... targetNames) {
        targetNames.each { targetName ->
            Closure target = targetMap.get(targetName)
            assert target != null: "target $targetName does not exist"

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
    def includeTargets(Class<? extends Script> scriptClass) {
        return includeTargets(scriptClass, binding)
    }

    /**
     * the same as {@link #includeTargets(Class)}, but a binding can be passed so more global variables can
     * be loaded
     *
     * @param scriptClass
     * @param binding
     * @return the passed binding
     */
    def includeTargets(Class<? extends Script> scriptClass, Binding binding) {

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
        this.targetMap.putAll(targetMap)
    }

    /**
     * imports binding variables from another binding
     *
     * @param binding
     */
    def importBindingVariables(Binding binding) {
        this.binding.variables.putAll(binding.variables)
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
        def log = LoggerFactory.getLogger(TargetManager)
        def start = System.currentTimeMillis()
        log.info "profiling [$description] start"
        closure.call()
        def end = System.currentTimeMillis()
        log.info "profiling [$description] finished ${end - start} ms"
        if (interrupted) {
            throw new JobInterruptionException(this.getClass().name)
        }
    }

    def <T> T includeService(Class<T> serviceClass) {
        includeService([:] as LinkedHashMap, serviceClass)
    }

    /**
     * @deprecated
     * @param tool
     * @return
     */
    public <T> T includeTool(Class<T> tool) {
        includeService([:] as LinkedHashMap, tool)
    }

    def <T> T includeService(LinkedHashMap args, Class<T> serviceClass) {
        def serviceName = serviceClass.simpleName
        def serviceNameUsed = StringUtils.uncapitalize(serviceName)
        if (binding.hasVariable(serviceNameUsed)) {
            def log = LoggerFactory.getLogger(TargetManager)
            log.debug "service $serviceNameUsed already exists"
        }
        else {
            def service = createService(args, serviceClass)
            if (!binding.hasVariable(serviceNameUsed)) {
                binding."$serviceNameUsed" = service
            }
        }
        return binding."${serviceNameUsed}"
    }

    def <T> T createService(LinkedHashMap args, Class<T> serviceClass) {
        def instance = serviceClass.newInstance(args)
        if (instance.metaClass.respondsTo(instance, "setBinding")) {
            instance.binding = binding
        }
        handlePropertyInjection(instance)
        if (instance.metaClass.respondsTo(instance, "init")) {
            instance.init()
        }

        return instance
    }

    /**
     * @deprecated
     * @param args
     * @param tool
     * @return
     */
    public <T> T includeTool(LinkedHashMap args, Class<T> tool) {
        includeService(args, tool)
    }

    protected void handlePropertyInjection(instance) {
        InjectArgBase injectArgBase = instance.getClass().getAnnotation(InjectArgBase)
        instance.properties.each { String key, value ->
            InjectArg injectArg
            try {
                def field = getField(instance, key)
                if (field) {
                    injectArg = field.getAnnotation(InjectArg)
                }
                else {
                    return
                }
            }
            catch (NoSuchFieldException ignored) {
                //ignore... handles issues when searching for field "class" for instance
                return
            }

            boolean ignoreInjection = injectArg ? injectArg.ignore() : false

            if (ignoreInjection) return
            if (injectWithCli(instance, key, injectArg)) return
            if (injectWithConfig(instance, key, injectArg, injectArgBase)) return

            injectWithBinding(instance, key, injectArg)
        }
    }

    protected void injectWithBinding(def instance, String fieldName, InjectArg injectArg) {
        boolean injectByName = injectArg ? injectArg.injectByName() : true
        if (injectByName) {
            if (binding.hasVariable(fieldName)) {
                setValueOnInstance(instance, fieldName, binding."$fieldName")
            }
        }
    }

    protected boolean injectWithConfig(def instance, String fieldName, InjectArg injectArg,
                                       InjectArgBase injectArgBase) {
        def configObject = binding.variables.config
        def usedName = injectArg ? injectArg.injectByName() ? fieldName : null : fieldName
        def key = injectArg ? injectArg.config() ?: usedName : usedName
        def prefix
        if (injectArgBase) {
            prefix = injectArgBase.value()
        }
        if (!key.contains(".") && prefix) {
            key = "$prefix.$key"
        }

        if (configObject instanceof ConfigObject) {
            def flattened = configObject.flatten()
            def containsKey = flattened.containsKey(key as String)
            if (containsKey) {
                return setValueOnInstance(instance, fieldName, flattened[key])
            }
        }

        return false
    }

    protected boolean injectWithCli(instance, String fieldName, InjectArg injectArg) {
        def argsMap = binding.variables.argsMap
        def usedName = injectArg ? injectArg.injectByName() ? fieldName : null : fieldName
        def key = injectArg ? injectArg.cli() ?: usedName : usedName
        if (argsMap instanceof Map) {
            if (argsMap.containsKey(key)) {
                return setValueOnInstance(instance, fieldName, argsMap[key])
            }
        }

        return false
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected boolean setValueOnInstance(instance, String fieldName, value) {
        try {
            def field = getField(instance, fieldName)
            def type = field.type

            def isBoolean = type instanceof Boolean || type.name == "boolean"
            if (isBoolean) {
                instance."$fieldName" = Boolean.valueOf(value as String)
            }
            else {
                instance."$fieldName" = value.asType(type)
            }
            return true
        }
        catch (Throwable ignored) {
            //ignore, probably a casting issue
        }

        return false
    }

    def runDefaultTarget() {
        includeService(ParseArgsTool)
        if (binding.hasVariable("argsMap")) {
            Map argsMap = binding.argsMap
            defaultTarget = argsMap.target ?: defaultTarget
        }
        depends(defaultTarget)
    }

    protected static Field getField(instance, String fieldName) {
        def clazz = instance.getClass()
        def field = null

        while (clazz && field == null) {
            try {
                field = clazz.getDeclaredField(fieldName)
            }
            catch (NoSuchFieldException ignored) {
                clazz = clazz.superclass
            }
        }

        return field
    }


}
