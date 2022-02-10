package at.ac.tuwien.treequery.matching.mode;

import at.ac.tuwien.treequery.matching.XmlMatchingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class OrderedMatchingTest extends XmlMatchingTest {

    private static final String SUBJECT = "subject_modes";

    public static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(SUBJECT, "ordered/ordered01_pos", true),
                Arguments.of(SUBJECT, "ordered/ordered02_pos", true),
                Arguments.of(SUBJECT, "ordered/ordered03_pos", true),
                Arguments.of(SUBJECT, "ordered/ordered04_pos", true),
                Arguments.of(SUBJECT, "ordered/ordered05_pos", true),
                Arguments.of(SUBJECT, "ordered/ordered06_pos", true),
                Arguments.of(SUBJECT, "ordered/ordered07_neg", false),
                Arguments.of(SUBJECT, "ordered/ordered08_neg", false),
                Arguments.of(SUBJECT, "ordered/ordered09_neg", false),
                Arguments.of(SUBJECT, "ordered/ordered10_neg", false),
                Arguments.of(SUBJECT, "ordered/ordered11_neg", false),
                Arguments.of(SUBJECT, "ordered/ordered12_neg", false),
                Arguments.of(SUBJECT, "ordered/ordered13_neg", false),
                Arguments.of(SUBJECT, "ordered/ordered14_neg", false),
                Arguments.of(SUBJECT, "ordered/ordered15_neg", false)
        );
    }
}
