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

import groovy.stream.Stream
import metridoc.writers.WriteResponse
import metridoc.writers.WrittenRecordStat
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.ResultSet

/**
 * @author Tommy Barker
 */
class StreamExtension {

    @SuppressWarnings("GroovyAccessibility")
    public static StreamResponse process(Stream self, Closure closure) {
        def response = new StreamResponse()
        Closure clone = closure.clone() as Closure

        def wrapped = self.wrapped
        Closure oldCondition = wrapped.condition
        boolean lastFilterInvalid = false
        if(oldCondition) {
            self.filter {value ->
                def result = oldCondition.call(value)
                if (!result) {
                    response.incrementIgnored()
                    lastFilterInvalid = true
                }
                else {
                    lastFilterInvalid = false
                }

                return result
            }
        }

        int count = 0
        int logEvery = getMetaProperty(self, "logEvery", Integer) ?: 0
        while (self.hasNext()) {
            def next = self.next()
            boolean valid = true
            Closure validator = getMetaProperty(self, "validator", Closure)
            if(validator) {
                validator.delegate = wrapped.using
                validator.resolveStrategy = Closure.DELEGATE_FIRST
                try {
                    def validatorResponse = validator.call(next)
                    if(validatorResponse instanceof Boolean) {
                        valid = validatorResponse
                    }
                }
                catch (AssertionError assertionError) {
                    getLogger(self).warn("invalid record: $assertionError.message")
                    valid = false
                }
            }
            if (valid) {
                clone.call(next)
                response.incrementWritten()
            }
            else {
                response.incrementInvalid()
            }

            if (logEvery != 0) {
                if(count != 0 && count % logEvery == 0) {
                    getLogger(self).info response.toString()
                }
            }
        }

        if(response.ignored > 1 && lastFilterInvalid) {
            response.decrementIgnored()
        }

        if (logEvery != 0) {
            getLogger(self).info response.toString()
        }
        return response
    }

    public static Stream validate(Stream self, Closure validator) {
        setMetaProperty(self, "validator", validator)
        return self
    }

    protected static <T> T getMetaProperty(Stream self, String propertyName, Class<T> aClass) {
        initializePropertyMapIfNotThere(self)
        Map propertyMap = self.metaPropertyMap
        def response = propertyMap[propertyName]
        if(response && aClass.isAssignableFrom(response.getClass())) {
            return response.asType(aClass)
        }

        return null
    }

    protected static void setMetaProperty(Stream self, String propertyName, propertyValue) {
        initializePropertyMapIfNotThere(self)
        Map propertyMap = self.metaPropertyMap
        propertyMap[propertyName] = propertyValue
    }

    private static void initializePropertyMapIfNotThere(Stream self) {
        def propertyMapExists = self.metaClass.respondsTo(self, "getMetaPropertyMap")
        if(!propertyMapExists) {
            def propertyMap = [:]
            self.metaClass.getMetaPropertyMap = {
                propertyMap
            }
        }
    }

    public static Stream logEvery(Stream self, int actionCountBeforeLogging) {
        setMetaProperty(self, "logEvery", actionCountBeforeLogging)
        return self
    }

    public static Stream setLogName(Stream self, String logName) {
        setMetaProperty(self, "logName", logName)
        return self
    }

    public static Logger getLogger(Stream self) {
        def logName = getMetaProperty(self, "logName", String) ?: "metridoc.stream"
        LoggerFactory.getLogger(logName)
    }
}
