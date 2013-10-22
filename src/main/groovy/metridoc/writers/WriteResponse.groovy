package metridoc.writers

import groovy.transform.ToString
import org.apache.commons.lang.text.StrBuilder
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import static metridoc.writers.WrittenRecordStat.Status.*

/**
 * Created with IntelliJ IDEA on 7/3/13
 * @author Tommy Barker
 */
@ToString(includePackage = false, includes = ["aggregateStats"])
class WriteResponse {
    EnumMap<WrittenRecordStat.Status, Integer> aggregateStats = new EnumMap<WrittenRecordStat.Status, Integer>(WrittenRecordStat.Status)
    Map<String, Object> body = [:]
    Map<String, Object> headers = [:]
    List<Throwable> fatalErrors = []
    List<AssertionError> validationErrors = []

    WriteResponse() {
        aggregateStats[ERROR] = 0
        aggregateStats[IGNORED] = 0
        aggregateStats[WRITTEN] = 0
        aggregateStats[INVALID] = 0
        aggregateStats[TOTAL] = 0
    }

    EnumMap<WrittenRecordStat.Status, Integer> getAggregateStats() {
        //make sure total has been added
        aggregateStats[TOTAL] = getTotal()
        aggregateStats
    }

    void addAll(List<WrittenRecordStat> stats) {
        stats.each {
            aggregateStats[it.status] = aggregateStats[it.status] + 1
            if (it.fatalError) {
                fatalErrors << it.fatalError
            }
            if (it.validationError) {
                validationErrors << it.validationError
            }
        }
    }

    def asType(Class clazz) {
        def possibilities = body.values().findAll { clazz.isAssignableFrom(it.getClass()) }
        if (possibilities.size() == 0) {
            super.asType(clazz) //let the normal implementation fail on this
        }

        if (possibilities.size() > 1) {
            def message = new StrBuilder("Could not convert response to $clazz, there was more than one possible solution")
            possibilities.each {
                message.appendln("  --> $it")
            }

            throw new GroovyCastException(message.toString())
        }

        possibilities[0]
    }

    int getTotal() {
        int total = 0
        aggregateStats.findAll{it.key != TOTAL}.each {
            total += it.value
        }
        return total
    }

    int getErrorTotal() {
        aggregateStats[ERROR]
    }

    int getInvalidTotal() {
        aggregateStats[INVALID]
    }

    int getWrittenTotal() {
        aggregateStats[WRITTEN]
    }

    int getIgnoredTotal() {
        aggregateStats[IGNORED]
    }

    void addError(Throwable throwable) {
        aggregateStats[ERROR] += 1
        fatalErrors << throwable
    }

    void addInvalid(AssertionError assertionError) {
        validationErrors << assertionError
        aggregateStats[INVALID] += 1
    }
}
