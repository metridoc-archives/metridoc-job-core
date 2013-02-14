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


class DelimitedLineIterator extends FileIteratorCreator {

    org.apache.commons.io.LineIterator lineIterator
    String delimiter
    int delimitTill = 0

    @Override
    Iterator<List> doCreate(InputStream inputStream) {
        def lineIterator = new org.apache.commons.io.LineIterator(new InputStreamReader(inputStream))

        if (getParameters() && getParameters().containsKey('delimiter')) {
            delimiter = getParameters().get('delimiter')
        }
        return new DelimitedLineIterator(lineIterator: lineIterator, delimiter: delimiter, delimitTill: delimitTill)
    }

    void close() {
        lineIterator.close()
    }

    @Override
    List doNext() {
        if (lineIterator.hasNext()) {
            def line = lineIterator.nextLine()
            if (delimiter) {
                def splitLine = line.split(delimiter, delimitTill)

                return splitLine
            } else {
                line.split()
            }
        }
        return null
    }
}
