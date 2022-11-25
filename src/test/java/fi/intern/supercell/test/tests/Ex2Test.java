package fi.intern.supercell.test.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.intern.supercell.UserGraphStateProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class Ex2Test {

    UserGraphStateProcessor userGraphStateProcessor;
    ObjectMapper mapper;
    String dirPrefix;

    @BeforeEach
    void setUp() {
        this.userGraphStateProcessor = new UserGraphStateProcessor(true);
        this.mapper = new ObjectMapper();
        this.dirPrefix = "src/test/java/resources/ex2";
    }

    /**
     * Tests the graph processor
     *
     * @param inputFilename input filename
     * @param outputFilename output filename
     */
    void testSequentialProcessor(String inputFilename, String outputFilename) {

        Path inputPath = Paths.get(dirPrefix, inputFilename);
        Path outputPath = Paths.get(dirPrefix, outputFilename);

        String processorOutput = userGraphStateProcessor.readSequential(inputPath.toString());

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> lines = Files.lines(outputPath, StandardCharsets.UTF_8)){
            lines.forEach(s -> contentBuilder.append(s).append("\n"));

            String outputContent = contentBuilder.toString();

            Assertions.assertEquals(mapper.readTree(processorOutput), mapper.readTree(outputContent));
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("test input1")
    void testInput1() {
        testSequentialProcessor("input1.txt", "output1.txt");
    }
}