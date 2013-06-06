package metridoc.writers;

import java.util.Iterator;
import java.util.Map;

public interface IteratorWriter<T> extends Iterator<Map<String, Object>> {
    T write();
}
