package at.ac.tuwien.treequery.xml;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

class CDATAOutputStreamWrapperTest {

    private static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of("cdata01_input", "cdata01_expected"),
                Arguments.of("cdata02_input", "cdata02_expected")
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void cdataOutputTest(String inputFile, String expectedFile) throws Exception {
        try (
                // Open resources
                InputStream input = getResource(inputFile);
                InputStream expected = getResource(expectedFile)
        ) {
            // Create output stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStream wrapper = new CDATAOutputStreamWrapper(out);

            // Print incorrect content to wrapped output stream
            input.transferTo(wrapper);
            wrapper.flush();

            // Compare to expected result
            assertArrayEquals(expected.readAllBytes(), out.toByteArray());
        }
    }

    private InputStream getResource(String name) {
        InputStream in = getClass().getClassLoader().getResourceAsStream("xml/cdata/" + name + ".xml");
        assertNotNull(in, "Resource " + name + " must not be null");
        return in;
    }
}
