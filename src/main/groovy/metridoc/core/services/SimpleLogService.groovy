package metridoc.core.services

import groovy.util.logging.Slf4j

/**
 * @author Tommy Barker
 */
@Slf4j
class SimpleLogService extends DefaultService {
    public static final String DEFAULT_LOG_LEVEL = "org.slf4j.simpleLogger.defaultLogLevel"
    public static final String METRIDOC_LOGGER = "org.slf4j.simpleLogger.log.metridoc"
    public static final String LOG_FILE = "org.slf4j.simpleLogger.logFile"
    String logLevel
    boolean verboseLine = false

    void init() {
        Class simpleLoggerClass
        try {
            simpleLoggerClass = Thread.currentThread().contextClassLoader.loadClass("org.slf4j.impl.SimpleLogger")
        }
        catch (ClassNotFoundException ignored) {
            System.err.println("Could not find SimpleLogger on the classpath, [SimpleLogTool] will not be initialized")
            return
        }


        String SHOW_THREAD_NAME_KEY = simpleLoggerClass.SHOW_THREAD_NAME_KEY
        String SHOW_LOG_NAME_KEY = simpleLoggerClass.SHOW_THREAD_NAME_KEY
        String SHOW_DATE_TIME_KEY = simpleLoggerClass.SHOW_DATE_TIME_KEY

        if (!verboseLine) {
            System.setProperty(SHOW_THREAD_NAME_KEY, "false")
            System.setProperty(SHOW_LOG_NAME_KEY, "false")
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

        result = System.getProperty(SHOW_DATE_TIME_KEY)

        if (!result) {
            System.setProperty(SHOW_DATE_TIME_KEY, "true")
        }
    }
}
