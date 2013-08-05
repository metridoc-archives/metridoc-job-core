package metridoc.entities

import metridoc.core.RecordLoader
import metridoc.iterators.Record

import javax.persistence.MappedSuperclass

/**
 * Created with IntelliJ IDEA on 7/2/13
 * @author Tommy Barker
 */
@MappedSuperclass
abstract class MetridocRecordEntity extends MetridocEntity implements RecordLoader {
    abstract void validate()

    boolean acceptRecord(Record record) {
        return true
    }

    boolean shouldSave() {
        return true
    }

    void populate(Record record) {
        def dataOfInterest = record.body.findAll { this.properties.keySet().contains(it.key) }
        try {
            dataOfInterest.each {
                this."$it.key" = it.value
            }
            validate()
        }
        catch (ClassCastException e) {
            throw new AssertionError("Cast error setting values", e)
        }
    }
}
