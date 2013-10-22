/*
 * Copyright 2013 Trustees of the University of Pennsylvania Licensed under the
 * 	Educational Community License, Version 2.0 (the "License"); you may
 * 	not use this file except in compliance with the License. You may
 * 	obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * 	Unless required by applicable law or agreed to in writing,
 * 	software distributed under the License is distributed on an "AS IS"
 * 	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * 	or implied. See the License for the specific language governing
 * 	permissions and limitations under the License.
 */



package metridoc.stream

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class StreamExtensionSpec extends Specification {

    void "test iterator mapping"() {
        when:
        StreamResponse result = [0,1,2,3].toStream().map {
            it * 2
        }.process{}

        then:
        4 == result.written
        4 == result.total
    }

    void "test filtering"() {
        when:
        StreamResponse result = [0,1,2,3].toStream().filter {
            it % 2 == 0
        }.process{}

        then:
        2 == result.written
        4 == result.total
        2 == result.ignored
    }

    void "test validation"() {
        when:
        StreamResponse result = [0,1,2,3,4].toStream().filter {
            it % 2 == 0
        }.map{it * 2}.validate {assert it != 8}.process{}

        then:
        2 == result.written
        5 == result.total
        2 == result.ignored
        1 == result.invalid
    }
}
