package fi.intern.supercell.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Test case generator
 */
public class TestGenerator {
    private static final int randomValueCount = 10;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    /**
     * Generates a random key set
     */
    private static String[] getRandomKeys () {
        String[] randomKeys = new String[randomValueCount];

        for (int i = 0; i < randomValueCount; i++) {
            randomKeys[i] = UUID.randomUUID().toString();
        }

        return randomKeys;
    }

    /**
     * Generates a random value set
     */
    private static String[] getRandomValues () {
        String[] randomValues = new String[randomValueCount];

        for (int i = 0; i < randomValueCount; i++) {
            randomValues[i] = UUID.randomUUID().toString();
        }

        return randomValues;
    }

    /**
     * Create update action
     *
     * @param user user
     * @param timestamp timestamp
     * @param values values
     */
    private static String createUpdateAction(String user, int timestamp, JsonNode values) {
        ObjectNode action = mapper.createObjectNode();
        action.put("type", "update");
        action.put("user", user);
        action.put("timestamp", timestamp);
        action.set("values", values);

        return action.toString();
    }

    /**
     * Shuffles the array
     *
     * @param arr array
     * @param shuffleTimes how many times the swap takes place
     */
    private static void shuffleArray (Object[] arr, int shuffleTimes) {
        for (int i = 0; i < shuffleTimes; i++) {
            int index1 = random.nextInt(arr.length);
            int index2 = random.nextInt(arr.length);
            while (index1 == index2) {
                index2 = random.nextInt(arr.length);
            }

            Object temp = arr[index1];
            arr[index1] = arr[index2];
            arr[index2] = temp;
        }
    }

    /**
     * Generates state updates
     *
     * @param userCount user count
     * @param userActionCount user action count
     */
    public static List<String> generateStateUpdates (int userCount, int userActionCount) {
        int totalActions = userCount * userActionCount;
        String[] randomKeys = getRandomKeys();
        String[] randomValues = getRandomValues();

        String[] actionArray = new String[totalActions];

        for (int i = 0; i < userCount; i++) {
            String username = UUID.randomUUID().toString();

            for (int j = 0; j < userActionCount; j++) {
                int updateIndex = i * userActionCount + j;
                ObjectNode updateValues = mapper.createObjectNode();

                // 0 to 3 update entries
                int updateEntryCount = random.nextInt(4);
                for (int t = 0; t < updateEntryCount; t++) {
                    int randomKeyIdx = random.nextInt(randomValueCount);
                    int randomValueIdx = random.nextInt(randomValueCount);

                    String key = randomKeys[randomKeyIdx];
                    String value = randomValues[randomValueIdx];
                    updateValues.put(key, value);
                }

                /*
                 * The initial motivation behind this line is to make the timestamp varies to simulate real world situation.
                 * But turns out this design faulty as the result will also be non-deterministic. So it is disabled
                 *
                 * e.g. user "A" updates value of key "key1" to "result1" and "result2" at the same timestamp 20, both results are acceptable.
                 * So there will be a conflict between the sequential version and concurrent version -> bad test case :(
                 */
                // make timestamp not deterministic
                // int timestamp = updateIndex + random.nextInt(3) - 1;

                actionArray[updateIndex] = createUpdateAction(username, updateIndex, updateValues);
            }
        }

        // shuffles the array slightly
        shuffleArray(actionArray, totalActions / 20);

        return Arrays.stream(actionArray).toList();
    }
}
