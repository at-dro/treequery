package at.ac.tuwien.treequery.matching;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class RealMatchingTest extends XmlMatchingTest {

    public static Stream<Arguments> cases() {
        return Stream.of(
                // Subject 01
                Arguments.of("subject_real01", "real/real01", false),
                Arguments.of("subject_real01", "real/real02", false),
                Arguments.of("subject_real01", "real/real03", true),
                Arguments.of("subject_real01", "real/real04", true),
                Arguments.of("subject_real01", "real/real05", true),
                Arguments.of("subject_real01", "real/real06", true),
                Arguments.of("subject_real01", "real/real07", false),
                Arguments.of("subject_real01", "real/real08", true),
                Arguments.of("subject_real01", "real/real09", true),
                Arguments.of("subject_real01", "real/real10", false),
                Arguments.of("subject_real01", "real/real11", false),
                Arguments.of("subject_real01", "real/real12", true),
                Arguments.of("subject_real01", "real/real13", true),
                Arguments.of("subject_real01", "real/real14", true),
                Arguments.of("subject_real01", "real/real15", true),
                // Subject 02
                Arguments.of("subject_real02", "real/real01", false),
                Arguments.of("subject_real02", "real/real02", false),
                Arguments.of("subject_real02", "real/real03", false),
                Arguments.of("subject_real02", "real/real04", true),
                Arguments.of("subject_real02", "real/real05", false),
                Arguments.of("subject_real02", "real/real06", true),
                Arguments.of("subject_real02", "real/real07", false),
                Arguments.of("subject_real02", "real/real08", true),
                Arguments.of("subject_real02", "real/real09", true),
                Arguments.of("subject_real02", "real/real10", true),
                Arguments.of("subject_real02", "real/real11", false),
                Arguments.of("subject_real02", "real/real12", false),
                Arguments.of("subject_real02", "real/real13", false),
                Arguments.of("subject_real02", "real/real14", true),
                Arguments.of("subject_real02", "real/real15", false),
                // Subject 02
                Arguments.of("subject_real03", "real/real01", true),
                Arguments.of("subject_real03", "real/real02", true),
                Arguments.of("subject_real03", "real/real03", true),
                Arguments.of("subject_real03", "real/real04", true),
                Arguments.of("subject_real03", "real/real05", true),
                Arguments.of("subject_real03", "real/real06", true),
                Arguments.of("subject_real03", "real/real07", false),
                Arguments.of("subject_real03", "real/real08", true),
                Arguments.of("subject_real03", "real/real09", true),
                Arguments.of("subject_real03", "real/real10", false),
                Arguments.of("subject_real03", "real/real11", true),
                Arguments.of("subject_real03", "real/real12", true),
                Arguments.of("subject_real03", "real/real13", true),
                Arguments.of("subject_real03", "real/real14", true),
                Arguments.of("subject_real03", "real/real15", true)
        );
    }
}
