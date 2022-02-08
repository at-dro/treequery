package at.ac.tuwien.treequery.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import at.ac.tuwien.treequery.query.nodes.QueryNode;
import at.ac.tuwien.treequery.query.state.MatchingState;
import at.ac.tuwien.treequery.subject.SubjectNode;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;
import at.ac.tuwien.treequery.xml.SubjectXmlConverter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class RefMatchingTest {

    private static final String SUBJECT = "subject_props";

    private final SubjectXmlConverter subjectConverter = new SubjectXmlConverter();
    private final QueryXmlConverter queryConverter = new QueryXmlConverter();

    @Test
    void ref01() throws Exception {
        List<MatchingState> result = findMatches(SUBJECT, "ref/ref01");

        assertEquals(2, result.size(), "Expected two different results");

        SubjectNode node1 = getRefNode(result.get(0), "refA");
        assertNodeType(node1, "a");
        assertNodeProp(node1, "p4", "v4");

        SubjectNode node2 = getRefNode(result.get(1), "refA");
        assertNodeType(node2, "a");
        assertNodeProp(node2, "p4", "v4");
        assertNodeProp(node2, "p5", "v5a");
    }

    @Test
    void ref02() throws Exception {
        List<MatchingState> result = findMatches(SUBJECT, "ref/ref02");

        assertEquals(7, result.size(), "Expected 7 different results");

        // Check all possible result combinations
        assertNode(result.get(0), "ref1", "b", "p2", "v2");
        assertNode(result.get(0), "ref2", "b", "p6", "v6a");
        assertNode(result.get(0), "ref3", "c", "p7", "v7");

        assertNode(result.get(1), "ref1", "b", "p2", "v2");
        assertNode(result.get(1), "ref2", "b", "p6", "v6a");
        assertNode(result.get(1), "ref3", "c", "p9", "v9");

        assertNode(result.get(2), "ref1", "b", "p6", "v6a");
        assertNodeNull(result.get(2), "ref2");
        assertNode(result.get(2), "ref3", "c", "p6", "v6b");

        assertNode(result.get(3), "ref1", "b", "p6", "v6a");
        assertNodeNull(result.get(3), "ref2");
        assertNode(result.get(3), "ref3", "c", "p7", "v7");

        assertNode(result.get(4), "ref1", "b", "p6", "v6a");
        assertNodeNull(result.get(4), "ref2");
        assertNode(result.get(4), "ref3", "c", "p9", "v9");

        assertNode(result.get(5), "ref1", "b", "p5", "v5b");
        assertNodeNull(result.get(5), "ref2");
        assertNode(result.get(5), "ref3", "c", "p7", "v7");

        assertNode(result.get(6), "ref1", "b", "p5", "v5b");
        assertNodeNull(result.get(6), "ref2");
        assertNode(result.get(6), "ref3", "c", "p9", "v9");
    }

    private List<MatchingState> findMatches(String subjectFile, String queryFile) throws Exception {
        SubjectNode subject = subjectConverter.parseResource("xml/subject/" + subjectFile + ".xml");
        QueryNode query = queryConverter.parseResource("xml/query/" + queryFile + ".xml");
        return query.findMatches(subject).collect(Collectors.toList());
    }

    private SubjectNode getRefNode(MatchingState state, String name) {
        return state.getReferences().getData().get(name);
    }

    private void assertNodeType(SubjectNode node, String type) {
        assertEquals(type, node.getType());
    }

    private void assertNodeProp(SubjectNode node, String key, String value) {
        assertEquals(value, node.getProperties().get(key));
    }

    private void assertNode(MatchingState state, String ref, String type, String key, String value) {
        SubjectNode node = getRefNode(state, ref);
        assertNodeType(node, type);
        assertNodeProp(node, key, value);
    }

    private void assertNodeNull(MatchingState state, String ref) {
        SubjectNode node = getRefNode(state, ref);
        assertNull(node, "Expected node for " + ref + " to be null");
    }
}
