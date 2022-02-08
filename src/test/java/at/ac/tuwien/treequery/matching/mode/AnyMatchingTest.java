package at.ac.tuwien.treequery.matching.mode;

import at.ac.tuwien.treequery.matching.XmlMatchingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class AnyMatchingTest extends XmlMatchingTest {

    private static final String SUBJECT = "subject_modes";

    public static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(SUBJECT, "any/any01_pos", true),
                Arguments.of(SUBJECT, "any/any02_pos", true),
                Arguments.of(SUBJECT, "any/any03_pos", true),
                Arguments.of(SUBJECT, "any/any04_pos", true),
                Arguments.of(SUBJECT, "any/any05_pos", true),
                Arguments.of(SUBJECT, "any/any06_pos", true),
                Arguments.of(SUBJECT, "any/any07_pos", true),
                Arguments.of(SUBJECT, "any/any08_pos", true),
                Arguments.of(SUBJECT, "any/any09_pos", true),
                Arguments.of(SUBJECT, "any/any10_pos", true),
                Arguments.of(SUBJECT, "any/any11_pos", true),
                Arguments.of(SUBJECT, "any/any12_pos", true),
                Arguments.of(SUBJECT, "any/any13_pos", true),
                Arguments.of(SUBJECT, "any/any14_neg", false),
                Arguments.of(SUBJECT, "any/any15_neg", false)
        );
    }
}
