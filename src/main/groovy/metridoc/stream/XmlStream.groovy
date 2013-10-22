package metridoc.stream

import groovy.util.slurpersupport.GPathResult
import org.apache.camel.support.TokenXMLPairExpressionIterator

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
class XmlStream extends FileStream<Map>{
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
    protected Map computeNext() {
        if (!xmlTokenPairIterator) {
            initializeCamelIterator()
        }

        if (xmlTokenPairIterator.hasNext()) {
            def next = xmlTokenPairIterator.next()
            return convertToMap(next)
        }

        endOfData()
    }

    Map convertToMap(String xmlText) {
        GPathResult xmlResult
        if (namespaces) {
            xmlResult = new XmlSlurper().parseText(xmlText).declareNamespace(namespaces)
        }
        else {
            xmlResult = new XmlSlurper().parseText(xmlText)
        }
        Map result = [:]
        xmlResult.childNodes().each {groovy.util.slurpersupport.Node child ->
            result[child.name()] = child
        }
        result.root = xmlResult

        return result
    }

    void initializeCamelIterator() {
        assert tag: "tag must not be empty or null"
        assert inputStream: "file or stream has not been set"
        charSet = charSet ?: "utf-8" //just in case the user accidentally set it as null
        def iteratorCreator = new TokenXMLPairExpressionIterator(startTag, endTag, inheritNamespaceToken)
        xmlTokenPairIterator = iteratorCreator.createIterator(inputStream, charSet)
    }
}
