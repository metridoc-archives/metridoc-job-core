package metridoc.writers

import org.apache.commons.lang.text.StrBuilder
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import static metridoc.writers.WrittenRecordStat.Status.*

/**
 * Created with IntelliJ IDEA on 7/3/13
 * @author Tommy Barker
 */
class WriteResponse {
    EnumMap<WrittenRecordStat.Status, Integer> aggregateStats = new EnumMap<WrittenRecordStat.Status, Integer>(WrittenRecordStat.Status)
    Map<String, Object> response = [:]

    WriteResponse() {
        aggregateStats[ERROR] = 0
        aggregateStats[IGNORED] = 0
        aggregateStats[WRITTEN] = 0
        aggregateStats[INVALID] = 0
    }

    void addAll(List<WrittenRecordStat> stats) {
        stats.each {
            aggregateStats[it.status] = aggregateStats[it.status] + 1
        }
    }

    def asType(Class clazz) {
        def possibilities = response.values().findAll { clazz.isAssignableFrom(it.getClass()) }
        if (possibilities.size() == 0) {
            super.asType(clazz) //let the normal implementation fail on this
        }

        if (possibilities.size() > 1) {
            def message = new StrBuilder("Could not convert response to $clazz, there were more than one possible solution")
            possibilities.each {
                message.appendln("  --> $it")
            }

            throw new GroovyCastException(message.toString())
        }

        possibilities[0]
    }
}
