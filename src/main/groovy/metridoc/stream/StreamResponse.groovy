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
/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class StreamResponse {
    private int ignored = 0
    private int written = 0
    private int invalid = 0

    void incrementWritten() {
        written++
    }

    void incrementIgnored() {
        ignored++
    }

    void incrementInvalid() {
        invalid++
    }

    void decrementWritten() {
        written--
    }

    void decrementIgnored() {
        ignored--
    }

    void decrementInvalid() {
        invalid--
    }

    int getIgnored() {
        return ignored
    }

    int getWritten() {
        return written
    }

    int getInvalid() {
        return invalid
    }

    int getTotal() {
        ignored + invalid + written
    }

    @Override
    public String toString() {
        return "StreamResponse{" +
                "ignored=" + ignored +
                ", written=" + written +
                ", invalid=" + invalid +
                ", total=" + total +
                '}';
    }
}
