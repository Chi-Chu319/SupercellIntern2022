package fi.intern.supercell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

/**
 * User graph processor track users states
 *
 * This is a solution class to question 2
 */
public class UserGraphStateProcessor {

    private final UserGraph userGraph;
    // TODO use CDI
    private final ObjectMapper mapper;
    private final boolean surpassLog;

    public UserGraphStateProcessor(boolean surpassLog) {
        this.userGraph = new UserGraph();
        this.mapper = new ObjectMapper();
        this.surpassLog = surpassLog;
    }

    public UserGraphStateProcessor() {
        this.userGraph = new UserGraph();
        this.mapper = new ObjectMapper();
        this.surpassLog = false;
    }

    /**
     * Processes update action
     *
     * @param action action
     * @throws IllegalArgumentException IllegalArgumentException
     */
    private void processUpdateAction (JsonNode action) throws IllegalArgumentException {
        if (action.get("type") == null || !Objects.equals(action.get("type").textValue(), "update")) {
            throw new IllegalArgumentException("Invalid action type");
        }

        String user = action.get("user").textValue();
        int timestamp = action.get("timestamp").intValue();
        JsonNode values = action.get("values");

        userGraph.updateUser(user, timestamp, values);
    }

    /**
     * Reads from input, stdout/outputs entire user state in a map (sequential)
     *
     * @param filename filename
     * @return output string
     * @throws RuntimeException throws RuntimeException
     */
    public String readSequential(String filename) throws RuntimeException {
        try {
            File myObj = new File(filename);
            Scanner fileReader = new Scanner(myObj);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                JsonNode action = mapper.readValue(line, ObjectNode.class);

                processUpdateAction(action);
            }
            fileReader.close();

            String output = userGraph.getUserStates().toString();
            if (!surpassLog) {
                System.out.print(output);
            }

            return output;
        } catch (FileNotFoundException | JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
