package metridoc.core.tools

import java.util.regex.Matcher

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/22/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
class ParseArgsTool {
    static final String ARGS = "args"
    static final KEY_VALUE = /^[\-]+([^=]+)=(.*)$/
    static final KEY_NO_VALUE = /^[\-]+([^=]+)$/
    static final ONLY_PARAM = /^([^\-]+)$/

    void setBinding(Binding binding) {
        if (binding.hasVariable(ARGS) && binding."$ARGS" instanceof String[]) {
            String[] args = binding."$ARGS"
            def argsMap = [:]
            binding.argsMap = argsMap
            args.each {
                Matcher m = it =~ KEY_VALUE
                if (m.matches()) {
                    def key = m.group(1)
                    def value = m.group(2)
                    argsMap."${key}" = value
                }
                m = it =~ KEY_NO_VALUE
                if (m.matches()) {
                    def key = m.group(1)
                    argsMap."${key}" = true
                }
                m = it =~ ONLY_PARAM
                if (m.matches()) {
                    def key = m.group(1)
                    argsMap.params = argsMap.params ?: []
                    argsMap.params << key
                }
            }
        }
    }
}
