package metridoc.entities

import javax.persistence.MappedSuperclass

/**
 * Created with IntelliJ IDEA on 7/2/13
 * @author Tommy Barker
 */
@MappedSuperclass
abstract class MetridocRecordEntity extends MetridocEntity {
    abstract void validate()

    @SuppressWarnings("GrMethodMayBeStatic")
    boolean acceptRecord(Map record) {
        return true
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    boolean shouldSave() {
        return true
    }

    void populate(Map record) {
        def dataOfInterest = record.findAll { this.properties.keySet().contains(it.key) }
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
