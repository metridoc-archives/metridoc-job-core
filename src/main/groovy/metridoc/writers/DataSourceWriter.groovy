package metridoc.writers

import groovy.util.logging.Slf4j
import metridoc.iterators.RowIterator
import metridoc.sql.SqlPlus

import javax.sql.DataSource
import java.sql.Connection
import java.sql.PreparedStatement

@Slf4j
class DataSourceWriter extends DefaultIteratorWriter {
    public static final String DATASOURCE_MESSAGE = "dataSource cannot be null"
    public static final String TABLE_NAME_ERROR = "tableName cannot be null"
    public static final String ROW_ITERATOR_ERROR = "row Iterator cannot be null"
    DataSource dataSource
    String tableName

    List<Integer> response

    @Lazy
    SqlPlus sql = {
        assert dataSource != null: "dataSource must not be null"
        new SqlPlus(dataSource)
    }()

    @Override
    WriteResponseTotals write(RowIterator rowIterator) {
        assert dataSource != null: DATASOURCE_MESSAGE
        assert tableName != null: TABLE_NAME_ERROR
        assert rowIterator != null: ROW_ITERATOR_ERROR
        def firstRow = rowIterator.peek()
        def sortedParams = new TreeSet(firstRow.keySet())



        try {
            def totals = null
            sql.withTransaction { Connection connection ->
                def sql = SqlPlus.getInsertStatement(tableName, firstRow)
                def preparedStatement = connection.prepareStatement(sql)

                def rowIteratorToUse = new RowIterator() {
                    @Override
                    protected Map computeNext() {
                        if (rowIterator.hasNext()) {
                            def result = rowIterator.next()
                            result.sortedParams = sortedParams
                            result.preparedStatement = preparedStatement

                            return result
                        }

                        endOfData()
                    }
                }


                totals = super.write(rowIteratorToUse)
                totals.data.batchResponse = preparedStatement.executeBatch()
            }
            return totals
        }
        finally {
            sql.close()
        }
    }

    @Override
    boolean doWrite(int lineNumber, Map<String, Object> record) {
        validateState(sql, "sqlPlus service cannot be null")
        def preparedStatement = record.remove("preparedStatement") as PreparedStatement
        def sortedParams = record.remove("sortedParams")
        sql.processRecord(preparedStatement, record, sortedParams)

        return true
    }
}
