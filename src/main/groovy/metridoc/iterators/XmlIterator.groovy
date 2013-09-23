package metridoc.iterators

import groovy.util.slurpersupport.GPathResult
import org.apache.camel.support.TokenXMLPairExpressionIterator
import groovy.util.slurpersupport.Node

/**
 * Built to convert large xml docs into an iterator
 * @author Tommy Barker
 */
class XmlIterator extends FileIterator {

    /**
     * beginning and end tag for an xml "record"
     */
    String tag
    String charSet = "utf-8"
    String inheritNamespaceToken
    Map namespaces = [:]
    private Iterator<String> xmlTokenPairIterator

    String getStartTag() {
        return "<$tag>"
    }

    String getEndTag() {
        return "</$tag>"
    }

    @Override
    protected Record computeNext() {
        if (!xmlTokenPairIterator) {
            initializeCamelIterator()
        }

        if (xmlTokenPairIterator.hasNext()) {
            def next = xmlTokenPairIterator.next()
            return convertToRecord(next)
        }

        endOfData()
    }

    Record convertToRecord(String xmlText) {
        GPathResult xmlResult
        if (namespaces) {
            xmlResult = new XmlSlurper().parseText(xmlText).declareNamespace(namespaces)
        }
        else {
            xmlResult = new XmlSlurper().parseText(xmlText)
        }
        Record record = new Record()
        xmlResult.childNodes().each {Node child ->
            record.body[child.name()] = child
        }
        record.body.root = xmlResult

        return record
    }

    void initializeCamelIterator() {
        assert tag: "tag must not be empty or null"
        assert inputStream: "file or stream has not been set"
        charSet = charSet ?: "utf-8" //just in case the user accidentally set it as null
        def iteratorCreator = new TokenXMLPairExpressionIterator(startTag, endTag, inheritNamespaceToken)
        xmlTokenPairIterator = iteratorCreator.createIterator(inputStream, charSet)
    }
}
