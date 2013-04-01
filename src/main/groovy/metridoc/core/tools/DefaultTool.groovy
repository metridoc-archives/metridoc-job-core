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
    Binding binding = new Binding()

    void setBinding(Binding binding) {
        MetridocScript.includeTool(binding, ConfigTool)
        this.binding = binding
    }

    def getVariable(String variableName) {
        getVariable(variableName, null)
    }

    public <T> T getVariable(String variableName, Class<T> expectedType) {
        //command line goes first
        def value
        if (binding) {
            if (binding.hasVariable("argsMap")) {
                value = getVariableHelper(binding.argsMap, variableName, expectedType)
            }
            if (value != null) return value

            value = getVariableHelper(binding.variables, variableName, expectedType)
            if (value != null) return value

            if (binding.hasVariable("config")) {
                value = getVariableHelper(binding.config, variableName, expectedType)
            }
        }

        return value
    }

    @SuppressWarnings(["GroovyAssignabilityCheck", "GroovyUnusedCatchParameter"])
    private static <T> T getVariableHelper(config, String variableName, Class<T> expectedType) {
        Map usedConfig = convertConfig(config)
        def value = usedConfig[variableName]
        if (expectedType == null) {
            return value
        } else {
            if (value != null) {
                try {
                    return value.asType(expectedType)
                } catch (Throwable throwable) {
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
}
