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
 * User graph processor
 */
public class UserGraphProcessor {

    private final UserGraph userGraph;
    // TODO use CDI
    private final ObjectMapper mapper;
    private static final String UPDATE_TYPE = "update";
    private static final String MAKE_FRIEND_TYPE = "make_friends";
    private static final String DELETE_FRIEND_TYPE = "del_friends";

    public UserGraphProcessor() {
        this.userGraph = new UserGraph();
        this.mapper = new ObjectMapper();
    }

    /**
     * Processes update action
     *
     * @param action action
     * @return output log
     */
    private String processUpdateAction (JsonNode action) {
        String user = action.get("user").textValue();
        int timestamp = action.get("timestamp").intValue();
        JsonNode values = action.get("values");

        JsonNode logObj = userGraph.updateUser(user, timestamp, values);

        return logObj == null ? null : logObj.toString();
    }

    /**
     * Processes make friend action
     *
     * @param action action
     */
    private void processMakeFriendAction (JsonNode action) {
        String user1 = action.get("user1").textValue();
        String user2 = action.get("user2").textValue();

        userGraph.makeFriend(user1, user2);
    }

    /**
     * Processes delete friend action
     *
     * @param action action
     */
    private void processDeleteFriendAction (JsonNode action) {
        String user1 = action.get("user1").textValue();
        String user2 = action.get("user2").textValue();

        userGraph.deleteFriend(user1, user2);
    }

    /**
     * Processes action
     *
     * @param action action
     * @return action output
     * @throws IllegalArgumentException throws IllegalArgumentException
     */
    private String processAction(JsonNode action) throws IllegalArgumentException {
        if (action.get("type") == null) {
            throw new IllegalArgumentException("Null action type");
        }

        String actionType = action.get("type").textValue();
        if (Objects.equals(actionType, UPDATE_TYPE)) {
            return processUpdateAction(action);
        } else if (Objects.equals(actionType, MAKE_FRIEND_TYPE)) {
            processMakeFriendAction(action);
            return null;
        } else if (Objects.equals(actionType, DELETE_FRIEND_TYPE)) {
            processDeleteFriendAction(action);
            return null;
        }

        throw new IllegalArgumentException("Unknown action type");
    }

    /**
     * Reads from input, output the log in stdout and return it
     *
     * @param filename filename
     * @return output string
     * @throws RuntimeException throws RuntimeException
     */
    public String read(String filename) throws RuntimeException {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            StringBuilder outputBuilder = new StringBuilder();

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                JsonNode action = mapper.readValue(data, ObjectNode.class);

                Object actionOutput = processAction(action);
                if (actionOutput != null) {
                    outputBuilder.append(actionOutput);
                    outputBuilder.append(System.getProperty("line.separator"));
                }
            }
            myReader.close();

            String output = outputBuilder.toString();
            System.out.print(output);

            return output;
        } catch (FileNotFoundException | JsonProcessingException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
