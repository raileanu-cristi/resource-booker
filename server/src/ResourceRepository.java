import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ResourceRepository {
    protected final Map<String, Boolean> resourceAvailabilityMap = new HashMap<>();
    protected final Queue<String> freeResourceQueue = new ConcurrentLinkedQueue<>();

    /**
     * registers a new hosting client
     *
     * @param resource resource to register
     */
    public void register(final String resource) {
        if (!resourceAvailabilityMap.containsKey(resource)) {
            resourceAvailabilityMap.put(resource, true);
            freeResourceQueue.add(resource);
        }
    }

    /**
     * Retrieves an optional of existing free resource or empty value
     *
     */
    public Optional<String> bookResource() {
        final String resource = freeResourceQueue.poll();
        if (resource != null) {
            resourceAvailabilityMap.put(resource, false);
        }
        return Optional.ofNullable(resource);
    }

    /**
     * Free a resource for others to take
     *
     * @param resource the resource to free
     */
    public void freeResource(final String resource) {
        if (resourceAvailabilityMap.containsKey(resource)) {
            resourceAvailabilityMap.put(resource, false);
            freeResourceQueue.add(resource);
        }
    }

}
