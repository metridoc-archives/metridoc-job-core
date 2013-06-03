package metridoc.writers

import com.google.common.collect.TreeBasedTable
import metridoc.iterators.RowIterator

class TableWriter implements IteratorWriter<TreeBasedTable<Integer, String, Object>> {

    @Override
    TreeBasedTable write(RowIterator rowIterator) {
        assert rowIterator != null: "iterator cannot be null"
        TreeBasedTable<Integer, String, Object> treeBasedTable = TreeBasedTable.create()
        rowIterator.eachWithIndex { Map entry, int rowKey ->
            entry.each { String columnName, value ->
                treeBasedTable.put(rowKey, columnName, value)
            }
        }

        return treeBasedTable
    }
}
