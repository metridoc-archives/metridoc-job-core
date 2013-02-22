package metridoc.core.tools

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/21/13
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ConfigTool {
    void setBinding(Binding binding) {

        def args = binding.args
        if (args) {
            def keyValue = args.find{it.startsWith("-env=")}?.split("=")
            String environment
            if (keyValue && keyValue.size() == 2) {
                environment = keyValue[1]
            }

        }
        def configSlurper = new ConfigSlurper()
    }
}
