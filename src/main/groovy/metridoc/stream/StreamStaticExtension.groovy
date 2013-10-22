package metridoc.stream

import groovy.stream.Stream
import metridoc.iterators.Iterators

import java.sql.ResultSet

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class StreamStaticExtension {
    public static Stream fromResultSet(Stream self, ResultSet resultSet) {
        assert resultSet != null: "resultSet cannot be null"
        new ResultSetStream(resultSet: resultSet).toStream()
    }

    public static Stream fromDelimited(Stream self, File file, String delimiter, Map options = [:]) {
        assert file: "file cannot be null"
        return fromDelimited(self, file.newInputStream(), delimiter, options)
    }

    public static Stream fromDelimited(Stream self, InputStream stream, String delimiter, Map options = [:]) {
        assert stream: "stream cannot be null"
        assert delimiter: "delimiter cannot be null"
        def params = [delimiter: delimiter, inputStream: stream] as LinkedHashMap
        params.putAll(options)
        new DelimitedLineStream(params).toStream()
    }
}
