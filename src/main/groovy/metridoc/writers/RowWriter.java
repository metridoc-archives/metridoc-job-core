package metridoc.writers;

import java.util.Map;

/**
 * Created with IntelliJ IDEA on 6/5/13
 *
 * @author Tommy Barker
 */
public interface RowWriter {
    void write(int line, Map<String, Object> record);
}
