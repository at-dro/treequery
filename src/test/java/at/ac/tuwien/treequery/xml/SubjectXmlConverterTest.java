package at.ac.tuwien.treequery.xml;

import static at.ac.tuwien.treequery.builder.SubjectNodeBuilder.type;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import at.ac.tuwien.treequery.subject.SubjectNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.stream.Stream;

class SubjectXmlConverterTest {

    private final SubjectXmlConverter converter = new SubjectXmlConverter();

    private static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of("subject_props", buildSubjectProps()),
                Arguments.of("subject_values", buildSubjectValues())
        );
    }

    private static SubjectNode buildSubjectProps() {
        return type("root")
                .child(type("a").prop("p1", "v1a").prop("p2", "v2")
                        .child(type("a").prop("p1", "v1b").prop("p3", "v3")
                                .child(type("a").prop("p4", "v4").build())
                                .build()
                        )
                        .child(type("b").prop("p2", "v2").prop("p3", "v3").prop("p5", "v5a")
                                .child(type("b").prop("p6", "v6a").build())
                                .child(type("c").prop("p6", "v6b").build())
                                .build()
                        )
                        .child(type("b").prop("p5", "v5b").build())
                        .child(type("c").prop("p7", "v7").prop("p8", "v8")
                                .child(type("a").prop("p4", "v4").prop("p5", "v5a").build())
                                .build()
                        )
                        .build()
                )
                .child(type("c").prop("p9", "v9").build())
                .build();
    }

    private static SubjectNode buildSubjectValues() {
        return type("root")
                .child(type("a").prop("p1", "va")
                        .child(type("b").prop("p1", "vb").prop("value", "1234").build())
                        .child(type("c").prop("p1", "vc").prop("value", "    ").build())
                        .child(type("d").prop("p1", "vd").prop("value", "\n").build())
                        .child(type("e").prop("p1", "ve").prop("value", "foo").build())
                        .child(type("f").prop("p1", "vf").build())
                        .child(type("g").prop("p1", "vg").prop("value", "").build())
                        .build()
                )
                .child(type("h").prop("value", "bar").build())
                .build();
    }

    @ParameterizedTest
    @MethodSource("cases")
    void parseSubject(String xmlFile, SubjectNode expected) throws Exception {
        // Parse from XML
        SubjectNode parsed = converter.parseResource("xml/subject/" + xmlFile + ".xml");

        // Compare the nodes
        assertNodesEqual(expected, parsed, 0, 0);
    }

    @ParameterizedTest
    @MethodSource("cases")
    void exportSubject(String expectedFile, SubjectNode subject) throws Exception {
        // Create output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Export subject to output stream
        converter.export(subject, out);

        try (InputStream expected = getSubjectXml(expectedFile)) {
            // Compare to expected result
            assertArrayEquals(expected.readAllBytes(), out.toByteArray());
        }
    }

    private InputStream getSubjectXml(String name) {
        InputStream in = getClass().getClassLoader().getResourceAsStream("xml/subject/" + name + ".xml");
        assertNotNull(in, "Subject XML resource " + name + " must not be null");
        return in;
    }

    private void assertNodesEqual(SubjectNode expected, SubjectNode actual, int depth, int index) {
        assertEquals(expected.getType(), actual.getType(),
                String.format("Expected types to match at depth %d, index %d", depth, index));
        assertEquals(expected.getProperties(), actual.getProperties(),
                String.format("Expected properties to match at depth %d, index %d", depth, index));

        assertEquals(expected.getChildren().size(), actual.getChildren().size(),
                String.format("Expected children count to match at depth %d, index %d", depth, index));

        for (int i = 0; i < expected.getChildren().size(); i++) {
            assertNodesEqual(expected.getChildren().get(i), actual.getChildren().get(i), depth + 1, i);
        }
    }
}
