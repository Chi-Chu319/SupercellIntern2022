package fi.intern.supercell.test.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.intern.supercell.UserGraphProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

class Ex1Test {

    UserGraphProcessor userGraphProcessor;
    ObjectMapper mapper;
    String dirPrefix;

    @BeforeEach
    void setUp() {
        this.userGraphProcessor = new UserGraphProcessor(true);
        this.mapper = new ObjectMapper();
        this.dirPrefix = "src/test/java/resources/ex1";
    }

    /**
     * Tests the graph processor
     *
     * @param inputFilename input filename
     * @param outputFilename output filename
     * @throws JsonMappingException JsonMappingException
     * @throws RuntimeException RuntimeException
     * @throws FileNotFoundException FileNotFoundException
     */
    void testProcessor(String inputFilename, String outputFilename) throws FileNotFoundException, RuntimeException, JsonProcessingException {
        try {
            Path inputPath = Paths.get(dirPrefix, inputFilename);
            File outputFile = new File(dirPrefix, outputFilename);

            String processorOutput = userGraphProcessor.read(inputPath.toString());
            String[] processorOutputLines = processorOutput.split(System.lineSeparator());
            Scanner fileReader = new Scanner(outputFile);

            int lineIndex = 0;
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String processedLine = processorOutputLines[lineIndex];

                Assertions.assertEquals(mapper.readTree(line), mapper.readTree(processedLine));
                lineIndex++;
            }
            fileReader.close();

        } catch (FileNotFoundException | RuntimeException | JsonProcessingException e) {
            throw e;
        }
    }

    @Test
    @DisplayName("test input1")
    void testInput1() throws FileNotFoundException, RuntimeException, JsonProcessingException {
        try {
            testProcessor("input1.txt", "output1.txt");
        } catch (FileNotFoundException | RuntimeException | JsonProcessingException e) {
            throw e;
        }
    }

    @Test
    @DisplayName("test input2")
    void testInput2() throws FileNotFoundException, RuntimeException, JsonProcessingException {
        try {
            testProcessor("input2.txt", "output2.txt");
        } catch (FileNotFoundException | RuntimeException | JsonProcessingException e) {
            throw e;
        }
    }

    @Test
    @DisplayName("test input3")
    void testInput3() throws FileNotFoundException, RuntimeException, JsonProcessingException {
        try {
            testProcessor("input3.txt", "output3.txt");
        } catch (FileNotFoundException | RuntimeException | JsonProcessingException e) {
            throw e;
        }
    }
}