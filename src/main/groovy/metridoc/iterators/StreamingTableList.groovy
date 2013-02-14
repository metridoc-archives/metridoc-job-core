package metridoc.iterators

import groovy.sql.Sql

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/16/12
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
class StreamingTableList extends AbstractList {
    def id = "id"

    @Override
    Iterator iterator() {
        return new StreamingNoNullIterator(iterator: super.iterator())
    }

    def table
    def dataSource
    def batchSize
    def currentCache = [:]
    private size

    def selectSize = {id, table ->
        "select max(${id}) as total from $table" as String
    }

    def selectAllWithIdBetween = {id, table, low, hi ->
        "select * from ${table} where ${id} >= ${low} and ${id} <= ${hi}" as String
    }

    private Sql _sql

    @Override
    def synchronized get(int i) {

        if(currentCache.containsKey(i)) {
            return currentCache.get(i)
        }

        if(i >= size()) {
            throw new ArrayIndexOutOfBoundsException("$i is greater than or equal to ${size()}")
        }

        def hi = Math.min(size(), i + batchSize)

        currentCache.clear()
        (i..batchSize).each {
            currentCache[it] = null
        }

        sql.eachRow(selectAllWithIdBetween(id, table, i, hi)) {
            currentCache[it."$id"] = it
        }

        return currentCache.get(i)
    }

    @Override
    int size() {
        if(size) return size

        size = sql.firstRow(selectSize(id, table)).total as Integer
    }

    def getSql() {
        if(_sql) return _sql
        assert dataSource: "dataSource must not be null"

        _sql = new Sql(dataSource)
    }
}
