package at.ac.tuwien.treequery.matching;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class PropsMatchingTest extends XmlMatchingTest {

    private static final String SUBJECT = "subject_props";

    public static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(SUBJECT, "props/props01_pos", true),
                Arguments.of(SUBJECT, "props/props02_pos", true),
                Arguments.of(SUBJECT, "props/props03_pos", true),
                Arguments.of(SUBJECT, "props/props04_pos", true),
                Arguments.of(SUBJECT, "props/props05_pos", true),
                Arguments.of(SUBJECT, "props/props06_neg", false),
                Arguments.of(SUBJECT, "props/props07_neg", false),
                Arguments.of(SUBJECT, "props/props08_neg", false),
                Arguments.of(SUBJECT, "props/props09_neg", false),
                Arguments.of(SUBJECT, "props/props10_neg", false)
        );
    }
}
