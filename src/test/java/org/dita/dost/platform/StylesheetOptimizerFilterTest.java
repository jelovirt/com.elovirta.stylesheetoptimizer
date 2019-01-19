package org.dita.dost.platform;

import org.dita.dost.TestUtils;
import org.dita.dost.util.XMLUtils;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static javax.xml.XMLConstants.NULL_NS_URI;
import static org.dita.dost.platform.StylesheetOptimizerFilter.XSL_NS;
import static org.junit.Assert.assertEquals;

public class StylesheetOptimizerFilterTest {

    private final StylesheetOptimizerFilter f;

    public StylesheetOptimizerFilterTest() {
        f = new StylesheetOptimizerFilter(true);
        f.setLogger(new TestUtils.TestLogger());
    }

    @Test
    public void testBase() throws Exception {
        assertEquals("topic",
                f.optimizeAttributeValue("*[contains(@class, ' topic/topic ')]"));
    }

    @Test
    public void testStructuralSpecialization() throws Exception {
        assertEquals("*[contains(@class, ' task/task ')]",
                f.optimizeAttributeValue("*[contains(@class, ' task/task ')]"));
    }

    @Test
    public void testDomainSpecialization() throws Exception {
        assertEquals("ph[contains(@class, ' hi-d/b ')]",
                f.optimizeAttributeValue("*[contains(@class, ' hi-d/b ')]"));
    }

    // FIXME
    @Test
    public void testWhitespace() throws Exception {
        assertEquals(" * [ contains( @class ,  ' hi-d/b ' ) ] ",
                f.optimizeAttributeValue(" * [ contains( @class ,  ' hi-d/b ' ) ] "));
    }

    @Test
    public void testPredicate() throws Exception {
        assertEquals("ph[contains(@class, ' hi-d/b ')][exists(@id)]",
                f.optimizeAttributeValue("*[contains(@class, ' hi-d/b ')][exists(@id)]"));
    }

    @Test
    public void testSteps() throws Exception {
        assertEquals("topic/body",
                f.optimizeAttributeValue("*[contains(@class, ' topic/topic ')]/*[contains(@class, ' topic/body ')]"));
    }

    @Test
    public void testElementTypeDomainSpecialization() throws Exception {
        assertEquals("element(ph)",
                f.optimizeTypeAttributeValue("element(b)"));
    }

    @Test
    public void testElementTypeStructuralSpecialization() throws Exception {
        assertEquals("element(strow)",
                f.optimizeTypeAttributeValue("element(property)"));
    }

    // FIXME
    @Test
    public void testElementTypeWhitespace() throws Exception {
        assertEquals(" element ( b ) ",
                f.optimizeTypeAttributeValue(" element ( b ) "));
    }

    @Test
    public void testContentHandler() throws SAXException {
        f.setContentHandler(new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName,
                                     String qName, Attributes attributes) throws SAXException {
                assertEquals("ph", localName);
            }
        });
        f.startElement(NULL_NS_URI, "b", "b",
                new XMLUtils.AttributesBuilder()
                        .add("class", "+ topic/ph hi-d/b ")
                        .build());
    }

    @Test
    public void testContentHandlerHtml() throws SAXException {
        f.setContentHandler(new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName,
                                     String qName, Attributes attributes) throws SAXException {
                assertEquals("b", localName);
            }
        });
        f.startElement(NULL_NS_URI, "b", "b",
                new XMLUtils.AttributesBuilder()
                        .add("class", "html5")
                        .build());
    }

    @Test
    public void testContentHandlerExpression() throws SAXException {
        f.setContentHandler(new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName,
                                     String qName, Attributes attributes) throws SAXException {
                assertEquals("ph[contains(@class, ' hi-d/b ')]", attributes.getValue("select"));
            }
        });
        f.startElement(XSL_NS, "value-of", "xsl:value-of",
                new XMLUtils.AttributesBuilder()
                        .add("select", "*[contains(@class, ' hi-d/b ')]")
                        .build());
    }

    @Test
    public void testContentHandlerType() throws SAXException {
        f.setContentHandler(new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName,
                                     String qName, Attributes attributes) throws SAXException {
                assertEquals("element(ph)", attributes.getValue("as"));
            }
        });
        f.startElement(XSL_NS, "value-of", "xsl:value-of",
                new XMLUtils.AttributesBuilder()
                        .add("as", "element(b)")
                        .build());
    }

}