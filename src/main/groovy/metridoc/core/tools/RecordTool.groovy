package metridoc.core.tools

import groovy.transform.ToString
import groovy.util.logging.Slf4j

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/18/13
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
abstract class RecordTool extends RunnableTool {
    String encoding = "utf-8"

    @Override
    void setBinding(Binding binding) {
        super.setBinding(binding)
        encoding = getVariable("encoding") ?: this.encoding
    }



    def doRun() {
        handleRecords(preProcessors)
        handleRecords(sourceProcessors)
        handleRecords(postProcessors)
    }

    private handleRecords(List processors) {
        if (processors) { //this makes sure they are not null as well and avoids NPE
            sources.each {
                def record = new Record(sourceMetaData: getMetaData(source), tool: this)
                processors.each {processor ->
                    new RecordProcessorWrapper(processor: processor).process(record)
                }
            }

        }
    }

    abstract List getSources()
    abstract List getPreProcessors()
    abstract List getPostProcessors()
    abstract List getSourceProcessors()

    /**
     * meant to be overridden, provides meta data about the source about to be parsed
     * @param source
     * @return
     */
    Map getMetaData(source) {
        [encoding: this.encoding]
    }
}

@ToString(includeFields = true)
class Record {
    Map sourceMetaData
    Tool tool
    Map data
}

class RecordProcessorWrapper implements RecordProcessor{
    def processor

    @Override
    Record process(Record record) {
        if(processor instanceof Closure) {
            processor.call(record)
        } else {
            processor.process(record)
        }
    }
}

interface RecordProcessor {
    Record process(Record record)
}