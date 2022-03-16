package at.ac.tuwien.treequery.benchmark;

import at.ac.tuwien.treequery.query.QueryNode;
import at.ac.tuwien.treequery.subject.SubjectNode;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;
import at.ac.tuwien.treequery.xml.SubjectXmlConverter;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This class executes a benchmark and stores the result times
 */
class BenchmarkRunner {

    private final SubjectNode subject;
    private final QueryNode query;
    private final String csvPrefix;

    private final long initTime;
    private long warmupQueryTime;
    private long totalQueryTime;
    private int runCount;

    /**
     * Creates a new runner instance
     *
     * @param subjPath The path to the subject XML file
     * @param queryPath The path to the query XML file
     * @param csvPrefix The prefix for the output CSV line
     * @throws IOException Thrown if the subject or query could not be loaded
     */
    public BenchmarkRunner(String subjPath, String queryPath, String csvPrefix) throws IOException {
        this.csvPrefix = csvPrefix;

        long start = currentTime();
        subject = new SubjectXmlConverter().parseFile(subjPath);
        query = new QueryXmlConverter().parseFile(queryPath);
        initTime = currentTime() - start;
    }

    private long currentTime() {
        return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
    }

    /**
     * Runs the query a number of times
     *
     * @param maxRuns The maximum number of runs
     */
    public void runBenchmark(int maxRuns) {
        totalQueryTime = 0;
        runCount = 0;

        // Run once to warm up JVM
        warmupQueryTime = performQuery();

        // Run until number of runs is reached
        while (runCount < maxRuns) {
            totalQueryTime += performQuery();
            runCount++;
        }
    }

    private long performQuery() {
        long start = currentTime();
        boolean result = query.hasMatches(subject);
        if (!result) {
            // This should generally not happen, benchmark should only be executed on positive queries
            throw new IllegalStateException("Query failed!");
        }
        return currentTime() - start;
    }

    /**
     * Returns the results as CSV line
     *
     * @return A comma-separated string of values
     */
    public String toCsv() {
        // prefix,init time,warmup time,total query time,run count,average time,success
        return String.format(Locale.ENGLISH, "%s,%d,%d,%d,%d,%d,OK",
                csvPrefix,
                TimeUnit.NANOSECONDS.toMicros(initTime),
                TimeUnit.NANOSECONDS.toMicros(warmupQueryTime),
                TimeUnit.NANOSECONDS.toMicros(totalQueryTime),
                runCount,
                runCount > 0 ? TimeUnit.NANOSECONDS.toMicros(totalQueryTime / runCount) : 0
        );
    }
}
