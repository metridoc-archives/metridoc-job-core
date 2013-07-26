package metridoc.core.tools

import org.slf4j.impl.SimpleLogger

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class LogTool extends DefaultTool {

    public static final String DEFAULT_LOG_LEVEL = "org.slf4j.simpleLogger.defaultLogLevel"
    public static final String METRIDOC_LOGGER = "org.slf4j.simpleLogger.log.metridoc"
    public static final String LOG_FILE = "org.slf4j.simpleLogger.logFile"
    String logLevel
    boolean verboseLine = false

    void init() {
        if (!verboseLine) {
            System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false")
            System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false")
        }

        if (logLevel) {
            System.setProperty(DEFAULT_LOG_LEVEL, logLevel)
            return
        }

        def result
        result = System.getProperty(DEFAULT_LOG_LEVEL)

        if (!result) {
            System.setProperty(DEFAULT_LOG_LEVEL, "error")
        }

        result = System.getProperty(METRIDOC_LOGGER)

        if (!result) {
            System.setProperty(METRIDOC_LOGGER, "info")
        }

        result = System.getProperty(LOG_FILE)

        if (!result) {
            System.setProperty(LOG_FILE, "System.out")
        }

        result = System.getProperty(SimpleLogger.SHOW_DATE_TIME_KEY)

        if (!result) {
            System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true")
        }
    }
}
