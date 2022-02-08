package at.ac.tuwien.treequery.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.ac.tuwien.treequery.query.nodes.QueryNode;
import at.ac.tuwien.treequery.subject.SubjectNode;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;
import at.ac.tuwien.treequery.xml.SubjectXmlConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class XmlMatchingTest {

    private final SubjectXmlConverter subjectConverter = new SubjectXmlConverter();
    private final QueryXmlConverter queryConverter = new QueryXmlConverter();

    @ParameterizedTest
    @MethodSource("cases")
    void runTestCase(String subjectFile, String queryFile, boolean expected) throws Exception {
        SubjectNode subject = subjectConverter.parseResource("xml/subject/" + subjectFile + ".xml");
        QueryNode query = queryConverter.parseResource("xml/query/" + queryFile + ".xml");
        assertEquals(expected, query.hasMatches(subject));
    }
}
