package metridoc.core.tools

import metridoc.core.MetridocScript

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/13/13
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class DefaultTool implements Tool {
    boolean mergeMetridocConfig = true
    Binding binding = new Binding()

    @SuppressWarnings("GroovyAssignabilityCheck")
    void setBinding(Binding binding) {
        this.binding = binding
        if (!(this instanceof ConfigTool)) {
            if (binding.hasVariable("args")) {
                def argsMap = ParseArgsTool.parseCli(binding.args)
                if (argsMap.containsKey("mergeMetridocConfig")) {
                    mergeMetridocConfig = Boolean.valueOf(argsMap.mergeMetridocConfig)
                }

            }
            use(MetridocScript) {
                binding.includeTool(mergeMetridocConfig: mergeMetridocConfig, ConfigTool)
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

    void target(LinkedHashMap targetInfo, Closure closure) {
        use(MetridocScript) {
            getBinding().target(targetInfo, closure)
        }
    }

    public <T> T includeTool(Class<T> tool) {
        use(MetridocScript) {
            return getBinding().includeTool(tool)
        }
    }

    public <T> T includeTool(LinkedHashMap args, Class<T> tool) {
        use(MetridocScript) {
            return getBinding().includeTool(args, tool)
        }
    }

    void includeTargets(Class<Script> targets) {
        use(MetridocScript) {
            return getBinding().includeTargets(targets)
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
