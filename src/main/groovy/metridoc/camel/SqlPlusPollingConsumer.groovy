package metridoc.camel

import org.apache.camel.PollingConsumer
import org.apache.camel.Exchange
import org.apache.camel.Endpoint

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/7/12
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
class SqlPlusPollingConsumer implements PollingConsumer {

    SqlPlusEndpoint endpoint

    @Override
    Exchange receive() {
        def query = endpoint.getTableQuery()
        def sql = new SqlUnManagedResultSet(endpoint.dataSource)
        def resultSet = sql.executeQuery(query)
        def exchange = endpoint.createExchange()
        exchange.in.body = resultSet
        return exchange
    }

    @Override
    Exchange receiveNoWait() {
        receive()
    }

    @Override
    Exchange receive(long timeout) {
        receive()
    }

    @Override
    void start() {
        //do nothing
    }

    @Override
    void stop() {
        //do nothing
    }
}
