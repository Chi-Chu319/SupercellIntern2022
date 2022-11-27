package fi.intern.supercell.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.intern.supercell.processors.UserGraphProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Ex1Test {

    ObjectMapper mapper = new ObjectMapper();
    String dirPrefix = "src/test/java/resources/ex1";

    /**
     * Tests the graph processor
     *
     * @param inputFilename input filename
     * @param outputFilename output filename
     */
    void testProcessor(String inputFilename, String outputFilename) {
        UserGraphProcessor userGraphProcessor = new UserGraphProcessor(true);

        try {
            File inputFile = new File(dirPrefix, inputFilename);
            File outputFile = new File(dirPrefix, outputFilename);

            List<String> inputLines = Files.readAllLines(inputFile.toPath());
            List<String> outputLines = Files.readAllLines(outputFile.toPath());

            String processorOutput = userGraphProcessor.process(inputLines);
            String[] processorOutputLines = processorOutput.split(System.lineSeparator());

            int lineIndex = 0;
            for (String outputLine: outputLines) {
                String processedLine = processorOutputLines[lineIndex];

                Assertions.assertEquals(mapper.readTree(outputLine), mapper.readTree(processedLine));
                lineIndex++;
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("test input1")
    public void testInput1() {
        testProcessor("input1.txt", "output1.txt");
    }

    @Test
    @DisplayName("test input2")
    public void testInput2() {
        testProcessor("input2.txt", "output2.txt");
    }

    @Test
    @DisplayName("test input3")
    public void testInput3() {
        testProcessor("input3.txt", "output3.txt");
    }

    /**
     * Corner case test 1
     * Makes friend and delete, then perform update
     * Expect empty output
     */
    @Test
    @DisplayName("corner test input1")
    public void testCornerInput1() {
        testProcessor("cornerInput1.txt", "cornerOutput1.txt");
    }

    /**
     * Corner case test 2
     * Update when no friend
     * Make friend
     * Perform same update (same timestamp)
     * Expect empty output
     */
    @Test
    @DisplayName("corner test input2")
    public void testCornerInput2() {
        testProcessor("cornerInput2.txt", "cornerOutput2.txt");
    }

    /**
     * Corner case test 3
     * Update when no friend
     * Make friend
     * Perform same update (larger timestamp)
     * Expect update log
     */
    @Test
    @DisplayName("corner test input3")
    public void testCornerInput3() {
        testProcessor("cornerInput3.txt", "cornerOutput3.txt");
    }

    /**
     * Corner case test 3
     * Update when no friend
     * Make friend
     * Perform different update (same timestamp)
     * Expect update log
     */
    @Test
    @DisplayName("corner test input4")
    public void testCornerInput4() {
        testProcessor("cornerInput4.txt", "cornerOutput4.txt");
    }
}