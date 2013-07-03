package metridoc.writers

import static metridoc.writers.WriteResponse.Type.*

/**
 * Created with IntelliJ IDEA on 7/3/13
 * @author Tommy Barker
 */
class WriteResponseTotals {
    EnumMap<WriteResponse.Type, Integer> aggregateStats = new EnumMap<WriteResponse.Type, Integer>(WriteResponse.Type)
    Map<String, Object> data = [:]

    WriteResponseTotals() {
        aggregateStats[ERROR] = 0
        aggregateStats[IGNORED] = 0
        aggregateStats[WRITTEN] = 0
        aggregateStats[INVALID] = 0
    }

    void addAll(List<WriteResponse> responses) {
        responses.each {
            aggregateStats[it.type] = aggregateStats[it.type] + 1
        }
    }
}
