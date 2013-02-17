package metridoc.core.tools

import metridoc.core.TargetManager
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.component.file.GenericFile
import org.apache.camel.component.file.GenericFileFilter
import org.apache.camel.component.mock.MockEndpoint
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
                consume("seda:test") {Exchange exchange ->
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
        assert manager.binding.camel
    }

    @Test
    void "test full blown routing examples"() {
        int filesProcessed = 0
        def binding = new Binding()
        binding.fileFilter = [
                accept: { GenericFile file ->
                    def correctFileName = file.fileName.startsWith("file")
                    def noMoreThanFourFilesProcessed = filesProcessed < 4
                    filesProcessed++
                    return correctFileName && noMoreThanFourFilesProcessed
                }
        ] as GenericFileFilter

        tool.binding = binding
        createTempDirectoryAndFiles()
        MockEndpoint mock = null
        tool.with {
            withCamelContext { CamelContext context ->
                mock = context.getEndpoint("mock:endFull")
                mock.expectedMessageCount(4)
                Set fileNames = []
                //let's do 5 messages.  The fifth should be ignored because of the file filter
                (1..5).each {
                    consumeWait("file://${tmpDirectory.path}?initialDelay=0&filter=#fileFilter" as String, 1000L) {GenericFile file ->
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

    def getTmpDirectory() {
        def home = SystemUtils.USER_HOME
        new File("${home}/.metridoctmp")
    }

    def deleteTempDirectoryAndFiles() {
        deleteFiles()

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

