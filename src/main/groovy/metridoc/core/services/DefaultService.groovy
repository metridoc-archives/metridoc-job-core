package metridoc.core.services

import metridoc.core.MetridocScript

/**
 * @author Tommy Barker
 */
class DefaultService implements Service {
    boolean mergeMetridocConfig = true
    Binding binding = new Binding()

    @SuppressWarnings("GroovyAssignabilityCheck")
    void setBinding(Binding binding) {
        this.binding = binding
        if (binding.hasVariable("args")) {
            def argsMap = ParseArgsService.parseCli(binding.args)
            if (argsMap.containsKey("mergeMetridocConfig")) {
                mergeMetridocConfig = Boolean.valueOf(argsMap.mergeMetridocConfig)
            }

        }
        if (!(this instanceof ConfigService)) {
            use(MetridocScript) {
                binding.includeTool(ConfigService)
            }
        }
    }

    def getVariable(String variableName) {
        getVariable(variableName, null)
    }

    public <T> T getVariable(String variableName, Class<T> expectedType) {
        //command line goes first
        def value = null
        if (getBinding()) {
            if (getBinding().hasVariable("argsMap")) {
                value = getVariableHelper(getBinding().argsMap, variableName, expectedType)
            }
            if (value != null) return value

            value = getVariableHelper(getBinding().variables, variableName, expectedType)
            if (value != null) return value

            if (getBinding().hasVariable("config")) {
                value = getVariableHelper(getBinding().config, variableName, expectedType)
            }
        }

        return value
    }

    @SuppressWarnings(["GroovyAssignabilityCheck", "GroovyUnusedCatchParameter"])
    private static <T> T getVariableHelper(config, String variableName, Class<T> expectedType) {
        Map usedConfig = convertConfig(config)
        def value = usedConfig[variableName]

        if (value instanceof Map && value.isEmpty()) {
            return null
        }

        if (expectedType == null) {
            return value
        }
        else {
            if (value != null) {
                try {
                    return value.asType(expectedType)
                }
                catch (Throwable throwable) {
                    //do nothing, not compatible
                }
            }
        }

        return null
    }

    private static Map convertConfig(config) {
        if (config instanceof ConfigObject) {
            return config.flatten()
        }
        if (config instanceof Map) {
            return config
        }
        if (config instanceof Binding) {
            return config.variables
        }

        return [:]
    }

    /**
     * @deprecated
     * @param targetInfo
     * @param closure
     */
    void target(LinkedHashMap targetInfo, Closure closure) {
        step(targetInfo, closure)
    }

    void step(LinkedHashMap stepInfo, Closure closure) {
        use(MetridocScript) {
            getBinding().step(stepInfo, closure)
        }
    }

    void step(LinkedHashMap stepInfo) {
        use(MetridocScript) {
            def stepName = MetridocScript.getStepName(stepInfo)
            if(this.metaClass.respondsTo(this, stepName)) {
                getBinding().step(stepInfo, this.&"$stepName")
            }
            else {
                getBinding().step(stepInfo)
            }
        }
    }

    public <T> T includeService(Class<T> serviceClass) {
        use(MetridocScript) {
            return getBinding().includeService(serviceClass)
        }
    }

    /**
     * @deprecated
     * @param tool
     * @return
     */
    public <T> T includeTool(Class<T> tool) {
        includeService(tool)
    }

    public <T> T includeService(LinkedHashMap args, Class<T> serviceClass) {
        use(MetridocScript) {
            return getBinding().includeService(args, serviceClass)
        }
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

    /**
     * @deprecated
     * @param targets
     */
    void includeTargets(Class<Script> targets) {
        includeSteps(targets)
    }

    void includeSteps(Class<Script> steps) {
        use(MetridocScript) {
            return getBinding().includeSteps(steps)
        }
    }

    void depends(String... targetNames) {
        use(MetridocScript) {
            return getBinding().depends(targetNames)
        }
    }

    void profile(String description, Closure work) {
        use(MetridocScript) {
            return getBinding().profile(description, work)
        }
    }
}
