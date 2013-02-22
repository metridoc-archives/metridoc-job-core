package metridoc.core.tools

import metridoc.core.MetridocScript
import org.apache.commons.lang.SystemUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/21/13
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ConfigTool {

    public static final String DEFAULT_METRIDOC_HOME = "${SystemUtils.USER_HOME}/.metridoc"
    public static final String METRIDOC_HOME = System.getProperty("metridoc.home", DEFAULT_METRIDOC_HOME)
    public static final String METRIDOC_CONFIG = "${METRIDOC_HOME}/MetridocConfig.groovy"

    public static final ENVIRONMENT_SHORTCUTS = [
            dev: "development",
            prod: "production",
    ]

    void setBinding(Binding binding) {
        use(MetridocScript) {
            binding.includeTool(ParseArgsTool)
        }
        def configSlurper = new ConfigSlurper()

        binding.with {
            String env = getVariableFromCli("env", String, binding)
            if (env) {
                String envUsed = ENVIRONMENT_SHORTCUTS[env] ?: env
                configSlurper = new ConfigSlurper(envUsed)
            }
        }

        def defaultConfig = new File(METRIDOC_CONFIG)
        def defaultConfigObject = configureFromFile(defaultConfig, configSlurper) ?: new ConfigObject()
        def cliConfigLocation = getVariableFromCli("config", String, binding)
        def cliConfigObject = new ConfigObject()
        if (cliConfigLocation) {
            cliConfigObject = configureFromFile(new File(cliConfigLocation), configSlurper)
        }
        defaultConfigObject.merge(cliConfigObject)
        binding.config = defaultConfigObject
    }

    private static ConfigObject configureFromFile(File file, ConfigSlurper slurper) {
        if (file.exists()) {
            return slurper.parse(file.toURI().toURL())
        }

        return null
    }

    private static getVariableFromCli(String cliVariable, Class type, Binding binding) {
        def result = null
        binding.with {
            if (hasVariable("argsMap") && argsMap instanceof Map) {
                def variable = getVariable("argsMap")[cliVariable]
                if (variable) {
                    if (type.isAssignableFrom(variable.class)) {
                        result = variable
                    }
                }
            }
        }

        return result
    }
}
