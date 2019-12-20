package org.xmlunit.test.test.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Read resource file and parse test data.
 * Your data should be hold in properties naming as follow:
 * <b>TEST_NAME.testXml</b>
 * <b>TEST_NAME.controlXml</b>
 * eg
 * <pre>
 * mySuperExtraTest.testXml=&lt;abc&gt;&lt;xyz&gt;&lt;/xyz&gt;&lt;/abc&gt;
 * mySuperExtraTest.controlXml=&lt;abc&gt;&lt;xyz&gt;&lt;/xyz&gt;&lt;/abc&gt;
 * </pre>
 *
 * If you want prevent removing leading whitespaces, you should start new line with ||.
 */
public class TestDataLoader {

    private static String testKey = "testXml";
    private static String controlKey = "controlXml";
    private static String startLine = "||"; //starts with no whitespaces character prevent leading whitespace skipping

    public static Map<String, TestData> load(String resourceFileName) {

        try (InputStream input = TestDataLoader.class.getClassLoader().getResourceAsStream(resourceFileName)) {

            Map<String, TestData> result = new HashMap<>();
            Properties properties = new Properties();

            properties.load(input);

            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String[] split = key.split("\\.");

                if (split.length == 2) {
                    String xml = ((String) entry.getValue()).replace(startLine, "");

                    TestData testData = result.computeIfAbsent(split[0], s -> new TestData());
                    if (testKey.equalsIgnoreCase(split[1])) {
                        testData.testXml = xml;
                    }
                    else if (controlKey.equalsIgnoreCase(split[1])) {
                        testData.controlXml = xml;
                    }
                    else {
                        System.out.printf("WARN: Not found \"%s\" or \"%s\" in \"%s\" property key", testKey, controlKey, key);
                    }
                }
            }

            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyMap();
    }
}
