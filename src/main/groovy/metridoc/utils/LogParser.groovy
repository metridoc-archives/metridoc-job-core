package metridoc.utils

import java.util.regex.Matcher

/**
 * Basic log parsing utility
 */
class LogParser {

    LinkedHashMap<String, String> regexMap = [:]

    Map<String, Matcher> parseGetMatcher(String line) {
        return LogParser.parse(line, regexMap) {it}
    }

    Map<String, String> parse(String line) {
        return LogParser.parse(line, regexMap) {Matcher m ->
            m.group(1)
        }
    }

    static Map parse(String line, Map regexMap, Closure closure) {
        def result = [:]
        def currentLine = line
        regexMap.each {
            def m = currentLine =~ it.value
            if (m.lookingAt()) {
                result[it.key] = closure.call(m)
                def start = m.group()
                currentLine = currentLine.substring(start.size())
            } else {
                result[it.key] = null
            }
        }

        return result
    }
}
