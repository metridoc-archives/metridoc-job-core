package metridoc.core.tools

import metridoc.core.TargetManager
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.file.GenericFile
import org.apache.camel.component.file.GenericFileFilter
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultExchange
import org.apache.commons.lang.SystemUtils
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/16/13
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
class CamelToolTest {

    def tool = new CamelTool()

    @Test
    void "do basic from and to test"() {

        def fromCalled = false

        tool.with {
            withCamelContext {
                asyncSend("seda:test", "testBody")
                consume("seda:test") {
                    assert "testBody" == it
                    fromCalled = true
                }
            }
        }
        assert fromCalled
    }

    @Test
    void "do raw Exchange handling"() {
        def fromCalled = false

        tool.with {
            withCamelContext {
                asyncSend("seda:test", "testBody")
                consume("seda:test") { Exchange exchange ->
                    assert "testBody" == exchange.in.body
                    fromCalled = true
                }
            }
        }
        assert fromCalled
    }

    @Test
    void "test binding to binding"() {
        def binding = new Binding()
        binding.setVariable("boo", "bam")
        binding.setVariable("foo", "bar")
        tool.binding = binding
        tool.withCamelContext { CamelContext context ->
            def registry = context.registry
            assert "bam" == registry.lookup("boo")
            assert "bar" == registry.lookup("foo")
        }
    }

    @Test
    void "make sure we can add the tool to the target manager"() {
        def manager = new TargetManager()
        manager.includeTool(CamelTool)
        assert manager.binding.camelTool
    }

    @Test
    void "test full blown routing examples"() {
        def binding = new Binding()
        binding.fileFilter = [
                accept: { GenericFile file ->
                    return file.fileName.startsWith("file1") ||
                            file.fileName.startsWith("file2") ||
                            file.fileName.startsWith("file3") ||
                            file.fileName.startsWith("file4")

                }
        ] as GenericFileFilter

        tool.binding = binding
        deleteTempDirectoryAndFiles()
        createTempDirectoryAndFiles()
        MockEndpoint mock = null
        tool.with {
            withCamelContext { CamelContext context ->
                mock = context.getEndpoint("mock:endFull")
                mock.expectedMessageCount(4)
                Set fileNames = []
                //let's do 5 messages.  The fifth should be ignored because of the file filter
                (1..5).each {
                    consumeWait("file://${tmpDirectory.path}?noop=true&initialDelay=0&filter=#fileFilter" as String, 1000L) { GenericFile file ->
                        if (file != null) {
                            fileNames << file.fileName
                            send("mock:endFull", it)
                        }
                    }
                }
                assert 4 == fileNames.size()
                mock.assertIsSatisfied()
            }
        }

        deleteTempDirectoryAndFiles()
    }

    @Test
    void "if there is a failure proper actions should take place, such as moving files on error"() {
        def tool = new CamelTool()
        createTempDirectoryAndFiles()
        tool.with() {
            try {
                consume("file://${tmpDirectory.path}?initialDelay=0&moveFailed=.error" as String) { GenericFile file ->
                    throw new RuntimeException("maent to fail for testing")
                }
                assert false : "exception should have occurred"
            } catch (RuntimeException e) {
                assert new File("${tmpDirectory.path}/.error").listFiles()
            }
        }

        deleteTempDirectoryAndFiles()
    }

    @Test
    void "check responses from a route"() {
        def tool = new CamelTool()
        tool.withCamelContext {CamelContext camelContext ->
            camelContext.addRoutes(
                    new RouteBuilder() {
                        @Override
                        void configure() throws Exception {
                            from("direct:start").process(
                                    new Processor() {
                                        @Override
                                        void process(Exchange exchange) throws Exception {
                                            exchange.out.body = 5
                                        }
                                    }
                            )
                        }
                    }
            )
            camelContext.start()
            int response = tool.send("direct:start", "hello") as int
            assert 5 == response
        }
    }


    def getTmpDirectory() {
        def home = SystemUtils.USER_HOME
        new File("${home}/.metridoctmp")
    }

    def getErrorDirectory() {
        new File("${tmpDirectory.path}/.error")
    }

    def deleteErrorFiles() {
        if (errorDirectory.exists()) {
            errorDirectory.eachFile {
                it.delete()
            }
            errorDirectory.delete()
        }
    }

    def deleteTempDirectoryAndFiles() {
        deleteFiles()
        deleteErrorFiles()
        tmpDirectory.delete()
    }

    def deleteFiles() {
        tmpDirectory.listFiles().each {
            it.delete()
        }
    }

    def createTempDirectoryAndFiles() {
        def home = SystemUtils.USER_HOME
        def tempDirectory = new File("${home}/.metridoctmp")
        tempDirectory.mkdir()
        deleteFiles()

        File.createTempFile("unused", "metridocTest", tempDirectory)
        File.createTempFile("file1", "metridocTest", tempDirectory)
        File.createTempFile("file2", "metridocTest", tempDirectory)
        File.createTempFile("file3", "metridocTest", tempDirectory)
        File.createTempFile("file4", "metridocTest", tempDirectory)
        File.createTempFile("file5", "metridocTest", tempDirectory)
        File.createTempFile("file6", "metridocTest", tempDirectory)
    }
}

