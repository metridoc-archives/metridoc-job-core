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

    void setBinding(Binding binding) {
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
}
