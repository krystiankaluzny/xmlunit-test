package org.xmlunit.test.test;

import org.junit.Test;

import static org.xmlunit.assertj.XmlAssert.assertThat;

public class AssertJBinaryCompatibilityTests {

    @Test
    public void contextLoads() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        //indirect test of assertThat(String actual)
        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveAttribute("attr");
        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveAttribute("attr");
    }

    @Test
    public void testIdentical() {
        String testXml = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        //indirect test of assertThat(T actual)
        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testContainsAllNodesHavingXPath_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .containsAllNodesHavingXPath("self::node()[@attr]")
                .containsAllNodesHavingXPath("./title");
    }

    @Test
    public void testValueByXPath_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" weight=\"66.6\"/>" +
                "</fruits>";

        //indirect test of assertThat(String actual)
        assertThat(xml).valueByXPath("//fruits/fruit/@weight");
    }

    @Test
    public void testAsInt_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\"/>" +
                "<fruit name=\"orange\"/>" +
                "<fruit name=\"banana\"/>" +
                "</fruits>";
        //indirect test of assertThat(int actual)
        assertThat(xml).valueByXPath("count(//fruits/fruit)").asInt().isEqualTo(3);
    }

    @Test
    public void testAsDouble_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" weight=\"66.6\"/>" +
                "</fruits>";

        //indirect test of assertThat(double actual)
        assertThat(xml).valueByXPath("//fruits/fruit/@weight").asDouble().isEqualTo(66.6);
    }

    @Test
    public void testAsBoolean_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" fresh=\"True\"/>" +
                "<fruit name=\"orange\" fresh=\"false\"/>" +
                "<fruit name=\"banana\" fresh=\"1\"/>" +
                "<fruit name=\"pear\" fresh=\"0\"/>" +
                "</fruits>";

        //indirect test of assertThat(boolean actual)
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"apple\"]/@fresh").asBoolean().isTrue();
    }
}
