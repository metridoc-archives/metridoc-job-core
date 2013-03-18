package metridoc.core.tools

import groovy.util.logging.Slf4j

class FileProcessingTool extends RecordTool {

    List sources
    List preProcessors
    List postProcessors
    List sourceProcessors
    def lineProcessor
    File directory
    File file
    Boolean preview

    List getSources() {
        if (sources) return sources

        if (directory) {

            if (directory.exists()) {
                sources << directory.listFiles().collect { it.isFile() }
            }
        } else if (file) {
            if (file.exists()) {
                sources << [file]
            }
        }

        return sources
    }

    List getSourceProcessors() {
        if (preview) {
            return [new PreviewSourceProcessor()]
        }

        return sourceProcessors
    }
}

@Slf4j
abstract class HandleLineProcessor implements RecordProcessor {

    @Override
    Record process(Record record) {
        FileProcessingTool tool = record.tool
        File file = record.data
        file.eachLine(tool.encoding) { String line, int rowNum ->
            def lineRecord = new Record(
                    tool: tool,
                    sourceMetaData: [
                            file: file
                    ],
                    data: [
                            fileName: file.name,
                            lineNumber: rowNum,
                            line: line
                    ]
            )
            def transformedRecord = new RecordProcessorWrapper(processor: tool.lineProcessor).process(record)
            processTransformedRecord(transformedRecord)
        }

    }

    abstract processTransformedRecord(Record record)
}

@Slf4j
class PreviewSourceProcessor extends HandleLineProcessor{

    @Override
    def processTransformedRecord(Record record) {
        log.info record.toString()
    }
}


