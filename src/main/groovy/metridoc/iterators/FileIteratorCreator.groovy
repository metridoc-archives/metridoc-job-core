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

import org.apache.camel.component.file.GenericFile
import metridoc.utils.IOUtils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/8/11
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class FileIteratorCreator extends DefaultIteratorCreator<GenericFile, List> implements Closeable {

    InputStream inputStream

    @Override
    Iterator<List> doCreate(GenericFile file) {
        InputStream inputStream = IOUtils.convertGenericFileToInputStream(file);
        doCreate(inputStream)
    }

    void close() {
        IOUtils.closeQuietly(inputStream)
    }

    abstract Iterator<List> doCreate(InputStream inputStream)
}
