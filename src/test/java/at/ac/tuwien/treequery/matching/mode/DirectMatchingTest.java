package at.ac.tuwien.treequery.matching.mode;

import at.ac.tuwien.treequery.matching.XmlMatchingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class DirectMatchingTest extends XmlMatchingTest {

    private static final String SUBJECT = "subject_modes";

    public static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(SUBJECT, "direct/direct01_pos", true),
                Arguments.of(SUBJECT, "direct/direct02_pos", true),
                Arguments.of(SUBJECT, "direct/direct03_neg", false),
                Arguments.of(SUBJECT, "direct/direct04_neg", false),
                Arguments.of(SUBJECT, "direct/direct05_neg", false),
                Arguments.of(SUBJECT, "direct/direct06_pos", true),
                Arguments.of(SUBJECT, "direct/direct16_pos", true),
                Arguments.of(SUBJECT, "direct/direct17_pos", true)
        );
    }
}
