package metridoc.stream

import groovy.stream.Stream
import metridoc.writers.WriteResponse
import metridoc.writers.WrittenRecordStat
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Tommy Barker
 */
class StreamExtension {

    @SuppressWarnings("GroovyAccessibility")
    public static WriteResponse each(Stream self, Closure closure) {
        def response = new WriteResponse()
        Closure clone = closure.clone() as Closure

        def wrapped = self.wrapped
        Closure oldCondition = wrapped.condition
        boolean lastFilterInvalid = false
        if(oldCondition) {
            self.filter {value ->
                def result = oldCondition.call(value)
                if (!result) {
                    response.aggregateStats[WrittenRecordStat.Status.IGNORED] = response.ignoredTotal + 1
                    lastFilterInvalid = true
                }
                else {
                    lastFilterInvalid = false
                }

                return result
            }
        }

        int count = 0
        int logEvery = getMetaProperty(self, "logEvery", Integer) ?: 1000
        while (self.hasNext()) {
            def next = self.next()
            boolean valid = true
            Closure validator = getMetaProperty(self, "validator", Closure)
            if(validator) {
                validator.delegate = wrapped.using
                validator.resolveStrategy = Closure.DELEGATE_FIRST
                try {
                    def validatorResponse = validator.call(next)
                    if(validatorResponse instanceof Boolean) {
                        valid = validatorResponse
                    }
                }
                catch (AssertionError assertionError) {
                    getLogger(self).warn("invalid record: $assertionError.message")
                    valid = false
                }
            }
            if (valid) {
                clone.call(next)
                response.aggregateStats[WrittenRecordStat.Status.WRITTEN] = response.writtenTotal + 1
            }
            else {
                response.aggregateStats[WrittenRecordStat.Status.INVALID] = response.invalidTotal + 1
            }

            if(count != 0 && count % logEvery == 0) {
                getLogger(self).info response.toString()
            }
        }

        if(response.aggregateStats[WrittenRecordStat.Status.IGNORED] > 1 && lastFilterInvalid) {
            response.aggregateStats[WrittenRecordStat.Status.IGNORED] = response.ignoredTotal - 1
        }

        getLogger(self).info response.toString()
        return response
    }

    public static Stream validate(Stream self, Closure validate) {
        setMetaProperty(self, "validator", validate)
        return self
    }

    protected static <T> T getMetaProperty(Stream self, String propertyName, Class<T> aClass) {
        initializePropertyMapIfNotThere(self)

        def response = self.getMetaPropertyMap[propertyName]
        if(response && aClass.isAssignableFrom(response.getClass())) {
            return response.asType(aClass)
        }

        return null
    }

    protected static void setMetaProperty(Stream self, String propertyName, propertyValue) {
        initializePropertyMapIfNotThere(self)

        self.getMetaPropertyMap[propertyName] = propertyValue
    }

    private static void initializePropertyMapIfNotThere(Stream self) {
        if(!self.metaClass.respondsTo(self, "getMetaPropertyMap")) {
            self.metaClass.metaPropertyMap = {
                [:]
            }
        }
    }

    public static Stream logEvery(Stream self, int actionCountBeforeLogging) {
        setMetaProperty(self, "logEvery", actionCountBeforeLogging)
        return self
    }

    public static Stream setLogName(Stream self, String logName) {
        setMetaProperty(self, "logName", logName)
        return self
    }

    public static Logger getLogger(Stream self) {
        def logName = getMetaProperty(self, "logName", String) ?: "metridoc.stream"
        LoggerFactory.getLogger(logName)
    }
}
