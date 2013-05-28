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

import org.apache.commons.io.LineIterator


class DelimitedLineIterator extends FileIterator<List<String>> {

    Reader reader
    LineIterator lineIterator
    /**
     * regex of the delimiter, for instance if you split text with | you would pass /\|/
     */
    String delimiter

    int delimitTill = 0

    Reader getReader() {
        if(reader) {
            return reader
        }
        reader = new InputStreamReader(inputStream)
    }

    LineIterator getLineIterator() {
        if(lineIterator) return lineIterator

        lineIterator = new LineIterator(getReader())
    }

    @Override
    void close() {
        getLineIterator().close()
    }

    @Override
    protected List<String> computeNext() {
        if (getLineIterator().hasNext()) {
            def line = getLineIterator().nextLine()
            if (delimiter) {
                def splitLine = line.split(delimiter, delimitTill)

                return splitLine
            } else {
                return line.split()
            }
        }
        close()
        return endOfData()
    }
}
