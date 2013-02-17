package metridoc.utils

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 12/16/12
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
class ApacheLogParserTest {

    @Test
    void "test parsing a common apache log" () {
        def commonLog = '127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326'
        def result = new ApacheLogParser().parseCommon(commonLog)
        assert "127.0.0.1" == result.ipAddress
        assert "user-identifier" == result.clientId
        assert "frank" == result.patronId
        assert result.logDate instanceof Date
        assert result.httpMethod == "GET"
        assert result.url == "/apache_pb.gif"
        assert result.httpStatus == 200
        assert result.fileSize == 2326
    }

    @Test
    void "testing parsing a combined apache log"() {
        def combinedLog = '127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326 "http://www.example.com/start.html" "Mozilla/4.08 [en] (Win98; I ;Nav)"'
        def result = new ApacheLogParser().parseCombined(combinedLog)
        assert "127.0.0.1" == result.ipAddress
        assert "user-identifier" == result.clientId
        assert "frank" == result.patronId
        assert result.logDate instanceof Date
        assert result.httpMethod == "GET"
        assert result.url == "/apache_pb.gif"
        assert result.httpStatus == 200
        assert result.fileSize == 2326
        assert result.refUrl == "http://www.example.com/start.html"
        assert result.agent == "Mozilla/4.08 [en] (Win98; I ;Nav)"

    }

}
