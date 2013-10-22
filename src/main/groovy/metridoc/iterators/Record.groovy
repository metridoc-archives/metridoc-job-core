package metridoc.iterators

import groovy.transform.ToString

/**
 * Created with IntelliJ IDEA on 7/9/13
 *
 * @author Tommy Barker
 * @deprecated
 */
@ToString(includePackage = false, includeFields = true, includeNames = true, includes = ["body"])
class Record implements Cloneable {
    Map body = [:]
    Map headers = [:]

    /**
     * When creating a record, a RecordIterator can set an exception if one occurred while
     * creating the record instead of just throwing an exception
     */
    Throwable throwable

    @Override
    protected Object clone() throws CloneNotSupportedException {
        def result = new Record()

        if (body) {
            result.body = body
        }

        if (headers) {
            result.headers = headers
        }

        return result
    }

    def asType(Class clazz) {
        if (clazz.isAssignableFrom(Map)) {
            return body
        }

        super.asType(clazz)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Record)) return false

        Record record = (Record) o

        if (body != record.body) return false
        if (headers != record.headers) return false

        return true
    }

    int hashCode() {
        int result
        result = (body != null ? body.hashCode() : 0)
        result = 31 * result + (headers != null ? headers.hashCode() : 0)
        return result
    }

    def <T> T getHeader(String headerName, Class<T> type) {
        if (headers.containsKey(headerName)) {
            try {
                return headers[headerName].asType(type)
            }
            catch (ClassCastException ignore) {
            }
        }

        return null
    }
}
