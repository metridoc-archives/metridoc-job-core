package metridoc.writers

import groovy.util.logging.Slf4j
import metridoc.iterators.Record
import metridoc.iterators.RecordIterator
import metridoc.sql.SqlPlus

import javax.sql.DataSource
import java.sql.Connection
import java.sql.PreparedStatement

/**
 * @deprecated
 */
@Slf4j
class DataSourceIteratorWriter extends DefaultIteratorWriter {
    public static final String DATASOURCE_MESSAGE = "dataSource cannot be null"
    public static final String TABLE_NAME_ERROR = "tableName cannot be null"
    public static final String ROW_ITERATOR_ERROR = "record Iterator cannot be null"
    DataSource dataSource
    String tableName

    @Lazy
    SqlPlus sql = {
        assert dataSource != null: "dataSource must not be null"
        new SqlPlus(dataSource)
    }()

    @Override
    WriteResponse write(RecordIterator recordIterator) {
        assert dataSource != null: DATASOURCE_MESSAGE
        assert tableName != null: TABLE_NAME_ERROR
        assert recordIterator != null: ROW_ITERATOR_ERROR
        def firstRow = recordIterator.peek()
        def sortedParams = new TreeSet(firstRow.body.keySet())
        def headers = recordIterator.recordHeaders
        headers.sortedParams = sortedParams

        try {
            def totals = null
            try {
                sql.withTransaction { Connection connection ->
                    def sql = SqlPlus.getInsertStatement(tableName, firstRow.body)
                    def preparedStatement = connection.prepareStatement(sql)
                    headers.preparedStatement = preparedStatement
                    totals = super.write(recordIterator)
                    if (totals.fatalErrors) {
                        //throw the first one
                        throw totals.fatalErrors[0]
                    }
                    totals.body.batchResponse = preparedStatement.executeBatch()
                }
            }
            catch (Throwable throwable) {
                //no records were written... start from scratch
                totals = new WriteResponse()
                totals.aggregateStats[WrittenRecordStat.Status.ERROR] = 1
                totals.fatalErrors << throwable
            }
            return totals
        }
        finally {
            sql.close()
        }
    }

    @Override
    boolean doWrite(int lineNumber, Record record) {
        validateState(sql, "sqlPlus service cannot be null")
        def headers = record.headers

        def preparedStatement = headers.preparedStatement as PreparedStatement
        def sortedParams = headers.sortedParams
        sql.processRecord(preparedStatement, record.body, sortedParams)

        return true
    }
}
