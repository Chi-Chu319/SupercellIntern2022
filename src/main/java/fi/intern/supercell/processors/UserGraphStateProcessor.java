package fi.intern.supercell.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.intern.supercell.graph.AbstractUserGraph;
import fi.intern.supercell.graph.ConcurrentUserGraph;
import fi.intern.supercell.graph.UserGraph;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * User graph processor track users states
 *
 * This is a solution class to question 2
 */
public class UserGraphStateProcessor {

    private UserGraph userGraph = new UserGraph();
    private ConcurrentUserGraph concurrentUserGraph = new ConcurrentUserGraph();
    private final ObjectMapper mapper = new ObjectMapper();
    private boolean surpassLog = false;
    private int parallelism;
    private ForkJoinPool forkJoinPool;

    public UserGraphStateProcessor(boolean surpassLog) {
        this.surpassLog = surpassLog;
        this.parallelism = Runtime.getRuntime().availableProcessors();
        this.forkJoinPool = new ForkJoinPool(this.parallelism);
    }

    public UserGraphStateProcessor() {
        this.parallelism = Runtime.getRuntime().availableProcessors();
        this.forkJoinPool = new ForkJoinPool(this.parallelism);
    }

    /**
     * Processes update action
     *
     * @param action action
     * @throws IllegalArgumentException IllegalArgumentException
     */
    private void processUpdateAction (JsonNode action, AbstractUserGraph appliedUserGraph) throws IllegalArgumentException {
        if (action.get("type") == null || !Objects.equals(action.get("type").textValue(), "update")) {
            throw new IllegalArgumentException("Invalid action type");
        }

        String user = action.get("user").textValue();
        int timestamp = action.get("timestamp").intValue();
        JsonNode values = action.get("values");

        appliedUserGraph.updateUser(user, timestamp, values);
    }

    /**
     * Gets parallelism
     */
    public int getParallelism () {
        return this.parallelism;
    }

    /**
     * Gets parallelism
     */
    public void setParallelism (int parallelism) {
        this.parallelism = parallelism;
        this.forkJoinPool = new ForkJoinPool(this.parallelism);
    }

    /**
     * Processes the input, stdout/outputs entire user state in a map (sequential)
     *
     * @param lines lines
     * @return output string
     */
    public String processSequential(List<String> lines) {
        try {
            lines.forEach(line -> {
                try {
                    JsonNode action = mapper.readValue(line, ObjectNode.class);
                    processUpdateAction(action, this.userGraph);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });

            String output = userGraph.getUserStates().toPrettyString();
            if (!surpassLog) {
                System.out.print(output);
            }

            return output;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resets the graph states
     */
    public void reset () {
        this.userGraph = new UserGraph();
        this.concurrentUserGraph = new ConcurrentUserGraph();
    }

    /**
     * Processes the input, stdout/outputs entire user state in a map (concurrent)
     *
     * @param lines lines
     * @return output string
     */
    public String processConcurrent(List<String> lines) {
        try {
            forkJoinPool.submit(() ->
                lines.parallelStream().forEach(line -> {
                    try {
                        JsonNode action = mapper.readValue(line, ObjectNode.class);
                        processUpdateAction(action, this.concurrentUserGraph);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                })
            ).get();

            String output = concurrentUserGraph.getUserStates().toPrettyString();
            if (!surpassLog) {
                System.out.println(output);
            }

            return output;
        } catch (IllegalArgumentException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
