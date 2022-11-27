package fi.intern.supercell.test.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.intern.supercell.processors.UserGraphStateProcessor;
import fi.intern.supercell.test.TestGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BenchmarkTest {

    static final ObjectMapper mapper = new ObjectMapper();
    final static int[] userCounts = new int[] {40, 80 ,160};
    final static int[] actionsPerUser = new int[] {2000, 4000, 8000};
    final static int[] parallelisms = new int[] {2, 4, 8};
    final static UserGraphStateProcessor userGraphStateProcessor = new UserGraphStateProcessor(true);

    /**
     * Logs test start info
     *
     * @param userCount user count
     * @param actionPerUser action per user
     * @param parallelism parallelism
     */
    private void logTestStart (int userCount, int actionPerUser, int parallelism) {
        StringBuilder contentBuilder = new StringBuilder();

        contentBuilder.append(String.format("Benchmark test on %d users, %d update actions per user, parallelism: %d", userCount, actionPerUser, parallelism));
        contentBuilder.append(System.lineSeparator());
        contentBuilder.append(System.lineSeparator());

        System.out.print(contentBuilder);
    }

    /**
     * Logs test summary
     *
     * @param parallelism parallelism
     * @param sequentialTime sequential runtime
     * @param concurrentTime concurrent runtime
     */
    private void logTestSummary (int parallelism, long sequentialTime, long concurrentTime) {
        double factor = (sequentialTime * 1.0) / (concurrentTime * 1.0);
        StringBuilder contentBuilder = new StringBuilder();

        contentBuilder.append(String.format("Sequential processor runtime: %d ms", sequentialTime));
        contentBuilder.append(System.lineSeparator());
        contentBuilder.append(String.format("Concurrent processor runtime: %d ms", concurrentTime));
        contentBuilder.append(System.lineSeparator());
        contentBuilder.append(String.format("Boost factor is %f with parallelism %d", factor, parallelism));
        contentBuilder.append(System.lineSeparator());
        contentBuilder.append("-".repeat(20));
        contentBuilder.append(System.lineSeparator());

        System.out.print(contentBuilder);
    }

    /**
     * Perform individual test of a benchmark test
     *
     * @param userCount user count
     * @param actionPerUser action per user
     * @param parallelism parallelism
     */
    private void individualTest (int userCount, int actionPerUser, int parallelism) {
        try {
            logTestStart(userCount, actionPerUser, parallelism);
            List<String> testLines = TestGenerator.generateStateUpdates(userCount, actionPerUser);

            userGraphStateProcessor.reset();
            userGraphStateProcessor.setParallelism(parallelism);

            long sequentialStart = System.currentTimeMillis();
            String sequentialOutput = userGraphStateProcessor.processSequential(testLines);
            long sequentialFinish = System.currentTimeMillis();

            userGraphStateProcessor.reset();

            long concurrentStart = System.currentTimeMillis();
            String concurrentOutput = userGraphStateProcessor.processConcurrent(testLines);
            long concurrentFinish = System.currentTimeMillis();

            Assertions.assertEquals(mapper.readTree(sequentialOutput), mapper.readTree(concurrentOutput));

            long sequentialTime = sequentialFinish - sequentialStart;
            long concurrentTime = concurrentFinish - concurrentStart;
            logTestSummary(parallelism, sequentialTime, concurrentTime);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("benchmark test")
    void benchmarkTest () {
        for (int userCount : userCounts) {
            for (int actionPerUser : actionsPerUser) {
                for (int parallelism : parallelisms) {
                    this.individualTest(userCount, actionPerUser, parallelism);
                }
            }
        }
    }
}
