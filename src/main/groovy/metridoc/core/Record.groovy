package metridoc.core

import groovy.sql.Sql
import org.hibernate.Session

/**
 * Contains a record that is meant to be stored either through a hibernate session or groovy sql
 */
class Record {
    Sql sql
    Session session
    Map<String, Object> data
}
