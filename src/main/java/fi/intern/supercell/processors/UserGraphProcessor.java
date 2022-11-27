package fi.intern.supercell.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.intern.supercell.graph.UserGraph;

import java.util.List;
import java.util.Objects;

/**
 * User graph processor broadcasts updates in user states to friends
 *
 * This is a solution class to question 1
 */
public class UserGraphProcessor {

    private UserGraph userGraph = new UserGraph();
    private final ObjectMapper mapper = new ObjectMapper();
    private final boolean surpassLog;
    private static final String UPDATE_TYPE = "update";
    private static final String MAKE_FRIEND_TYPE = "make_friends";
    private static final String DELETE_FRIEND_TYPE = "del_friends";

    public UserGraphProcessor(boolean surpassLog) {
        this.surpassLog = surpassLog;
    }

    public UserGraphProcessor() {
        this.surpassLog = false;
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
     * Resets the graph states
     */
    public void reset () {
        this.userGraph = new UserGraph();
    }

    /**
     * process the input, output the log in stdout and return it
     *
     * @param lines lines
     * @return output string
     */
    public String process(List<String> lines) {
        try {
            StringBuilder outputBuilder = new StringBuilder();

            for (String line: lines) {
                JsonNode action = mapper.readValue(line, ObjectNode.class);
                Object actionOutput = processAction(action);

                if (actionOutput != null) {
                    outputBuilder.append(actionOutput);
                    outputBuilder.append(System.getProperty("line.separator"));
                }
            }

            String output = outputBuilder.toString();
            if (!surpassLog) {
                System.out.print(output);
            }

            return output;
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
