package at.ac.tuwien.treequery.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class XmlNodeTest {

    @Test
    void getName() throws Exception {
        XmlNode node = get("<a>\n    <b/>\n    <c/>\n</a>");
        assertEquals("a", node.getName());
    }

    @Test
    void getValueText() throws Exception {
        XmlNode node = get("<a>\n    foobar\n</a>");
        assertEquals(Optional.of("foobar"), node.getValue());
    }

    @Test
    void getValueCdata() throws Exception {
        XmlNode node = get("<a>\n    <![CDATA[  foobar\n ]]>\n</a>");
        assertEquals(Optional.of("  foobar\n "), node.getValue());
    }

    @Test
    void getValueCdataEmpty() throws Exception {
        XmlNode node = get("<a>\n    <![CDATA[]]>\n</a>");
        assertEquals(Optional.of(""), node.getValue());
    }

    @Test
    void getValueMixed() throws Exception {
        XmlNode node = get("<a>\n    foo\n    <![CDATA[bar]]>\n    <b>asdf</b>\n    <![CDATA[\n  baz  ]]>\n</a>");
        assertEquals(Optional.of("foobar\n  baz  "), node.getValue());
    }

    @Test
    void getValueNone() throws Exception {
        XmlNode node = get("<a>\n    <b>asdf</b>\n</a>");
        assertTrue(node.getValue().isEmpty(), "Expected value to be missing");
    }

    @Test
    void getNonNullValue() throws Exception {
        XmlNode node = get("<a>\n    foobar\n</a>");
        assertEquals("foobar", node.getNonNullValue());
    }

    @Test
    void getNonNullValueMissing() throws Exception {
        XmlNode node = get("<a>\n    <b>asdf</b>\n</a>");
        assertThrows(RuntimeException.class, node::getNonNullValue);
    }

    @Test
    void getAttributes() throws Exception {
        XmlNode node = get("<a att1=\"att1A\" att2=\"att2A\" att3=\"\">\n    <b att1=\"att1B\"/>\n</a>");
        Map<String, String> attributes = node.getAttributes();
        assertEquals(3, attributes.size());
        assertEquals("att1A", attributes.get("att1"));
        assertEquals("att2A", attributes.get("att2"));
        assertEquals("", attributes.get("att3"));
    }

    @Test
    void getAttribute() throws Exception {
        XmlNode node = get("<a att1=\"att1A\" att2=\"att2A\">\n    <b att1=\"att1B\"/>\n</a>");
        assertEquals(Optional.of("att1A"), node.getAttribute("att1"));
        assertEquals(Optional.of("att2A"), node.getAttribute("att2"));
    }

    @Test
    void getAttributeEmpty() throws Exception {
        XmlNode node = get("<a att1=\"\"/>");
        assertTrue(node.getAttribute("att1").isEmpty(), "Expected attribute to be missing");
    }

    @Test
    void getNonNullAttribute() throws Exception {
        XmlNode node = get("<a att1=\"val\" att2=\"foobar\"/>");
        assertEquals("val", node.getNonNullAttribute("att1"));
        assertEquals("foobar", node.getNonNullAttribute("att2"));
    }

    @Test
    void getNonNullAttributeMissing() throws Exception {
        XmlNode node = get("<a att1=\"foo\"/>");
        assertThrows(RuntimeException.class, () -> node.getNonNullAttribute("att2"));
    }

    @Test
    void getIntAttribute() throws Exception {
        XmlNode node = get("<a att1=\"42\" att2=\"1337\"/>");
        assertEquals(42, node.getIntAttribute("att1"));
        assertEquals(1337, node.getIntAttribute("att2"));
    }

    @Test
    void getIntAttributeMissing() throws Exception {
        XmlNode node = get("<a att1=\"42\" att2=\"1337\"/>");
        assertThrows(RuntimeException.class, () -> node.getIntAttribute("att3"));
    }

    @Test
    void getIntAttributeFormat() throws Exception {
        XmlNode node = get("<a att1=\"foobar\" att2=\"1337\"/>");
        assertThrows(NumberFormatException.class, () -> node.getIntAttribute("att1"));
    }

    @Test
    void getChildByName() throws Exception {
        XmlNode node = get("<a>\n    <b>\n        <c/>\n    </b>\n    <d/>\n    <b/>\n     <e/></a>");
        XmlNode child = node.getChild("b").orElse(null);
        assertNotNull(child);
        assertEquals("b", child.getName());
        assertEquals(1, child.getChildren().count());
    }

    @Test
    void getNestedChildByName() throws Exception {
        XmlNode node = get("<a>\n    <b>\n        <c/>\n    </b>\n    <d/>\n    <b/>\n     <e/></a>");
        assertTrue(node.getChild("c").isEmpty(), "Expected no child to be returned");
    }

    @Test
    void getNonExistentChildByName() throws Exception {
        XmlNode node = get("<a>\n    <b>\n        <c/>\n    </b>\n    <d/>\n    <b/>\n     <e/></a>");
        assertTrue(node.getChild("x").isEmpty(), "Expected no child to be returned");
    }

    @Test
    void getChildren() throws Exception {
        XmlNode node = get("<a>\n    <b>\n        <c/>\n    </b>\n    <d/>\n    <b/>\n     <e/></a>");
        List<XmlNode> children = node.getChildren().collect(Collectors.toList());
        assertEquals(4, children.size());
        assertEquals("b", children.get(0).getName());
        assertEquals("d", children.get(1).getName());
        assertEquals("b", children.get(2).getName());
        assertEquals("e", children.get(3).getName());
    }

    @Test
    void getEmptyChildren() throws Exception {
        XmlNode node = get("<a>foobar</a>");
        assertEquals(0, node.getChildren().count());
    }

    @Test
    void getChildrenByName() throws Exception {
        XmlNode node = get("<a>\n    <b>\n        <c/>\n    </b>\n    <d/>\n    <b/>\n     <e/></a>");
        List<XmlNode> children = node.getChildrenByName("b").collect(Collectors.toList());
        assertEquals(2, children.size());
        assertEquals("b", children.get(0).getName());
        assertEquals("b", children.get(1).getName());
    }

    @Test
    void getNonExistentChildren() throws Exception {
        XmlNode node = get("<a>\n    <b>\n        <c/>\n    </b>\n    <d/>\n    <b/>\n     <e/></a>");
        assertEquals(0, node.getChildrenByName("c").count());
    }

    private XmlNode get(String xml) throws Exception {
        InputSource source = new InputSource(new StringReader(xml));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        return new XmlNode(document.getDocumentElement());
    }
}