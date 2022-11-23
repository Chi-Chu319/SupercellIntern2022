package fi.intern.supercell;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class for user node representation
 */
public class UserNode {
    public String name;
    private final LinkedList<String> adj;
    private final Map<String, Pair<String, Integer>> values;

    /**
     * Constructor for user node
     */
    UserNode (String name) {
        this.name = name;
        this.adj = new LinkedList<>();
        this.values = new HashMap<>();
    }

    /**
     * adds a friend
     *
     * @param friend friend
     */
    public void addFriend (String friend) {
        adj.addLast(friend);
    }

    /**
     * Deletes a friend
     *
     * @param friend friend
     */
    public void deleteFriend (String friend) {
        adj.remove(friend);
    }

    /**
     * Lists friends
     *
     * @return friend list
     */
    public LinkedList<String> getFriends () {
        return adj;
    }

    /**
     * Updates value
     *
     * @param timeStamp time stamp
     * @param key key
     * @param value value
     * @return true if updated, false if not updated
     */
    public boolean updateValue (int timeStamp, String key, String value) {
        Pair<String, Integer> currentValue = values.get(key);

        // if value not exists or value is outdated, updated
        if (currentValue == null || currentValue.getRight() < timeStamp) {
            values.put(key, Pair.of(value, timeStamp));
            return true;
        }

        return false;
    }
}
