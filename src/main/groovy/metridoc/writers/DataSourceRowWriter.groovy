package metridoc.writers

import metridoc.sql.SqlPlus

import java.sql.PreparedStatement

/**
 * Created with IntelliJ IDEA on 6/5/13
 * @author Tommy Barker
 */
class DataSourceRowWriter extends DefaultRowWriter {

    PreparedStatement preparedStatement
    SortedSet sortedParams
    SqlPlus sql

    @Override
    void write(int line, Map<String, Object> record) {
        assert preparedStatement != null: "prepared statement cannot be null"
        assert sortedParams != null: "prepared statement cannot be null"
        assert sql != null: "sqlPlus service cannot be null"

        sql.processRecord(preparedStatement, record, sortedParams)
    }
}
