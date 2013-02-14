/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.iterators

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/9/11
 * Time: 10:46 AM
 *
 * This class is a convenience class for creating new grid implementations.  This class is not thread safe.  Iteration
 * should be performed with one thread.
 */
abstract class DefaultIterator<U> implements Iterator<U> {

    int line = 0
    private boolean hasClosed

    private U peekedAtRecord

    U next() {
        if (hasNext()) {

            def data
            if (!peekedAtRecord) {
                peek()
            }

            data = peekedAtRecord
            peekedAtRecord = null

            return data
        }

        throw new NoSuchElementException("no more elements in ${this}")
    }

    protected void peek() {
        line++
        peekedAtRecord = doNext()
    }

    boolean hasNext() {

        boolean result
        if (!peekedAtRecord) {
            peek()
        }

        boolean doClose = !hasClosed && !peekedAtRecord && this instanceof Closeable

        if (doClose) {
            def closeable = this as Closeable
            closeable.close()
            hasClosed = true
        }

        return peekedAtRecord ? true : false
    }

    void remove() {
        throw new UnsupportedOperationException("remove not supported in DefaultIterator")
    }

    abstract U doNext()
}
