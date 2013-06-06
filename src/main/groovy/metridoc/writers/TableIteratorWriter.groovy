package metridoc.writers

import com.google.common.collect.TreeBasedTable

class TableIteratorWriter extends DefaultIteratorWriter<TreeBasedTable<Integer, String, Object>> {

    TreeBasedTable<Integer, String, Object> response = TreeBasedTable.create()

    @Lazy(soft = true)
    RowWriter rowWriter = {
        new TableRowWriter(table: response)
    }()
}
