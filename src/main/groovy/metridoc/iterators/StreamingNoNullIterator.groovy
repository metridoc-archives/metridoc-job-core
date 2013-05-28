package metridoc.iterators

import com.google.common.collect.AbstractIterator

class StreamingNoNullIterator extends AbstractIterator {
    Iterator iterator

    @Override
    protected Object computeNext() {
        while(iterator.hasNext()) {
            def next = iterator.next()
            if(next != null) {
                return next
            }
        }

        return endOfData()
    }
}
