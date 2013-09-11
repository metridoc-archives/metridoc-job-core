package metridoc.iterators

import metridoc.writers.WriteResponse

import javax.sql.DataSource
import java.sql.ResultSet;

/**
 * Created with IntelliJ IDEA on 9/11/13
 *
 * @author Tommy Barker
 */
public class IteratorsExtension {

    public static WrappedIterator fromSql(Iterators self, ResultSet resultSet) {
        assert resultSet != null : "resultSet cannot be null"
        Iterators.createIterator([resultSet: resultSet] as LinkedHashMap, "sql")
    }

    public static WrappedIterator fromDelimited(Iterators self, File file, String delimiter, Map options = [:]) {
        assert file : "file cannot be null"
        def result = fromDelimited(self, file.newInputStream(), delimiter)
        result.file = file

        return result
    }

    public static WrappedIterator fromDelimited(Iterators self, InputStream stream, String delimiter, Map options = [:]) {
        assert stream : "stream cannot be null"
        def params = [delimiter: delimiter, inputStream: stream] as LinkedHashMap
        params.putAll(options)
        Iterators.createIterator(params, "delimited")
    }

    public static WrappedIterator fromMaps(Iterators self, Map... data) {
        Iterators.toRecordIterator(data.toList())
    }

    public static WrappedIterator fromCsv(Iterators self, InputStream inputStream) {
        Iterators.createIterator([inputStream: inputStream] as LinkedHashMap, "csv")
    }

    public static WrappedIterator fromCsv(Iterators self, InputStream inputStream, List headers) {
        Iterators.createIterator([inputStream: inputStream, headers: headers] as LinkedHashMap, "csv")
    }
}
