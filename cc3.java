import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class TimeBasedCache<K, V> {

    // Each entry in the map stores the value and the expiryTime (absolute time).
    private static class Entry<V> {
        V value;
        long expiryTime;

        Entry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    // Each node in the priority queue stores the key and its expiryTime,
    // used to find and remove expired entries in O(log n).
    private static class ExpiryNode<K> implements Comparable<ExpiryNode<K>> {
        K key;
        long expiryTime;

        ExpiryNode(K key, long expiryTime) {
            this.key = key;
            this.expiryTime = expiryTime;
        }

        @Override
        public int compareTo(ExpiryNode<K> other) {
            return Long.compare(this.expiryTime, other.expiryTime);
        }
    }

    private final Map<K, Entry<V>> map;
    private final PriorityQueue<ExpiryNode<K>> minHeap;

    public TimeBasedCache() {
        this.map = new HashMap<>();
        this.minHeap = new PriorityQueue<>();
    }

    /**
     * Store a key-value pair with an expiry time (in milliseconds from now).
     * Example usage: set("foo", "bar", 3000) => expires in 3 seconds.
     */
    public void set(K key, V value, long ttlMillis) {
        long currentTime = System.currentTimeMillis();
        long expiryTime = currentTime + ttlMillis;

        // 1. Clean up any expired entries before adding a new one
        evictExpired();

        // 2. Put/update the entry in the map
        map.put(key, new Entry<>(value, expiryTime));

        // 3. Push a new ExpiryNode into the min-heap
        minHeap.offer(new ExpiryNode<>(key, expiryTime));
    }

    /**
     * Retrieve the value for the given key if not expired; otherwise return null.
     */
    public V get(K key) {
        // 1. Evict expired entries first
        evictExpired();

        // 2. Now check if the key is present and not expired
        Entry<V> entry = map.get(key);
        if (entry == null) {
            return null;  // key not found or was expired
        }

        // If it's found, we must check if it's still valid (edge case if system time advanced)
        long currentTime = System.currentTimeMillis();
        if (currentTime >= entry.expiryTime) {
            // It's expired
            map.remove(key);
            return null;
        }

        // Otherwise, it's valid
        return entry.value;
    }

    /**
     * Internal method to remove all expired entries from the top of the min-heap.
     */
    private void evictExpired() {
        long currentTime = System.currentTimeMillis();

        // Keep removing from the heap top while entries are expired
        while (!minHeap.isEmpty()) {
            ExpiryNode<K> top = minHeap.peek();
            // If the earliest expiry is in the future, we're done
            if (top.expiryTime > currentTime) {
                break;
            }

            // Otherwise, pop it
            minHeap.poll();

            // Check if this node is truly expired in the map
            Entry<V> entry = map.get(top.key);
            if (entry != null && entry.expiryTime <= currentTime) {
                // Remove from map if it matches
                map.remove(top.key);
            }
        }
    }

    // ---------------------------
    // Demonstration
    // ---------------------------
    public static void main(String[] args) throws InterruptedException {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>();

        // Set key "hello" -> "world" for 2 seconds
        cache.set("hello", "world", 2000);
        System.out.println("get(hello): " + cache.get("hello")); // Should be "world"

        // Wait 3 seconds
        Thread.sleep(3000);

        // After 3 seconds, "hello" should be expired
        System.out.println("get(hello): " + cache.get("hello")); // Should be null
    }
}
