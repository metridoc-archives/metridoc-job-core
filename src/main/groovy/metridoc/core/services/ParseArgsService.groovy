package metridoc.core.services

import java.util.regex.Matcher

/**
 * @author Tommy Barker
 */
class ParseArgsService {
    static final String ARGS = "args"
    static final KEY_VALUE = /^[\-]+([^=]+)=(.*)$/
    static final KEY_NO_VALUE = /^[\-]+([^=]+)$/
    static final ONLY_PARAM = /^([^\-]+)$/

    Binding binding

    @SuppressWarnings("GrMethodMayBeStatic")
    void init() {
        if (binding.hasVariable(ARGS) &&
                (binding."$ARGS" instanceof String[] ||
                        binding."$ARGS" instanceof List)) {

            def args = binding."$ARGS"
            if (args instanceof List) {
                args = args as String[]
            }
            binding.argsMap = parseCli(args)
        }
    }

    static Map parseCli(String[] args) {
        def argsMap = [:]
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

        return argsMap
    }
}
