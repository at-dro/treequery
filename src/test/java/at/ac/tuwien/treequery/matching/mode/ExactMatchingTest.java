package at.ac.tuwien.treequery.matching.mode;

import at.ac.tuwien.treequery.matching.XmlMatchingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class ExactMatchingTest extends XmlMatchingTest {

    private static final String SUBJECT = "subject_modes";

    public static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(SUBJECT, "exact/exact01_pos", true),
                Arguments.of(SUBJECT, "exact/exact02_pos", true),
                Arguments.of(SUBJECT, "exact/exact03_pos", true),
                Arguments.of(SUBJECT, "exact/exact04_pos", true),
                Arguments.of(SUBJECT, "exact/exact05_neg", false),
                Arguments.of(SUBJECT, "exact/exact06_neg", false),
                Arguments.of(SUBJECT, "exact/exact07_neg", false),
                Arguments.of(SUBJECT, "exact/exact08_neg", false),
                Arguments.of(SUBJECT, "exact/exact09_neg", false),
                Arguments.of(SUBJECT, "exact/exact10_neg", false),
                Arguments.of(SUBJECT, "exact/exact11_neg", false),
                Arguments.of(SUBJECT, "exact/exact12_neg", false),
                Arguments.of(SUBJECT, "exact/exact13_neg", false),
                Arguments.of(SUBJECT, "exact/exact14_neg", false),
                Arguments.of(SUBJECT, "exact/exact15_neg", false)
        );
    }
}
