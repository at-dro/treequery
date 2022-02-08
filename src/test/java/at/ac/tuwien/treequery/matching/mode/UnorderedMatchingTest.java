package at.ac.tuwien.treequery.matching.mode;

import at.ac.tuwien.treequery.matching.XmlMatchingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class UnorderedMatchingTest extends XmlMatchingTest {

    private static final String SUBJECT = "subject_modes";

    public static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(SUBJECT, "unordered/unordered01_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered02_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered03_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered04_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered05_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered06_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered07_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered08_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered09_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered10_pos", true),
                Arguments.of(SUBJECT, "unordered/unordered11_neg", false),
                Arguments.of(SUBJECT, "unordered/unordered12_neg", false),
                Arguments.of(SUBJECT, "unordered/unordered13_neg", false),
                Arguments.of(SUBJECT, "unordered/unordered14_neg", false),
                Arguments.of(SUBJECT, "unordered/unordered15_neg", false)
        );
    }
}
