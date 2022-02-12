package at.ac.tuwien.treequery.xml;

import static at.ac.tuwien.treequery.builder.QueryNodeBuilder.container;
import static at.ac.tuwien.treequery.builder.QueryNodeBuilder.single;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import at.ac.tuwien.treequery.query.AllQueryNode;
import at.ac.tuwien.treequery.query.AnyQueryNode;
import at.ac.tuwien.treequery.query.ContainerQueryNode;
import at.ac.tuwien.treequery.query.QueryNode;
import at.ac.tuwien.treequery.query.SingleQueryNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

class QueryXmlConverterTest {

    private final QueryXmlConverter converter = new QueryXmlConverter();

    private static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of("export/export01", buildExport01()),
                Arguments.of("nested/nested01", buildNested01())
        );
    }

    private static QueryNode buildExport01() {
        return container()
                .child(single("b").ref("foo").prop("p2", "v2").prop("p3", "v3").children(container()
                        .child(single("b").prop("p6", "v6a").build())
                        .child(container()
                                .child(single("f").direct().ref("bar").build())
                                .exact()
                        )
                        .child(single("c").direct().prop("p6", "v6b").build())
                        .optional()
                ).build())
                .child(single("a").children(container()
                        .child(single("d").direct().build())
                        .ordered()
                ).build())
                .unordered();
    }

    private static QueryNode buildNested01() {
        return container()
                .child(single("a").direct().children(container()
                        .child(single("b").direct().build())
                        .child(container()
                                .child(single("c").direct().build())
                                .child(single("d").direct().build())
                                .ordered()
                        ).any()
                ).build())
                .ordered();
    }

    @ParameterizedTest
    @MethodSource("cases")
    void parseQuery(String xmlFile, QueryNode expected) throws Exception {
        // Parse from XML
        QueryNode parsed = converter.parseResource("xml/query/" + xmlFile + ".xml");

        // Compare the nodes
        assertNodesEqual(expected, parsed, 0, 0);
    }

    @ParameterizedTest
    @MethodSource("cases")
    void exportSubject(String expectedFile, QueryNode subject) throws Exception {
        // Create output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Export subject to output stream
        converter.export(subject, out);

        try (InputStream expected = getQueryXml(expectedFile)) {
            // Compare to expected result
            assertArrayEquals(expected.readAllBytes(), out.toByteArray());
        }
    }

    private InputStream getQueryXml(String name) {
        InputStream in = getClass().getClassLoader().getResourceAsStream("xml/query/" + name + ".xml");
        assertNotNull(in, "Query XML resource " + name + " must not be null");
        return in;
    }

    private void assertNodesEqual(QueryNode expected, QueryNode actual, int depth, int index) {
        // Check if node class is the expected
        assertEquals(expected.getClass(), actual.getClass(),
                String.format("Expected node class to match at depth %d, index %d", depth, index));

        if (actual instanceof ContainerQueryNode) {
            // Check additional properties of some container nodes
            if (actual instanceof AllQueryNode) {
                assertEquals(((AllQueryNode) expected).isOrdered(), ((AllQueryNode) actual).isOrdered(),
                        String.format("Expected ordered mode to match at depth %d, index %d", depth, index));
            } else if (actual instanceof AnyQueryNode) {
                assertEquals(((AnyQueryNode) expected).isOptional(), ((AnyQueryNode) actual).isOptional(),
                        String.format("Expected optional mode to match at depth %d, index %d", depth, index));
            }

            // Check list of children
            List<QueryNode> expectedChildren = ((ContainerQueryNode) expected).getChildren(),
                    actualChildren = ((ContainerQueryNode) actual).getChildren();

            assertEquals(expectedChildren.size(), actualChildren.size(),
                    String.format("Expected children count to match at depth %d, index %d", depth, index));

            for (int i = 0; i < expectedChildren.size(); i++) {
                assertNodesEqual(expectedChildren.get(i), actualChildren.get(i), depth + 1, i);
            }
        } else if (actual instanceof SingleQueryNode) {
            SingleQueryNode actualSingle = (SingleQueryNode) actual, expectedSingle = (SingleQueryNode) expected;

            assertEquals(expectedSingle.getType(), actualSingle.getType(),
                    String.format("Expected types to match at depth %d, index %d", depth, index));
            assertEquals(expectedSingle.getProperties(), actualSingle.getProperties(),
                    String.format("Expected properties to match at depth %d, index %d", depth, index));
            assertEquals(expectedSingle.isDirect(), actualSingle.isDirect(),
                    String.format("Expected direct mode to match at depth %d, index %d", depth, index));
            assertEquals(expectedSingle.getReference(), actualSingle.getReference(),
                    String.format("Expected reference name to match at depth %d, index %d", depth, index));

            assertNodesEqual(expectedSingle.getChildren(), actualSingle.getChildren(), depth + 1, 0);
        } else {
            // This should not happen, there are no other node types
            fail(String.format("Unexpected node type %s at depth %d, index %d", actual.getClass(), depth, index));
        }
    }
}
