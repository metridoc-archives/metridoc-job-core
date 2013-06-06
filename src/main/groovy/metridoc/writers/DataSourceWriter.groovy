package metridoc.writers

import groovy.util.logging.Slf4j
import metridoc.sql.SqlPlus

import javax.sql.DataSource
import java.sql.Connection

@Slf4j
class DataSourceWriter extends DefaultIteratorWriter<List<Integer>> {
    public static final String DATASOURCE_MESSAGE = "dataSource cannot be null"
    public static final String TABLE_NAME_ERROR = "tableName cannot be null"
    public static final String ROW_ITERATOR_ERROR = "row Iterator cannot be null"
    DataSource dataSource
    String tableName

    List<Integer> response

    @Lazy
    Map<String, Object> firstRow = {
        rowIterator.peek()
    }()

    @Lazy
    SqlPlus sql = {
        assert dataSource != null: "dataSource must not be null"
        new SqlPlus(dataSource)
    }()

    @Lazy
    SortedSet<String> sortedParams = {
        new TreeSet(firstRow.keySet())
    }()

    @Lazy(soft = true)
    RowWriter rowWriter = {
        new DataSourceRowWriter(sortedParams: sortedParams, sql: sql)
    }()

    @Override
    synchronized List<Integer> write() {
        assert dataSource != null: DATASOURCE_MESSAGE
        assert tableName != null: TABLE_NAME_ERROR
        assert rowIterator != null: ROW_ITERATOR_ERROR

        try {
            sql.withTransaction { Connection connection ->
                def sql = SqlPlus.getInsertStatement(tableName, firstRow)
                def preparedStatement = connection.prepareStatement(sql)
                if (rowWriter instanceof DataSourceRowWriter) {
                    rowWriter.preparedStatement = preparedStatement
                }
                super.write()

                response = preparedStatement.executeBatch()
            }
        } finally {
            sql.close()
        }

        return response
    }
}
