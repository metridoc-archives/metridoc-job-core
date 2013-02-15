package metridoc.core

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/13/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Job {
    def execute(Map<String, Object> config)
    def execute()
    void interrupt()
}