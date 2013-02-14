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
package metridoc.camel


import org.apache.camel.language.groovy.CamelGroovyMethods

import metridoc.camel.aggregator.BodyAggregator
import metridoc.camel.aggregator.InflightAggregationWrapper
import org.apache.camel.Expression
import org.apache.camel.builder.ExpressionBuilder
import org.apache.camel.model.*
import metridoc.iterators.LineIterator

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/12/11
 * Time: 10:12 AM
 */
class CamelExtensions {

    /**
     * You can process exchanges using a Closure
     *
     * @param self
     * @param process
     * @return
     */
    static ProcessorDefinition process(ProcessorDefinition self, Closure process) {
        return self.process(new ClosureProcessor(process))
    }

    /**
     *
     * You can process a filter with a closure
     *
     * @param self
     * @param filter
     * @return
     */
    static FilterDefinition filter(ProcessorDefinition self, Closure filter) {
        return CamelGroovyMethods.filter(self, filter)
    }

    /**
     *
     * Process a condition with a closure
     *
     * @param self
     * @param filter
     * @return
     */
    static ChoiceDefinition when(ChoiceDefinition self, Closure filter) {
        return CamelGroovyMethods.when(self, filter)
    }

    /**
     * splits a text file by line
     *
     * @param self
     * @return
     */
    static SplitDefinition splitByLine(ProcessorDefinition self) {
        Expression bean = ExpressionBuilder.beanExpression(LineIterator.class, "create");
        return self.split(bean).streaming()
    }

    /**
     * Aggregates the body of a message with a default completion size of 500, and a completion timeout of 500 milliseconds
     *
     * @param self
     * @return
     */
    static AggregateDefinition aggregateBody(ProcessorDefinition self) {
        def expression = ExpressionBuilder.constantExpression(true)
        return self.aggregate(expression, new InflightAggregationWrapper(new BodyAggregator())).completionSize(500).completionTimeout(500)
    }

    /**
     * Aggregates the body of a message with a default completion timeout of 500 milliseconds
     * @param self
     * @param size aggregation size
     * @return
     */
    static AggregateDefinition aggregateBody(ProcessorDefinition self, int size) {
        def expression = ExpressionBuilder.constantExpression(true)
        return self.aggregate(expression, new InflightAggregationWrapper(new BodyAggregator())).completionSize(size).completionTimeout(500)
    }

    /**
     * Aggregates the body of a message with a default completion timeout of 500 milliseconds
     * @param self
     * @param size aggregation size
     * @param timeout aggregation timeout
     * @return
     */
    static AggregateDefinition aggregateBody(ProcessorDefinition self, int size, long timeout) {
        def expression = ExpressionBuilder.constantExpression(true)
        return self.aggregate(expression, new InflightAggregationWrapper(new BodyAggregator())).completionSize(size).completionTimeout(timeout)
    }

}
