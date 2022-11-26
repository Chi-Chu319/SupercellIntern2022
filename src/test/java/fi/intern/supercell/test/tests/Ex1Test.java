package fi.intern.supercell.test.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.intern.supercell.UserGraphProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class Ex1Test {

    private final UserGraphProcessor userGraphProcessor = new UserGraphProcessor(true);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String dirPrefix = "src/test/java/resources/ex1";

    @BeforeEach
    void setUp() {
        this.userGraphProcessor.reset();
    }

    /**
     * Tests the graph processor
     *
     * @param inputFilename input filename
     * @param outputFilename output filename
     */
    void testProcessor(String inputFilename, String outputFilename) {
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
    void testInput1() {
        testProcessor("input1.txt", "output1.txt");
    }

    @Test
    @DisplayName("test input2")
    void testInput2() {
        testProcessor("input2.txt", "output2.txt");
    }

    @Test
    @DisplayName("test input3")
    void testInput3() {
        testProcessor("input3.txt", "output3.txt");
    }
}