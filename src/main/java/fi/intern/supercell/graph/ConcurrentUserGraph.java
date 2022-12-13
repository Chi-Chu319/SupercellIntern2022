package fi.intern.supercell.graph;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.Striped;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * Class for user graph representation, handles connections between users
 */
public class ConcurrentUserGraph extends AbstractUserGraph {
    private final Map<String, UserNode> userMap = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Striped<Lock> stripedLock = Striped.lock(200);

    /**
     * Constructor for user graph
     */
    public ConcurrentUserGraph() {
    }


    /**
     * Gets user node, create user if not exists
     *
     * @param user username
     */
    private UserNode getUser (String user) {
        UserNode foundUserNode = userMap.get(user);

        if (foundUserNode == null) {
            UserNode userNode = new UserNode(user);
            this.userMap.put(user, userNode);

            return userNode;
        }

        return foundUserNode;
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
        UserNode user1Node = this.getUser(user1);
        UserNode user2Node = this.getUser(user2);

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
        UserNode user1Node = this.getUser(user1);
        UserNode user2Node = this.getUser(user2);

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
        Lock lock = stripedLock.get(user);

        lock.lock();
        try {
            ObjectNode loggedValues = this.mapper.createObjectNode();

            UserNode userNode = this.getUser(user);
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
        } finally {
            lock.unlock();
        }

    }

    /**
     * Gets all users' state in one json
     *
     * @return user states
     */
    public JsonNode getUserStates () {
        ObjectNode loggedValues = this.mapper.createObjectNode();

        for (UserNode userNode : userMap.values()) {
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
