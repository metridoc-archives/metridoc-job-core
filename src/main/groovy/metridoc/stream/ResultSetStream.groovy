package metridoc.stream

import com.google.common.collect.AbstractIterator

import java.sql.ResultSet

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class ResultSetStream extends AbstractIterator {

    ResultSet resultSet

    @Override
    protected Map computeNext() {
        if (resultSet.next()) {
            return resultSet.toRowResult()
        }

        return endOfData()
    }
}
