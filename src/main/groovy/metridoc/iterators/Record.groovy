package metridoc.iterators

/**
 * Created with IntelliJ IDEA on 7/9/13
 *
 * @author Tommy Barker
 */
class Record implements Cloneable {
    Map body = [:]
    Map headers = [:]

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
}
