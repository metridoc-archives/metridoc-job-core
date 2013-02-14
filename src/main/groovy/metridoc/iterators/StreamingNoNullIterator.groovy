package metridoc.iterators

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/16/12
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
class StreamingNoNullIterator implements Iterator{
    Iterator iterator
    private next

    synchronized boolean hasNext() {
        if(next) return true

        if(iterator.hasNext()) {
            next = iterator.next()
            return this.hasNext()
        }

        return false  //To change body of implemented methods use File | Settings | File Templates.
    }

    synchronized Object next() {
        if(hasNext()) {
            def nextToReturn = next
            next = null
            return nextToReturn
        }

        throw NoSuchElementException()
    }

    void remove() {
        throw new UnsupportedOperationException("not supported")
    }
}
