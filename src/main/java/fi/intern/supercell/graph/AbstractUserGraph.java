package fi.intern.supercell.graph;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Abstract class for user graph
 */
public abstract class AbstractUserGraph {
    abstract void makeFriend (String user1, String user2);

    abstract void deleteFriend (String user1, String user2);

    public abstract JsonNode updateUser(String user, int timestamp, JsonNode updatedValues);

    abstract JsonNode getUserStates ();
}
