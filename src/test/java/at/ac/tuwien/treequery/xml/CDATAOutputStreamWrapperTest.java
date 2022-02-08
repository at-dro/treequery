package at.ac.tuwien.treequery.xml;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

class CDATAOutputStreamWrapperTest {

    @Test
    void cdataOutputTest() throws Exception {
        try (
                // Open resources
                InputStream wrong = getResource("xml/cdata/cdata_wrong.xml");
                InputStream expected = getResource("xml/cdata/cdata_correct.xml")
        ) {
            // Create output stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Print incorrect content to wrapped output stream
            wrong.transferTo(new CDATAOutputStreamWrapper(out));

            // Compare to expected result
            assertArrayEquals(expected.readAllBytes(), out.toByteArray());
        }
    }

    private InputStream getResource(String name) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(name);
        assertNotNull(in, "Resource " + name + " must not be null");
        return in;
    }
}