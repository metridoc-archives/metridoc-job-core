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

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVParser

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 11/28/11
 * Time: 3:37 PM
 *
 * Handles iterating over csv files
 * The allowed parameters (which can be retrieved by calling {@link CsvIterator#getParameters} must be a property in
 * {@link CSVParser}, otherwise an exception will occur.
 *
 *
 */
class CsvIterator extends FileIteratorCreator {

    CSVReader csvReader

    @Override
    Iterator<List> doCreate(InputStream inputStream) {
        def reader = new InputStreamReader(inputStream)
        def csvReader = new CSVReader(reader)
        if (getParameters()) {
            csvReader.parser = new CSVParser(getParameters())
        }

        return new CsvIterator(csvReader: csvReader)
    }

    @Override
    List doNext() {
        def next = csvReader.readNext()
        next == null ? null : Arrays.asList(next)
    }
}

