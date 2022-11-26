package fi.intern.supercell.test.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.intern.supercell.UserGraphStateProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class Ex2Test {

    UserGraphStateProcessor userGraphStateProcessor = new UserGraphStateProcessor(true);
    ObjectMapper mapper = new ObjectMapper();
    String dirPrefix = "src/test/java/resources/ex2";

    @BeforeEach
    void setUp() {
        this.userGraphStateProcessor.reset();
    }

    /**
     * Tests the graph sequential processor
     *
     * @param inputFilename input filename
     * @param outputFilename output filename
     */
    void testSequentialProcessor(String inputFilename, String outputFilename) {
        StringBuilder outputContentBuilder = new StringBuilder();

        try {
            File inputFile = new File(dirPrefix, inputFilename);
            File outputFile = new File(dirPrefix, outputFilename);

            List<String> inputLines = Files.readAllLines(inputFile.toPath());
            List<String> outputLines = Files.readAllLines(outputFile.toPath());

            long sequentialStart = System.currentTimeMillis();
            String processorOutput = userGraphStateProcessor.processSequential(inputLines);
            long sequentialFinish = System.currentTimeMillis();

            long sequentialTime = sequentialFinish - sequentialStart;
            System.out.printf("Sequential time %d%n", sequentialTime);

            outputLines.forEach(s -> outputContentBuilder.append(s).append("\n"));
            String outputContent = outputContentBuilder.toString();

            Assertions.assertEquals(mapper.readTree(processorOutput), mapper.readTree(outputContent));
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    /**
     * Tests the graph concurrent processor
     *
     * @param inputFilename input filename
     * @param outputFilename output filename
     */
    void testConcurrentProcessor(String inputFilename, String outputFilename) {
        StringBuilder outputContentBuilder = new StringBuilder();

        try {
            File inputFile = new File(dirPrefix, inputFilename);
            File outputFile = new File(dirPrefix, outputFilename);

            List<String> inputLines = Files.readAllLines(inputFile.toPath());
            List<String> outputLines = Files.readAllLines(outputFile.toPath());

            long concurrentStart = System.currentTimeMillis();
            String processorOutput = userGraphStateProcessor.processConcurrent(inputLines);
            long concurrentFinish = System.currentTimeMillis();

            long sequentialTime = concurrentFinish - concurrentStart;
            System.out.printf("Concurrent time %d%n", sequentialTime);

            outputLines.forEach(s -> outputContentBuilder.append(s).append("\n"));
            String outputContent = outputContentBuilder.toString();

            Assertions.assertEquals(mapper.readTree(processorOutput), mapper.readTree(outputContent));
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("test input1 sequential")
    void testInput1Sequential() {
        testSequentialProcessor("input1.txt", "output1.txt");
    }

//    TODO check for failure
    @Test
    @DisplayName("test input1 concurrent")
    void testInput1Concurrent() {
        testConcurrentProcessor("input1.txt", "output1.txt");
    }
}