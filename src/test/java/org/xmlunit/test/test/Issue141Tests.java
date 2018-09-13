package org.xmlunit.test.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
//import org.xmlunit.diff.Difference;
import org.custommonkey.xmlunit.Difference;
import org.xmlunit.test.test.data.TestData;
import org.xmlunit.test.test.data.TestDataLoader;

import junit.framework.Assert;

public class Issue141Tests {

    private static Map<String, TestData> allTestData;

    @BeforeClass
    public static void setUp() {
        allTestData = TestDataLoader.load("issue141.properties");
    }

    @Test
    public void test() {
        TestData simpleData = allTestData.get("test1");

        XmlProviderAssertions.assertEquivalentXml(simpleData.controlXml, simpleData.testXml, null, null);
    }

    public static class XmlProviderAssertions extends Assert {

        public static void assertEquivalentXml(String expectedXML, String testXML, String[] nodesWithOrderedChildren, String[] attributesToIgnore) {
            Set<String> setOfNodesWithOrderedChildren = new HashSet<String>();
            if(nodesWithOrderedChildren != null ) {
                Collections.addAll(setOfNodesWithOrderedChildren, nodesWithOrderedChildren);
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setCoalescing(true);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setIgnoringComments(true);
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                fail("Error testing XML");
            }

            Document expectedXMLDoc = null;
            Document testXMLDoc = null;
            try {
                expectedXMLDoc = db.parse(new ByteArrayInputStream(expectedXML.getBytes()));
                expectedXMLDoc.normalizeDocument();

                testXMLDoc = db.parse(new ByteArrayInputStream(testXML.getBytes()));
                testXMLDoc.normalizeDocument();
            } catch (SAXException e) {
                fail("Could not parse testXML");
            } catch (IOException e) {
                fail("Could not read testXML");
            }
            NodeList expectedChildNodes = expectedXMLDoc.getLastChild().getChildNodes();
            NodeList testChildNodes = testXMLDoc.getLastChild().getChildNodes();

            assertEquals("Test XML does not have expected amount of child nodes", expectedChildNodes.getLength(), testChildNodes.getLength());

            //compare parent nodes
            Document expectedDEDoc = getNodeAsDocument(expectedXMLDoc.getDocumentElement(), db, false);
            Document testDEDoc = getNodeAsDocument(testXMLDoc.getDocumentElement(), db, false);
            Diff diff = new Diff(expectedDEDoc, testDEDoc);
            assertTrue("Test XML parent node doesn't match expected XML parent node. " + diff.toString(), diff.similar());

            // compare child nodes
            for(int i=0; i < expectedChildNodes.getLength(); i++) {
                // expected child node
                Node expectedChildNode = expectedChildNodes.item(i);
                // skip text nodes
                if( expectedChildNode.getNodeType() == Node.TEXT_NODE ) {
                    continue;
                }
                // convert to document to use in Diff
                Document expectedChildDoc = getNodeAsDocument(expectedChildNode, db, true);

                boolean hasSimilar = false;
                StringBuilder  messages = new StringBuilder();

                for(int j=0; j < testChildNodes.getLength(); j++) {
                    // find child node in test xml
                    Node testChildNode = testChildNodes.item(j);
                    // skip text nodes
                    if( testChildNode.getNodeType() == Node.TEXT_NODE ) {
                        continue;
                    }
                    // create doc from node
                    Document testChildDoc = getNodeAsDocument(testChildNode, db, true);

                    diff = new Diff(expectedChildDoc, testChildDoc);
                    // if it doesn't contain order specific nodes, then use the elem and attribute qualifier, otherwise use the default
                    if( !setOfNodesWithOrderedChildren.contains( expectedChildDoc.getDocumentElement().getNodeName() ) ) {
                        diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
                    }
                    if(attributesToIgnore != null) {
                        diff.overrideDifferenceListener(new IgnoreNamedAttributesDifferenceListener(attributesToIgnore));
                    }
                    messages.append(diff.toString());
                    boolean similar = diff.similar();
                    if(similar) {
                        System.out.println("success::"+similar);
                        hasSimilar = true;
                    }
                }
                assertTrue("Test XML does not match expected XML. " + messages, hasSimilar);

            }
        }

        private static Document getNodeAsDocument(Node node, DocumentBuilder db, boolean deep) {
            // create doc from node
            Document nodeDoc = db.newDocument();
            Node importedNode = nodeDoc.importNode(node, deep);
            nodeDoc.appendChild(importedNode);
            return nodeDoc;
        }

    }

    static class IgnoreNamedAttributesDifferenceListener implements DifferenceListener {
        Set<String> attributeBlackList;

        public IgnoreNamedAttributesDifferenceListener(String[] attributeNames) {
            attributeBlackList = new HashSet<String>();
            Collections.addAll(attributeBlackList, attributeNames);
        }

        public int differenceFound(Difference difference) {
            int differenceId = difference.getId();
            if (differenceId == DifferenceConstants.ATTR_VALUE_ID) {
                if(attributeBlackList.contains(difference.getControlNodeDetail().getNode().getNodeName())) {
                    return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
            }

            return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
        }

        public void skippedComparison(Node node, Node node1) {
            // left empty
        }
    }
}
