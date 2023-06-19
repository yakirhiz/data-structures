import java.util.Map;
import java.util.HashMap;

/**
 *
 * LRUCache
 *
 * An implementation of an LRU cache.
 *
 */

class LRUCache<K, V> {

    private int capacity;
    private Map<K, Node> map;
    private Node head, tail; // Could use sentinel instead

    private class Node {
        K key;
        V value;
        Node prev, next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>(capacity);
    }

    public V get(K key) {
        if (map.containsKey(key)) {
            Node node = map.get(key);

            deleteNode(node);
            insertNode(node);

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

            deleteNode(node);
            insertNode(node);
        } else {
            if (map.size() == capacity) {
                map.remove(tail.key);
                deleteNode(tail);
            }

            Node node = new Node(key, value);

            map.put(key, node);
            insertNode(node);
        }
    }

    private void insertNode(Node node) {
        if (head == null) {
            node.next = node.prev = null;
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;

            head = node;
            node.prev = null;
        }
    }

    private void deleteNode(Node node) {
        // Could be reformed to four conditions
        if (node.next == null && node.prev == null) {
            head = tail = null;
        } else {
            if (node.next == null) {
                tail = tail.prev;
            } else {
                node.next.prev = node.prev;
            }

            if (node.prev == null) {
                head = head.next;
            } else {
                node.prev.next = node.next;
            }
        }
    }
}