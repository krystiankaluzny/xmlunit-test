package org.xmlunit.test.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.ElementSelector;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.test.test.data.TestData;
import org.xmlunit.test.test.data.TestDataLoader;
import org.xmlunit.util.Predicate;

import java.util.Map;

import static org.xmlunit.diff.DifferenceEvaluators.Default;
import static org.xmlunit.diff.DifferenceEvaluators.chain;
import static org.xmlunit.diff.DifferenceEvaluators.downgradeDifferencesToEqual;

//https://github.com/xmlunit/xmlunit/issues/138
public class Issue138Tests {

    private static Map<String, TestData> allTestData;

    @BeforeClass
    public static void setUp() {
        allTestData = TestDataLoader.load("issue138.properties");
    }

    @Test
    public void test() {

        TestData data = allTestData.get("test1");

        //remove some noises
        DifferenceEvaluator de = chain(Default,
            downgradeDifferencesToEqual(ComparisonType.XML_STANDALONE),
            downgradeDifferencesToEqual(ComparisonType.NAMESPACE_PREFIX),
            downgradeDifferencesToEqual(ComparisonType.SCHEMA_LOCATION),
            downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_SEQUENCE)
        );

        ElementSelector elementSelector = ElementSelectors.conditionalBuilder()
            .whenElementIsNamed("requestProperties")
            .thenUse(ElementSelectors.byNameAndAttributesControlNS("name"))
            .elseUse(ElementSelectors.byName)
            .build();

        Diff diff = DiffBuilder.compare(data.controlXml)
            .withTest(data.testXml)
            .ignoreElementContentWhitespace()
            .withDifferenceEvaluator(de)
            .withComparisonController(ComparisonControllers.Default)
            .withNodeMatcher(new DefaultNodeMatcher(elementSelector))
            .build();

        for (Difference difference : diff.getDifferences()) {
            System.out.println(difference.toString());
        }
    }

    private final class AttributeFilter implements Predicate<Attr> {

        private final String attrName;

        private AttributeFilter(String attrName) {
            this.attrName = attrName;
        }

        @Override
        public boolean test(Attr toTest) {
            return !attrName.equalsIgnoreCase(toTest.getName());
        }
    }
}
