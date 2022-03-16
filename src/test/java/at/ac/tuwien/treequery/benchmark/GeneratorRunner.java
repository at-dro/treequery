package at.ac.tuwien.treequery.benchmark;

import at.ac.tuwien.treequery.benchmark.QueryGenerator.Mode;
import at.ac.tuwien.treequery.query.QueryNode;
import at.ac.tuwien.treequery.subject.SubjectNode;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;
import at.ac.tuwien.treequery.xml.SubjectXmlConverter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalTime;

/**
 * This class executes the subject and query generation
 * <br>
 * A number of differently shaped subjects are generated for each size.
 * Positive queries of different sizes are then derived, with the query root node located at different depths of the subject.
 */
class GeneratorRunner {

    private final Path subjDir;
    private final Path queryDir;

    private final SubjectXmlConverter subjConverter;
    private final QueryXmlConverter queryConverter;

    private final SubjectGenerator subjGenerator;
    private final QueryGenerator queryGenerator;

    private final int subjSizeMin;
    private final int subjSizeMax;

    private final double[] subjRectRatios;
    private final int[] subjTriaFactors;
    private final int[] subjExpWidths;

    private final int queryStartCount;
    private final int[] querySizes;

    /**
     * Creates a new generator
     *
     * @param outDir The path to the output directory
     */
    public GeneratorRunner(Path outDir) {
        subjDir = outDir.resolve("subj");
        queryDir = outDir.resolve("query");

        subjConverter = new SubjectXmlConverter();
        queryConverter = new QueryXmlConverter();

        subjGenerator = new SubjectGenerator(2, 4, 0.1);
        queryGenerator = new QueryGenerator(0.9, 0.7, 0.3);

        subjSizeMin = 100;
        subjSizeMax = 1_000_000;

        subjRectRatios = new double[]{0.5, 2};
        subjTriaFactors = new int[]{2, 4};
        subjExpWidths = new int[]{3, 6};

        queryStartCount = 5;
        querySizes = new int[]{1, 5, 10, 20, 30};
    }

    /**
     * Runs the generation
     *
     * @throws IOException Thrown if a subject or query could not be stored
     */
    public void run() throws IOException {
        log("Generating subjects and queries...");
        createDirs();
        createSubjects();
        log("Done.");
    }

    private void log(String message) {
        System.out.println(LocalTime.now() + " " + message);
    }

    private void createDirs() throws IOException {
        Files.createDirectories(subjDir);
        Files.createDirectories(queryDir);
    }

    private void createSubjects() throws IOException {
        for (int size = subjSizeMin; size <= subjSizeMax; size *= 10) {
            String prefix = new DecimalFormat("0E0").format(size);

            for (double ratio : subjRectRatios) {
                handleSubject(prefix + "_rectangle_" + ratio, subjGenerator.rectangle(size, ratio));
            }

            for (int factor : subjTriaFactors) {
                handleSubject(prefix + "_triangle_" + factor, subjGenerator.triangle(size, factor));
            }

            for (int perNode : subjExpWidths) {
                handleSubject(prefix + "_exponential_" + perNode, subjGenerator.exponential(size, perNode));
            }
        }
    }

    private void handleSubject(String name, SubjectNode subject) throws IOException {
        storeSubject(name, subject);
        createQueries(name, subject);
    }

    private void createQueries(String name, SubjectNode subject) throws IOException {
        SubjectNodeWrapper wrapper = new SubjectNodeWrapper(subject);

        for (int i = 0; i < queryStartCount; i++) {
            int level = i * (wrapper.height - 1) / queryStartCount + 1;
            SubjectNode start = wrapper.getRandom(level, level, 0.75);
            SubjectNodeSelection selected = queryGenerator.generateSelection(start);

            for (int size : querySizes) {
                for (boolean setDirect : new boolean[]{true, false}) {
                    String prefix = String.format("%s_start%d_size%d_dir%s", name, i, size, setDirect);

                    // All subtrees
                    storeQuery(prefix + "_all_exact", queryGenerator.buildAllSubtrees(selected, Mode.EXACT, setDirect, size));
                    storeQuery(prefix + "_all_ordered", queryGenerator.buildAllSubtrees(selected, Mode.ORDERED, setDirect, size));
                    storeQuery(prefix + "_all_unordered", queryGenerator.buildAllSubtrees(selected, Mode.UNORDERED, setDirect, size));
                    storeQuery(prefix + "_all_any", queryGenerator.buildAllSubtrees(selected, Mode.ANY, setDirect, size));

                    // Selected subtrees
                    storeQuery(prefix + "_sel_ordered", queryGenerator.buildSelectedSubtrees(selected, Mode.ORDERED, setDirect, size));
                    storeQuery(prefix + "_sel_unordered", queryGenerator.buildSelectedSubtrees(selected, Mode.UNORDERED, setDirect, size));
                    storeQuery(prefix + "_sel_any", queryGenerator.buildSelectedSubtrees(selected, Mode.ANY, setDirect, size));

                    // Shuffled subtrees
                    storeQuery(prefix + "_shuffle_unordered", queryGenerator.buildShuffled(selected, Mode.UNORDERED, setDirect, size));
                    storeQuery(prefix + "_shuffle_any", queryGenerator.buildShuffled(selected, Mode.ANY, setDirect, size));
                }
            }
        }
    }

    private void storeSubject(String name, SubjectNode subject) throws IOException {
        OutputStream out = Files.newOutputStream(subjDir.resolve(name + ".xml"));
        subjConverter.export(subject, out, false);
    }

    private void storeQuery(String name, QueryNode query) throws IOException {
        if (query != null) {
            OutputStream out = Files.newOutputStream(queryDir.resolve(name + ".xml"));
            queryConverter.export(query, out, false);
        }
    }
}
