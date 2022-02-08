package at.ac.tuwien.treequery.matching.mode;

import at.ac.tuwien.treequery.matching.XmlMatchingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class OptionalMatchingTest extends XmlMatchingTest {

    private static final String SUBJECT = "subject_modes";

    public static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(SUBJECT, "optional/optional01_pos", true),
                Arguments.of(SUBJECT, "optional/optional02_pos", true),
                Arguments.of(SUBJECT, "optional/optional03_pos", true),
                Arguments.of(SUBJECT, "optional/optional04_pos", true),
                Arguments.of(SUBJECT, "optional/optional05_pos", true),
                Arguments.of(SUBJECT, "optional/optional06_pos", true),
                Arguments.of(SUBJECT, "optional/optional07_pos", true),
                Arguments.of(SUBJECT, "optional/optional08_pos", true),
                Arguments.of(SUBJECT, "optional/optional09_pos", true),
                Arguments.of(SUBJECT, "optional/optional10_pos", true),
                Arguments.of(SUBJECT, "optional/optional11_pos", true),
                Arguments.of(SUBJECT, "optional/optional12_pos", true),
                Arguments.of(SUBJECT, "optional/optional13_pos", true),
                Arguments.of(SUBJECT, "optional/optional14_pos", true),
                Arguments.of(SUBJECT, "optional/optional15_pos", true)
        );
    }
}
