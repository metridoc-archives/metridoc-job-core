package metridoc.writers

import groovy.sql.Sql
import metridoc.iterators.RowIterator
import metridoc.sql.SqlPlus

import javax.sql.DataSource

class DataSourceWriter implements IteratorWriter<Sql> {
    DataSource dataSource
    String tableName
    Closure resultsCallBack

    @Override
    Sql write(RowIterator rowIterator) {
        assert dataSource != null: "dataSource must not be null"
        assert tableName != null: "tableName must not be null"
        def sql = new SqlPlus(dataSource)
        List itemsToInsert = rowIterator.collect()
        def results = sql.runBatch(tableName, itemsToInsert)

        if (resultsCallBack) {
            resultsCallBack.call(results)
        }

        return sql
    }
}
