package metridoc.core.tools

import metridoc.core.MetridocScript

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/21/13
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ConfigTool extends DefaultTool {

    Binding binding = new Binding()

    void setBinding(Binding binding) {
        this.binding = binding
        if (!binding.hasVariable("config")) {
            use(MetridocScript) {
                binding.includeTool(ParseArgsTool)
            }

            String env = getVariable("env", String)

            def configSlurper = env ? new ConfigSlurper(env) : new ConfigSlurper()
            File cliConfigLocation = getVariable("config", File)
            def cliConfigObject = new ConfigObject()
            if (cliConfigLocation) {
                cliConfigObject = configureFromFile(cliConfigLocation, configSlurper)
            }

            binding.config = cliConfigObject
        }
    }

    private static ConfigObject configureFromFile(File file, ConfigSlurper slurper) {
        if (file.exists()) {
            return slurper.parse(file.toURI().toURL())
        }

        return null
    }

    ConfigTool addConfig(Closure closure) {
        ConfigObject config = binding.variables.config ?: new ConfigObject()
        binding.config = config
        def slurper = new ConfigSlurper()
        config.merge(slurper.parse(new ConfigScript(configClosure: closure)))

        return this
    }

    ConfigTool addConfig(File file) {
        ConfigObject config = binding.variables.config ?: new ConfigObject()
        binding.config = config
        def slurper = new ConfigSlurper()
        config.merge(slurper.parse(file.toURI().toURL()))

        return this
    }
}

class ConfigScript extends Script {
    Closure configClosure

    @Override
    def run() {
        Closure clone = configClosure.clone() as Closure
        clone.delegate = this
        clone.run()
    }
}