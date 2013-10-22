package metridoc.iterators

import groovy.stream.Stream
import metridoc.writers.WriteResponse
import metridoc.writers.WrittenRecordStat
import org.slf4j.LoggerFactory

/**
 * @author Tommy Barker
 */
class StreamExtension {

    public static WriteResponse each(Stream self, Closure closure) {
        def response = new WriteResponse()
        Closure clone = closure.clone()

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

        while (self.hasNext()) {
            def next = self.next()
            boolean valid = true
            if(self.metaClass.respondsTo(self, "getValidator")) {
                Closure validator = self.getValidator()
                validator.delegate = wrapped.using
                validator.resolveStrategy = Closure.DELEGATE_FIRST
                try {
                    def validatorResponse = validator.call(next)
                    if(validatorResponse instanceof Boolean) {
                        valid = validatorResponse
                    }
                }
                catch (AssertionError assertionError) {
                    LoggerFactory.getLogger(Stream).warn("invalid record: $assertionError.message")
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
        }

        if(response.aggregateStats[WrittenRecordStat.Status.IGNORED] > 1 && lastFilterInvalid) {
            response.aggregateStats[WrittenRecordStat.Status.IGNORED] = response.ignoredTotal - 1
        }
        return response
    }

    public static Stream validate(Stream self, Closure validate) {
        self.metaClass.getValidator = {
            validate.clone() as Closure
        }

        return self
    }
}
