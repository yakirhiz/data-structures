/**
 *
 * AVLTree
 *
 * An implementation of an AVL tree.
 *
 */

public class AVLTree<K extends Comparable<K>, V> {

    private AVLNode root;

    public AVLTree() {
        this.root = null;
    }

    /**
     * Returns true if and only if the tree is empty.
     *
     * Time Complexity: O(1)
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * Truncates the tree, i.e., makes it empty.
     *
     * Time Complexity: O(1)
     */
    public void truncate() {
        this.root = null;
    }

    /**
     * Returns the node with the given key if it exists.
     *
     * Time Complexity: O(log n)
     */
    private AVLNode find(K key) {
        AVLNode node = this.root;

        while (node != null) {
            if (key.compareTo(node.key) == 0)
                return node;
            else if (key.compareTo(node.key) < 0)
                node = node.left;
            else
                node = node.right;
        }

        return null;
    }

    /**
     * Returns true if the given key is in the tree. Otherwise, return false.
     *
     * Time Complexity: O(log n)
     */
    public boolean contains(K key) {
        return find(key) != null;
    }

    /**
     * Returns the value of the node with the key k if it exists in the tree.
     * Otherwise, returns null.
     *
     * Time Complexity: O(log n)
     */
    public V search(K key) {
        AVLNode node = find(key);

        if (node == null)
            return null;

        return node.value;
    }

