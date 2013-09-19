package metridoc.core.services

import groovy.util.logging.Slf4j
import metridoc.core.MetridocScript
import metridoc.utils.DataSourceConfigUtil

import static org.apache.commons.lang.SystemUtils.FILE_SEPARATOR
import static org.apache.commons.lang.SystemUtils.USER_HOME

/**
 * @author Tommy Barker
 */
@Slf4j
class ConfigService extends DefaultService {
    private static final String METRIDOC_CONFIG = "${USER_HOME}${FILE_SEPARATOR}.metridoc${FILE_SEPARATOR}MetridocConfig.groovy"
    def metridocConfigLocation = METRIDOC_CONFIG

    void setBinding(Binding binding) {
        super.setBinding(binding)
        if (!binding.hasVariable("config")) {
            use(MetridocScript) {
                binding.includeService(ParseArgsService)
            }

            String env = getVariable("env", String)
            if ("prod" == env) {
                env = "production"
            }

            if ("dev" == env) {
                env = "development"
            }

            def configSlurper = env ? new ConfigSlurper(env) : new ConfigSlurper()
            File cliConfigLocation = getVariable("config", File)
            def cliConfigObject = new ConfigObject()
            if (cliConfigLocation) {
                cliConfigObject = configureFromFile(cliConfigLocation, configSlurper)
            }

            def metridocConfigFile = new File(metridocConfigLocation)
            if (metridocConfigFile.exists() && mergeMetridocConfig) {
                def metridocConfigObject = configureFromFile(metridocConfigFile, configSlurper)
                metridocConfigObject.merge(cliConfigObject)
                cliConfigObject = metridocConfigObject
            }

            addCliConfigArgs(configSlurper, cliConfigObject)

            binding.config = cliConfigObject
            initiateDataSources(cliConfigObject)
        }
    }

    void addCliConfigArgs(ConfigSlurper slurper, ConfigObject configObject) {
        if (binding.hasVariable("args")) {
            String[] args = binding.args
            File tempFile = File.createTempFile("cliConfig", null)
            args.each {
                if (it.startsWith("-config.") || it.startsWith("--config.")) {
                    def propertyToWrite = it.replaceFirst(/-?-config\./, "")
                    tempFile.append(propertyToWrite)
                    tempFile.append("\n")
                }
            }

            if (tempFile.text) {
                configObject.merge(slurper.parse(tempFile.toURI().toURL()))
            }
        }
    }

    private static ConfigObject configureFromFile(File file, ConfigSlurper slurper) {
        if (file.exists()) {
            return slurper.parse(file.toURI().toURL())
        }

        return null
    }

    ConfigService addConfig(Closure closure) {
        ConfigObject config = binding.variables.config ?: new ConfigObject()
        binding.config = config
        def slurper = new ConfigSlurper()
        config.merge(slurper.parse(new ConfigScript(configClosure: closure)))

        return this
    }

    ConfigService addConfig(File file) {
        ConfigObject config = binding.variables.config ?: new ConfigObject()
        binding.config = config
        def slurper = new ConfigSlurper()
        config.merge(slurper.parse(file.toURI().toURL()))

        return this
    }

    void initiateDataSources(ConfigObject configObject) {
        DataSourceConfigUtil.getDataSourcesNames(configObject).each { String dataSourceName ->
            try {
                def dataSource = DataSourceConfigUtil.getDataSource(configObject, dataSourceName)
                def m = dataSourceName =~ /dataSource_(\w+)$/
                def sqlName = "sql"
                if (m.matches()) {
                    sqlName += "_${m.group(1)}"
                }

                binding."$dataSourceName" = dataSource
                binding."$sqlName" = dataSource
            }
            catch (Throwable throwable) {
                log.warn "Could not instantiate dataSource [$dataSourceName]", throwable
            }
        }
    }
}

class ConfigScript extends Script {
    Closure configClosure

    @Override
    def run() {
        Closure clone = configClosure.clone() as Closure
        clone.delegate = this
        clone.run()
    }
}