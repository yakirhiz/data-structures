import java.util.Map;
import java.util.HashMap;

/**
 *
 * LFUCache
 *
 * An implementation of an LFU cache.
 *
 */

class LFUCache<K, V> {
    private int capacity;
    private Map<K, Node> map;
    private Bucket sBucket; // Sentinel

    public LFUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>(capacity);
        sBucket = new Bucket(0);
        sBucket.next = sBucket.prev = sBucket;
    }

    private class Node {
        K key;
        V value;
        Node prev, next;
        Bucket bucket;

        public Node(K key, V value, Bucket bucket) {
            this.key = key;
            this.value = value;
            this.bucket = bucket;
        }
    }

    private class Bucket {
        Node sNode; // Sentinel
        int frequency;
        Bucket prev, next;

        public Bucket(int frequency) {
            this.frequency = frequency;
            this.sNode = new Node(null, null, this);
            sNode.next = sNode.prev = sNode;
        }

        public boolean isEmpty() {
            // Could use size field instead
            return sNode.next == sNode && sNode.prev == sNode;
        }

        public void insertNode(Node node) {
            node.next = sNode.next;
            sNode.next.prev = node;
            sNode.next = node;
            node.prev = sNode;

            node.bucket = this;
        }

        public void deleteNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public V get(K key) {
        if (map.containsKey(key)) {
            Node node = map.get(key);

            Bucket previousBucket = node.bucket;

            previousBucket.deleteNode(node);
            insertNode(node);

            if (previousBucket.isEmpty()) {
                deleteBucket(previousBucket);
            }

            return node.value;
        }

        return null;
    }

    public void put(K key, V value) {
        if (capacity <= 0) // Handle bad capacity (instead of set minimum capacity)
            return;

        if (key == null || value == null) // null is not valid either as key or as value
            return;

        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;

            Bucket previousBucket = node.bucket;

            previousBucket.deleteNode(node);
            insertNode(node);

            if (previousBucket.isEmpty()) {
                deleteBucket(previousBucket);
            }
        } else {
            if (map.size() == capacity) {
                Node victim = sBucket.next.sNode.prev; // Least frequently used

                Bucket victimBucket = victim.bucket;

                map.remove(victim.key);
                victimBucket.deleteNode(victim);

                if (victimBucket.isEmpty()) {
                    deleteBucket(victimBucket);
                }
            }

            Node node = new Node(key, value, sBucket);

            map.put(key, node);
            insertNode(node);
        }
    }

    private void insertNode(Node node) {
        Bucket oldBucket = node.bucket;

        if (oldBucket.next.frequency == oldBucket.frequency + 1) {
            oldBucket.next.insertNode(node);
        } else {
            Bucket newBucket = new Bucket(oldBucket.frequency + 1);

            insertBucket(newBucket, oldBucket);

            newBucket.insertNode(node);
        }

    }

    private void insertBucket(Bucket newBucket, Bucket bucket) {
        newBucket.next = bucket.next;
        bucket.next.prev = newBucket;
        bucket.next = newBucket;
        newBucket.prev = bucket;
    }

    private void deleteBucket(Bucket bucket) {
        bucket.prev.next = bucket.next;
        bucket.next.prev = bucket.prev;
    }
}