package org.xmlunit.test.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlunit.diff.ByNameAndTextRecSelector;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.test.test.data.TestData;
import org.xmlunit.test.test.data.TestDataLoader;

import java.util.Collections;
import java.util.Map;

import static org.xmlunit.assertj.XmlAssert.assertThat;

//https://github.com/xmlunit/xmlunit/issues/143
public class Issue143Tests {

    private static Map<String, TestData> allTestData;

    @BeforeClass
    public static void setUp() {
        allTestData = TestDataLoader.load("issue143.properties");
    }

    @Test
    public void test() {

        TestData data = allTestData.get("test1");

        assertThat(data.testXml).and(data.controlXml)
            .withNodeMatcher(new DefaultNodeMatcher(
                ElementSelectors.selectorForElementNamed("a", ElementSelectors.byXPath("./b", new ByNameAndTextRecSelector())),
                new ByNameAndTextRecSelector(),
                ElementSelectors.byName))
            .areSimilar();
    }

    @Test
    public void testWithNamespaces() {

        TestData data = allTestData.get("withNamespace");

        Map<String, String> prefix2Uri = Collections.singletonMap("ns2", "http://test.xml");

        assertThat(data.testXml).and(data.controlXml)
            .withNodeMatcher(new DefaultNodeMatcher(
                ElementSelectors.selectorForElementNamed("a", ElementSelectors.byXPath("./ns2:b", prefix2Uri, new ByNameAndTextRecSelector())),
                new ByNameAndTextRecSelector(),
                ElementSelectors.byName))
            .areSimilar();
    }

    @Test
    public void testWithNamespaces2() {

        TestData data = allTestData.get("withNamespace2");

        Map<String, String> prefix2Uri = Collections.singletonMap("pref", "http://namespace.xml");

        assertThat(data.testXml).and(data.controlXml)
            .withNodeMatcher(new DefaultNodeMatcher(
                ElementSelectors.selectorForElementNamed("root", ElementSelectors.byXPath("./pref:a", prefix2Uri,ElementSelectors.byNameAndText)),
                ElementSelectors.byNameAndText))
            .areSimilar();
    }
}
