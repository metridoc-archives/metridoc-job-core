package metridoc.core;

import metridoc.iterators.Record;

/**
 * @author Tommy Barker
 */
public interface RecordLoader {
    void validate();

    boolean acceptRecord(Record record);

    boolean shouldSave();

    void populate(Record record);
}
