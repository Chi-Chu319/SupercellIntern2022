package fi.intern.supercell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Class for user graph representation, handles connections between users
 */
public class UserGraph {
    private int userLength;
    private final ArrayList<UserNode> users;
    private final Map<String, Integer> userSymbolTable;
    private final ObjectMapper mapper;

    /**
     * Constructor for user graph
     */
    UserGraph() {
        this.userLength = 0;
        this.users = new ArrayList<>();
        this.userSymbolTable = new HashMap<>();
        this.mapper = new ObjectMapper();
    }

    /**
     * Gets user index, create user if not exists
     *
     * @param user username
     */
    private int getUserIndex (String user) {
        if (userSymbolTable.get(user) != null) {
            return userSymbolTable.get(user);
        }

        userSymbolTable.put(user, userLength);
        this.users.add(new UserNode(user));

        return userLength++;
    }

    /**
     * Constructs log object
     *
     * @param user user
     * @param timestamp timestamp
     * @param friends friends
     * @param values values
     * @return log object
     * */
    private JsonNode constructLogObj (String user, int timestamp, LinkedList<String> friends, JsonNode values) {
        ObjectNode logObj = mapper.createObjectNode();
        ArrayNode friendsArray = mapper.valueToTree(friends.toArray());

        logObj.put("user", user);
        logObj.put("timestamp", timestamp);
        logObj.set("broadcast", friendsArray);
        logObj.set("values", values);
        return logObj;
    }

    /**
     * Makes two users friend
     *
     * @param user1 user 1
     * @param user2 user 2
     */
    public void makeFriend (String user1, String user2) {
        int user1Index = getUserIndex(user1);
        int user2Index = getUserIndex(user2);

        UserNode user1Node = users.get(user1Index);
        UserNode user2Node = users.get(user2Index);

        user1Node.addFriend(user2);
        user2Node.addFriend(user1);
    }

    /**
     * Deletes friend relationship
     *
     * @param user1 user 1
     * @param user2 user 2
     */
    public void deleteFriend (String user1, String user2) {
        int user1Index = getUserIndex(user1);
        int user2Index = getUserIndex(user2);

        UserNode user1Node = users.get(user1Index);
        UserNode user2Node = users.get(user2Index);

        user1Node.deleteFriend(user2);
        user2Node.deleteFriend(user1);
    }

    /**
     * Updates user and return the log object
     *
     * @param user user
     * @param timestamp timestamp
     * @param updatedValues updated values
     * @return log object
     */
    public JsonNode updateUser(String user, int timestamp, JsonNode updatedValues)  {
        ObjectNode loggedValues = this.mapper.createObjectNode();

        int userIndex = getUserIndex(user);
        UserNode userNode = this.users.get(userIndex);
        LinkedList<String> friends = userNode.getFriends();
        boolean hasFriends = friends.size() > 0;

        for (Iterator<Map.Entry<String, JsonNode>> it = updatedValues.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> field = it.next();

            if (userNode.updateValue(timestamp, field.getKey(), field.getValue().textValue()) && hasFriends) {
                loggedValues.put(field.getKey(), field.getValue().textValue());
            }
        }

        return hasFriends && !loggedValues.isEmpty() ?
                constructLogObj(user, timestamp, friends, loggedValues) :
                null;
    }

    /**
     * Gets all users' state in one json
     *
     * @return user states
     */
    public JsonNode getUserStates () {
        // TODO fix this by evaluating the double map solution
        ObjectNode loggedValues = this.mapper.createObjectNode();

        for (UserNode userNode : users) {
            Map<String, Pair<String, Integer>> userValues = userNode.getValues();
            ObjectNode jsonValues = this.mapper.convertValue(userValues, ObjectNode.class);

            for (Iterator<Map.Entry<String, JsonNode>> it = jsonValues.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> node = it.next();
                jsonValues.put(node.getKey(), userValues.get(node.getKey()).getLeft());
            }

            loggedValues.set(userNode.getName(), jsonValues);
        }

        return loggedValues;
    }
}
