package metridoc.stream

import au.com.bytecode.opencsv.CSVReader

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class CsvStream extends FileStream<Map> {
    @Lazy(soft = true)
    CSVReader csvReader = { new CSVReader(new InputStreamReader(inputStream)) }()

    @Lazy(soft = true)
    List headers = { csvReader.readNext() as List }()

    @Override
    protected Map computeNext() {

        def headersSize = headers.size()
        def next = csvReader.readNext()

        if (next != null && next.size() != headersSize) {
            def errorMessage = "headers ${headers} and result ${next} do not have the same number of arguments"
            throw new IllegalStateException(errorMessage)
        }

        def result = [:]

        if (next) {
            (0..next.size() - 1).each {
                result[headers[it]] = next[it]
            }
        }

        if (next == null) {
            csvReader.close()
            return endOfData()
        }

        return result
    }
}
