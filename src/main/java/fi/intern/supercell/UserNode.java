package fi.intern.supercell;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class for user node representation, handles user values, adj table
 */
public class UserNode {
    private final String name;
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
     * Name getter
     *
     * @return username
     */
    public String getName () {
        return this.name;
    }

    /**
     * Name getter
     *
     * @return username
     */
    public Map<String, Pair<String, Integer>> getValues () {
        return this.values;
    }

    /**
     * Adds a friend
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
     * Updates value if the timestamp is outdated
     *
     * @param timeStamp time stamp
     * @param key key
     * @param value value
     * @return true if updated, false if not updated
     */
    public boolean updateValue (int timeStamp, String key, String value) {
        synchronized (this) {
            Pair<String, Integer> currentValue = values.get(key);

            // if value not exists or value is outdated, updated
            if (currentValue == null || currentValue.getRight() < timeStamp) {
                values.put(key, Pair.of(value, timeStamp));
                return true;
            }

            return false;
        }
    }
}