    /**
     * Inserts a node with the given key and value to the tree.
     *
     * Time Complexity: O(log n)
     */
    public boolean insert(K key, V value) {
        AVLNode node = this.root;
        AVLNode parent = null;

        // Find insertion location, or notify that the key already exists (return false)
        while (node != null) {
            parent = node;
            if (key.compareTo(node.key) == 0) {
                return false;
            } else if (key.compareTo(node.key) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        // Create new node, and insert it to the tree
        AVLNode newNode = new AVLNode(key, value);
        newNode.parent = parent;

        if (parent == null) {
            this.root = newNode;
        } else if (newNode.key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        balance(parent); // Balance the tree

        return true;
    }

    /**
     * Delete a node with the given key from the tree if it exists.
     *
     * Time Complexity: O(log n)
     */
    public boolean delete(K key) {
        AVLNode node = find(key);

        if (node == null)
            return false;

        AVLNode start = node.parent;

        if (node.left == null && node.right == null) { // No children
            elevate(node, null);
        } else if (node.left == null) { // No left child (could cover the previous case also)
            elevate(node, node.right);
        } else if (node.right == null) { // No right child
            elevate(node, node.left);
        } else { // Two children
            AVLNode succ = successor(node);

            start = succ.parent;

            if (succ.parent != node) {
                /* Parent of succ & child of succ */
                elevate(succ, succ.right);

                /* Right child of node */
                succ.right = node.right;
                node.right.parent = succ;
            } else start = succ;

            /* Parent of node */
            elevate(node, succ);

            /* Left child of node */
            succ.left = node.left;
            node.left.parent = succ;
        }

        balance(start); // Balance the tree

        return true;
    }

    /**
     * Connect u.parent to v (as a parent of v) instead of u
     *
     * Time Complexity: O(1)
     */
    private void elevate(AVLNode u, AVLNode v) {
        if (u.parent == null) {
            this.root = v;
        } else if (u.parent.left == u) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }

        if (v != null)
            v.parent = u.parent;
    }

    /**
     * Returns the node with the smallest key in the tree.
     *
     * Time Complexity: O(log n)
     */
    public K min() {
        if (this.root == null)
            return null;

        return min(this.root).key;
    }

    /**
     * Returns the node with the smallest key in the subtree rooted in node.
     *
     * Time Complexity: O(log n)
     */
    private AVLNode min(AVLNode node) {
        if (node == null)
            return null;

        while (node.left != null) {
            node = node.left;
        }

        return node;
    }

    /**
     * Returns the node with the largest key in the tree.
     *
     * Time Complexity: O(log n)
     */
    public K max() {
        if (this.root == null)
            return null;

        return max(this.root).key;
    }

    /**
     * Returns the node with the largest key in the subtree rooted in node.
     *
     * Time Complexity: O(log n)
     */
    private AVLNode max(AVLNode node) {
        if (node == null)
            return null;

        while (node.right != null) {
            node = node.right;
        }

        return node;
    }

    /**
     * Returns the successor of node, or null if not exists.
     *
     * Time Complexity: O(log n)
     */
    private AVLNode successor(AVLNode node) {
        if (node == null)
            return null;

        if (node.right != null) {
            return min(node.right);
        } else {
            while (node.parent != null && node.parent.right == node) {
                node = node.parent;
            }
            return node.parent; // node.parent is null if successor not exists
        }
    }

    /**
     * Returns the predecessor of node, or null if not exists.
     *
     * Time Complexity: O(log n)
     */
    private AVLNode predecessor(AVLNode node) {
        if (node == null)
            return null;

        if (node.left != null) {
            return max(node.left);
        } else {
            while (node.parent != null && node.parent.left == node) {
                node = node.parent;
            }
            return node.parent; // node.parent is null if predecessor not exists
        }
    }

    /**
     * Returns the number of nodes in the tree.
     *
     * Time Complexity: O(1)
     */
    public int size() {
        return size(this.root);
    }

    /**
     * Returns the number of nodes in the subtree rooted in node.
     *
     * Time Complexity: O(1)
     */
    private int size(AVLNode node) {
        if (node == null)
            return 0;
        return node.size;
    }

    /**
     * Updates the size of the given node.
     *
     * Time Complexity: O(1)
     */
    private void updateSize(AVLNode node) {
        if (node != null) {
            node.size = size(node.left) + size(node.right) + 1;
        }
    }

    /**
     * Returns the height of the tree.
     *
     * Time Complexity: O(1)
     */
    public int height() {
        return height(this.root);
    }

    /**
     * Returns the height of the subtree rooted in node.
     *
     * Time Complexity: O(1)
     */
    private int height(AVLNode node) {
        if (node == null)
            return -1;
        return node.height;
    }

    /**
     * Updates the height of the given node.
     *
     * Time Complexity: O(1)
     */
    private void updateHeight(AVLNode node) {
        if (node != null) {
            if (height(node.left) < height(node.right)) {
                node.height = height(node.right) + 1;
            } else {
                node.height = height(node.left) + 1;
            }
        }
    }

    /**
     * Return the key with the given rank.
     *
     * Time Complexity: O(log n)
     */
    public K select(int rank) {
        if (rank < 1 || rank > size())
            return null;

        AVLNode node = this.root;

        int currentRank = size(node.left) + 1;

        while (rank != currentRank) {
            if (rank < currentRank) {
                node = node.left;
            }

            if (rank > currentRank) {
                node = node.right;
                rank = rank - currentRank;
            }

            currentRank = size(node.left) + 1;
        }

        return node.key;
    }

    /**
     * Return the rank of the given key.
     *
     * Time Complexity: O(log n)
     */
    public int rank(K key) {
        AVLNode node = find(key);

        if (node == null)
            return -1;

        int rank = size(node.left) + 1;

        while (node.parent != null) {
            if (node.parent.right == node)
                rank = rank + size(node.parent.left) + 1;
            node = node.parent;
        }

        return rank;
    }

    /**
     * Returns the balance-factor of the given node.
     *
     * Time Complexity: O(1)
     */
    private int balanceFactor(AVLNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    /**
     * Balances the tree.
     *
     * Time Complexity: O(log n)
     */
    private void balance(AVLNode node) {
        while (node != null) {
            if (balanceFactor(node) == 2) {
                if (balanceFactor(node.left) == -1) {
                    node = rotateLeftRight(node);
                } else { // balanceFactor(node.left) is 0 or 1
                    node = rotateRight(node);
                }
            } else if (balanceFactor(node) == -2) {
                if (balanceFactor(node.right) == 1) {
                    node = rotateRightLeft(node);
                } else { // balanceFactor(node.right) is 0 or -1
                    node = rotateLeft(node);
                }
            }

            update(node); // makes some unnecessary updates (For the sake of uniformity)

            node = node.parent;
        }
    }

    /**
     * Updates the sizes and the heights of the node and its children.
     *
     * Time Complexity: O(1)
     */
    private void update(AVLNode node) {
        if (node != null) {
            updateHeight(node.left);
            updateHeight(node.right);
            updateHeight(node);

            updateSize(node.left);
            updateSize(node.right);
            updateSize(node);
        }
    }

    /**
     * Right rotation
     *
     * Time Complexity: O(1)
     */
    private AVLNode rotateRight(AVLNode node) {
        AVLNode newRoot = node.left;

        node.left = newRoot.right;

        if (newRoot.right != null)
            newRoot.right.parent = node;

        elevate(node, newRoot);

        newRoot.right = node;
        node.parent = newRoot;

        return newRoot;
    }

    /**
     * Left rotation
     *
     * Time Complexity: O(1)
     */
    private AVLNode rotateLeft(AVLNode node) {
        AVLNode newRoot = node.right;

        node.right = newRoot.left;

        if (newRoot.left != null)
            newRoot.left.parent = node;

        elevate(node, newRoot);

        newRoot.left = node;
        node.parent = newRoot;

        return newRoot;
    }

    /**
     * Right-left rotation
     *
     * Time Complexity: O(1)
     */
    private AVLNode rotateRightLeft(AVLNode node) {
        AVLNode middle = node.right;
        AVLNode newRoot = node.right.left;

        middle.left = newRoot.right;

        if (newRoot.right != null)
            newRoot.right.parent = middle;

        newRoot.right = middle;
        middle.parent = newRoot;

        node.right = newRoot.left;

        if (newRoot.left != null)
            newRoot.left.parent = node;

        elevate(node, newRoot);

        newRoot.left = node;
        node.parent = newRoot;

        return newRoot;
    }

    /**
     * Left-right rotation
     *
     * Time Complexity: O(1)
     */
    private AVLNode rotateLeftRight(AVLNode node) {
        AVLNode middle = node.left;
        AVLNode newRoot = node.left.right;

        middle.right = newRoot.left;

        if (newRoot.left != null)
            newRoot.left.parent = middle;

        newRoot.left = middle;
        middle.parent = newRoot;

        node.left = newRoot.right;

        if (newRoot.right != null)
            newRoot.right.parent = node;

        elevate(node, newRoot);

        newRoot.right = node;
        node.parent = newRoot;

        return newRoot;
    }

    /**
     * Inorder traversal (no recursion or stack)
     *
     * Time Complexity: O(n)
     */
    public String inorder() {
        StringBuilder sb = new StringBuilder();

        // The algorithm (the tree structure is preserved)
        AVLNode curr = this.root;

        while (curr != null) {
            if (curr.left == null) {
                sb.append(curr.key).append(curr.right != null ? " " : ""); // visit(curr)
                curr = curr.right;
            } else {
                AVLNode pre = curr.left;

                while (pre.right != curr && pre.right != null) {
                    pre = pre.right;
                }

                if (pre.right == null) {
                    pre.right = curr;
                    curr = curr.left;
                } else {
                    pre.right = null;
                    sb.append(curr.key).append(curr.right != null ? " " : ""); // visit(curr)
                    curr = curr.right;
                }
            }
        }

        return sb.toString();
    }

    /**
     * Prints the tree.
     *
     * Time Complexity: O(n)
     */
    private StringBuilder toString(AVLNode node, StringBuilder buffer, StringBuilder prefix, boolean isTail) {
        if (node == null)
            return buffer;

        if (node.right != null)
            toString(node.right, buffer, new StringBuilder(prefix).append(isTail ? "│   " : "    "), false);

        buffer.append(prefix).append(isTail ? "└── " : "┌── ").append(node.key).append("\n");

        if (node.left != null)
            toString(node.left, buffer, new StringBuilder(prefix).append(isTail ? "    " : "│   "), true);

        return buffer;
    }

    public String toString() {
        return this.toString(this.root, new StringBuilder(), new StringBuilder(), true).toString();
    }

    /**
     * AVLNode
     *
     * An implementation of an AVL node.
     */

    public class AVLNode {
        K key;
        V value;
        AVLNode left;
        AVLNode right;
        AVLNode parent;
        int height;
        int size;

        public AVLNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.right = null;
            this.left = null;
            this.parent = null;
            this.height = 0;
            this.size = 1;
        }

        public K getKey() {
            return this.key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return this.value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public AVLNode getLeft() {
            return this.left;
        }

        public void setLeft(AVLNode node) {
            this.left = node;
        }

        public AVLNode getRight() {
            return this.right;
        }

        public void setRight(AVLNode node) {
            this.right = node;
        }

        public AVLNode getParent() {
            return this.parent;
        }

        public void setParent(AVLNode node) {
            this.parent = node;
        }

        public int getHeight() {
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getSize() {
            return this.size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}