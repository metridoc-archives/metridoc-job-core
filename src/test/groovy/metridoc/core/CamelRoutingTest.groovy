/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.core

import org.apache.camel.component.file.GenericFile
import org.apache.camel.component.file.GenericFileFilter
import org.apache.camel.component.mock.MockEndpoint
import org.apache.commons.lang.SystemUtils
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/25/12
 * Time: 9:57 AM
 */
class CamelRoutingTest {

    def job = new CamelRoutingJob()

    @Test(timeout = 5000L)
    void testFullRoute() {
        job.executeTarget("fullRoute")
    }
}

class CamelRoutingJob extends MetridocJob {

    def filesProcessed = 0
    def fileFilter = [
            accept: { GenericFile file ->
                def correctFileName = file.fileName.startsWith("file")
                def noMoreThanFourFilesProcessed = filesProcessed < 4
                filesProcessed++
                return correctFileName && noMoreThanFourFilesProcessed
            }
    ] as GenericFileFilter

    @Override
    def doExecute() {

        target(fullRoute: "a more complicated route") {

            createTempDirectoryAndFiles()
            MockEndpoint mock = null
            runRoute {
                mock = context.getEndpoint("mock:endFull")
                mock.reset()
                mock.expectedMessageCount(1)
                from("file://${tmpDirectory.path}?noop=true&initialDelay=0&filter=#fileFilter&maxMessagesPerPoll=4").threads(4).aggregateBody(4, 2000).to("mock:endFull")
            }

            mock.assertIsSatisfied()
            deleteTempDirectoryAndFiles()
        }
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
