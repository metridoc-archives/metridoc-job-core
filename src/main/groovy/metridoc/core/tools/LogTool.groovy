package metridoc.core.tools

/**
 * Created with IntelliJ IDEA on 7/25/13
 * @author Tommy Barker
 */
class LogTool extends DefaultTool {

    public static final String DEFAULT_LOG_LEVEL = "org.slf4j.simpleLogger.defaultLogLevel"
    public static final String METRIDOC_LOGGER = "org.slf4j.simpleLogger.log.metridoc"
    public static final String LOG_FILE = "org.slf4j.simpleLogger.logFile"
    String logLevel

    void init() {
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
    }
}
