package cn.nukkit.utils;

import com.google.common.base.Preconditions;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * SortedList is an ordered implementation of {@link List}, internally represented as a balanced binary tree.
 * <p>
 * Note that you cannot perform specific operations on this list except for {@code remove(int)}, {@code remove(Object)}, {@code clear}, and {@code add()}.
 * <p>
 * The time complexity of the following operations is as follows:
 * The time complexity of `{@code contains}`, `{@code add}`, `{@code remove}`, and `{@code get}` is `O(log(n))`.
 * <p>
 * This list is not guaranteed to be thread-safe. If necessary, use `{@link Collections#synchronizedList(List)}` to wrap it and ensure thread safety.
 * <p>
 * The iterator provided by this list is short-lived. Any structural modification outside the iterator itself will cause it to throw a {@link ConcurrentModificationException}.
 *
 * @param <T> The type of the list.
 * @see List
 * @see Collection
 * @see AbstractList
 */


public class SortedList<T> extends AbstractList<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7115342129716877152L;

    // Used to obtain the ID of the next node
    private int NEXT_NODE_ID = Integer.MIN_VALUE;

    // The root node of a balanced tree
    private Node root;

    // Element sorting comparator
    private final Comparator<? super T> comparator;

    /**
     * Construct a new empty SortedList and sort its elements using the specified comparator.
     *
     * @param comparator Comparator for sorting elements
     */
    public SortedList(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    /**
     * Inserts the given object into the appropriate position in a SortedList to ensure elements are preserved in the order specified by the given comparator.
     * <p>
     * This method only permits adding non-null values. If the given object is null, the list remains unchanged and returns false.
     *
     * @param object Elements to be added
     * @return Returns false if the given object is null; otherwise, returns true.
     */
    @Override
    public boolean add(T object) {
        boolean treeAltered = false;
        if (object != null) {
            // Wrap the value in a node and add it to the tree.
            add(new Node(object)); // This will ensure the mod count increments
            treeAltered = true;
        }
        return treeAltered;
    }

    /**
     * Add the given node to this SortedList
     * <p>
     * This method can be overridden by subclasses to modify the definition of nodes that this list will store.
     * <p>
     * This implementation uses the {@link Node#compareTo(Node)} method to determine where a given node should be stored. It also increments the modCount of this list.
     *
     * @param toAdd New nodes to be added
     */
    protected void add(Node toAdd) {
        if (root == null) {
            root = toAdd;
        } else {
            Node current = root;
            //noinspection ConstantConditions
            while (current != null) { // Theoretically, this thing is true, but to ensure it still works when using dynamic proxies, JVMTI, debuggers, or JVMCI, we need to check.
                int comparison = toAdd.compareTo(current);
                if (comparison < 0) { // toAdd < node
                    if (current.leftChild == null) {
                        current.setLeftChild(toAdd);
                        break;
                    } else {
                        current = current.leftChild;
                    }
                } else { // toAdd > node (==It's highly unlikely to happen, and even if it did, the escape hatch above would still function.)
                    if (current.rightChild == null) {
                        current.setRightChild(toAdd);
                        break;
                    } else {
                        current = current.rightChild;
                    }
                }
            }
        }
        modCount++; // Refer to AbstractList#modCount. Increasing this value can cause the iterator to become invalid quickly.
    }

    /**
     * Test whether this tree has the exact same structure and values as the given tree. For testing purposes only.
     */
    boolean structurallyEqualTo(SortedList<T> other) {
        return other != null && structurallyEqualTo(root, other.root);
    }

    private boolean structurallyEqualTo(Node currentThis, Node currentOther) {
        if (currentThis == null) {
            return currentOther == null;
        } else if (currentOther == null) {
            return false;
        }
        return currentThis.value.equals(currentOther.value)
                && structurallyEqualTo(currentThis.leftChild, currentOther.leftChild)
                && structurallyEqualTo(currentThis.rightChild, currentOther.rightChild);
    }

    /**
     * Provides an iterator that yields the elements of this SortedList in the order determined by the given comparator.
     * <p>
     * This iterator implementation allows traversing the entire list in O(n) time complexity, where n is the number of elements in the list.
     *
     * @return An iterator that provides the elements of this sorted list in the order determined by the given comparator.
     */
    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    // Implementation of an iterator interface using a successor method
    // To achieve O(n) speed, we iterate over the list rather than using O(n*log(n)) sorting.
    private class Itr implements Iterator<T> {
        private Node nextNode = (isEmpty() ? null : findNodeAtIndex(0));
        private int nextIndex = 0;
        private Node lastReturned = null;
        /**
         * The expected modCount for this iterator
         */
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public T next() {
            checkModCount();

            if (nextNode == null) {
                throw new NoSuchElementException();
            }

            lastReturned = nextNode;
            nextNode = nextNode.successor();
            nextIndex++;

            return lastReturned.value;
        }

        @Override
        public void remove() {
            checkModCount();

            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            SortedList.this.remove(lastReturned);
            lastReturned = null;

            // The next node may now be incorrect, so it needs to be retrieved again.
            nextIndex--;
            if (nextIndex < size()) { // Verify whether the node with this index actually exists.
                nextNode = findNodeAtIndex(nextIndex);
            } else {
                nextNode = null;
            }

            expectedModCount = modCount;
        }

        /**
         * Check whether the modCount is the expected value.
         */
        private void checkModCount() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * @return The number of elements stored in this SortedList.
     */
    @Override
    public int size() {
        return (root == null) ? 0 : 1 + root.numChildren;
    }

    /**
     * @return The root node of this SortedList. If the list is empty, returns null.
     */
    protected Node getRoot() {
        return root;
    }

    /**
     * Returns whether the given object is present in this SortedList.
     * <p>
     * Element comparison uses the {@link Object#equals(Object)} method and assumes the given obj must be of type T equal to an element in this SortedList.
     * Time complexity is <i>O(log(n))</i>, where n is the number of elements in the list.
     *
     * @param obj The object to check for existence
     * @return Whether it exists in this SortedList
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object obj) {
        return obj != null
                && !isEmpty()
                && findFirstNodeWithValue((T) obj) != null;
    }

    /**
     * Returns the node representing the given value in the tree. If no such node exists, it may be null.
     * <p>
     * This method performs a binary search using the given comparator, with a time complexity of O(log(n)).
     *
     * @param value The value to search for
     * @return The first node in this list with the given value
     */
    protected Node findFirstNodeWithValue(T value) {
        Node current = root;
        while (current != null) {
            int comparison = comparator.compare(current.value, value);
            if (comparison == 0) {
                while (current.leftChild != null
                        && comparator.compare(current.leftChild.value, value) == 0) {
                    current = current.leftChild;
                }
                break;
            } else if (comparison < 0) {
                current = current.rightChild;
            } else {
                current = current.leftChild;
            }
        }
        return current;
    }

    /**
     * Removes and returns the element at the given index in this SortedList. Since the list is sorted, this is the fourth smallest element starting from 0-n-1.
     * <p>
     * For example, calling remove(0) deletes the smallest element in the list.
     *
     * @param index The index of the element to remove
     * @return The removed element
     * @throws IllegalArgumentException If the index is invalid
     */
    @Override
    public T remove(int index) {
        // Retrieve the node at the index. If the node at the index does not exist, an exception will be thrown.
        Node nodeAtIndex = findNodeAtIndex(index);
        remove(nodeAtIndex);
        return nodeAtIndex.value;
    }

    /**
     * Removes the first element in the list with the given value (if such a node exists), otherwise does nothing. Compares elements using the specified comparator.
     * <p>
     * Returns whether a matching element was found and removed.
     *
     * @param value The element to be removed
     * @return Whether a matching element was found and removed.
     */
    @Override
    public boolean remove(Object value) {
        boolean treeAltered = false;
        try {
            if (value != null && root != null) {
                @SuppressWarnings("unchecked")
                Node toRemove = findFirstNodeWithValue((T) value);
                if (toRemove != null) {
                    remove(toRemove);
                    treeAltered = true;
                }
            }
        } catch (ClassCastException ignore) {

        }
        return treeAltered;
    }

    /**
     * Removes the specified node from this SortedList. If rebalancing is required, it also increments modCount.
     * Time complexity O(log(n)).
     *
     * @param toRemove The node to remove from this SortedList
     */
    protected void remove(Node toRemove) {
        if (toRemove.isLeaf()) {
            Node parent = toRemove.parent;
            if (parent == null) {
                root = null;
            } else {
                toRemove.detachFromParentIfLeaf();
            }
        } else if (toRemove.hasTwoChildren()) {
            Node successor = toRemove.successor();
            toRemove.switchValuesForThoseIn(successor);
            remove(successor);

        } else if (toRemove.leftChild != null) {
            toRemove.leftChild.contractParent();
        } else {
            toRemove.rightChild.contractParent();
        }
        modCount++;
    }

    /**
     * Returns the element at the given index in this SortedList. Since the list is sorted, this is the index of the fourth smallest element, starting from 0-n-1.
     * <p>
     * For example, calling get(0) returns the smallest element in the list.
     *
     * @param index The index of the element to retrieve
     * @return The element at the given index in this SortedList
     * @throws IllegalArgumentException If the index is invalid
     */
    @Override
    public T get(int index) {
        return findNodeAtIndex(index).value;
    }

    protected Node findNodeAtIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new IllegalArgumentException(index + " is not valid index.");
        }
        Node current = root;
        int totalSmallerElements = (current.leftChild == null) ? 0 : current.leftChild.sizeOfSubTree();
        //noinspection ConstantConditions
        while (current != null) {
            if (totalSmallerElements == index) {
                break;
            }
            if (totalSmallerElements > index) {
                current = current.leftChild;
                totalSmallerElements--;
                totalSmallerElements -= (Objects.requireNonNull(current).rightChild == null) ? 0 : current.rightChild.sizeOfSubTree();
            } else {
                totalSmallerElements++;
                current = current.rightChild;
                totalSmallerElements += (current.leftChild == null) ? 0 : current.leftChild.sizeOfSubTree();
            }
        }
        return current;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size()];
        int positionToInsert = 0;
        if (root != null) {
            Node next = root.smallestNodeInSubTree();
            while (next != null) {
                array[positionToInsert] = next.value;
                positionToInsert++;

                next = next.successor();
            }
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E[] toArray(E[] holder) {
        int size = size();
        if (holder.length < size) {
            Class<?> classOfE = holder.getClass().getComponentType();
            holder = (E[]) Array.newInstance(classOfE, size);
        }
        Iterator<T> itr = iterator();
        int posToAdd = 0;
        while (itr.hasNext()) {
            holder[posToAdd] = (E) itr.next();
            posToAdd++;
        }
        return holder;
    }

    /**
     * Returns the smallest balancing factor in the entire list. For testing purposes only.
     */
    int minBalanceFactor() {
        int minBalanceFactor = 0;
        Node current = root;
        while (current != null) {
            minBalanceFactor = Math.min(current.getBalanceFactor(), minBalanceFactor);
            current = current.successor();
        }
        return minBalanceFactor;
    }

    /**
     * Returns the maximum balancing factor in the entire list. For testing purposes only.
     */
    int maxBalanceFactor() {
        int maxBalanceFactor = 0;
        Node current = root;
        while (current != null) {
            maxBalanceFactor = Math.max(current.getBalanceFactor(), maxBalanceFactor);
            current = current.successor();
        }
        return maxBalanceFactor;
    }

    //Begin rebalancing the binary tree starting from startNode, then recursively traverse upward through the tree...
    private void rebalanceTree(Node startNode) {
        Node current = startNode;
        while (current != null) {
            //Retrieve the differences between the left and right subtrees at this point.
            int balanceFactor = current.getBalanceFactor();

            if (balanceFactor == -2) {
                if (current.rightChild.getBalanceFactor() == 1) {
                    current.rightChild.leftChild.rightRotateAsPivot();
                }
                current.rightChild.leftRotateAsPivot();

            } else if (balanceFactor == 2) {
                if (current.leftChild.getBalanceFactor() == -1) {
                    current.leftChild.rightChild.leftRotateAsPivot();
                }
                current.leftChild.rightRotateAsPivot();
            }

            if (current.parent == null) {
                root = current;
                break;
            } else {
                current = current.parent;
            }
        }
    }

    /**
     * An inner class used to represent positions within a tree. Each node stores a list of equal values containing its child and parent nodes, the height of the subtree rooted at that point, and the total number of child elements it possesses.
     * <p>
     * By the way, the terms “left” and “right” here are merely a convention used by Chinese programmers to describe data structures. They do not imply any political stance of vacillation between left and right. I firmly believe in socialism with Chinese characteristics.
     *
     * @author superice666
     */
    protected class Node implements Comparable<Node> {

        private T value;

        private Node leftChild;
        private Node rightChild;
        private Node parent;

        private int height;
        private int numChildren;

        /**
         * Unique ID for this node: Automatically generated; newer nodes have higher values.
         */
        protected final int id;

        protected Node(T t) {
            this.value = t;
            this.id = NEXT_NODE_ID++;
        }

        protected boolean hasTwoChildren() {
            return leftChild != null && rightChild != null;
        }

        // If it is a leaf node, remove the node and update the number of child nodes and height in the tree.
        private void detachFromParentIfLeaf() {
            Preconditions.checkState(isLeaf() && parent != null, "Call made to detachFromParentIfLeaf, but this is not a leaf node with a parent!");
            if (isLeftChildOfParent()) {
                parent.setLeftChild(null);
            } else {
                parent.setRightChild(null);
            }
        }

        /**
         * Returns the parent node of this node, which may be null.
         *
         * @return The parent node of this node (if it exists), otherwise null
         */
        protected Node getGrandParent() {
            return (parent != null && parent.parent != null) ? parent.parent : null;
        }

        // Move this node up one notch in the tree, update its value, and rebalance the tree.
        private void contractParent() {
            Preconditions.checkState(parent != null && !parent.hasTwoChildren(), "Can not call contractParent on root node or when the parent has two children!");
            Node grandParent = getGrandParent();
            if (grandParent != null) {
                if (isLeftChildOfParent()) {
                    if (parent.isLeftChildOfParent()) {
                        grandParent.leftChild = this;
                    } else {
                        grandParent.rightChild = this;
                    }
                    parent = grandParent;
                } else {
                    if (parent.isLeftChildOfParent()) {
                        grandParent.leftChild = this;
                    } else {
                        grandParent.rightChild = this;
                    }
                    parent = grandParent;
                }
            } else {
                parent = null;
                root = this; // If the root is reset without updating elsewhere
            }

            // Finally, update the value and rebalance this balanced binary tree.
            updateCachedValues();
            rebalanceTree(this);
        }

        /**
         * Returns whether it is the left child of its parent node; returns false if this is the root node.
         *
         * @return true if this is the left child of its parent node, false otherwise
         */
        public boolean isLeftChildOfParent() {
            return parent != null && parent.leftChild == this;
        }

        /**
         * Returns whether it is the right child of its parent node; returns false if this is the root node.
         *
         * @return true if this is the right child of its parent node, false otherwise
         */
        public boolean isRightChildOfParent() {
            return parent != null && parent.rightChild == this;
        }

        protected Node getLeftChild() {
            return leftChild;
        }

        protected Node getRightChild() {
            return rightChild;
        }

        protected Node getParent() {
            return parent;
        }

        /**
         * Compares the value stored at this node against the value at the given node using a comparator. If the values are equal, it compares the nodes by their IDs. We consider the older node to be smaller.
         *
         * @return Returns the number returned by the comparator when comparing the values stored at this node and the given node, if non-zero. Otherwise, returns the ID of this node minus the ID of the given node.
         */
        public int compareTo(Node other) {
            int comparison = comparator.compare(value, other.value);
            return (comparison == 0) ? (id - other.id) : comparison;
        }

        protected final Node smallestNodeInSubTree() {
            Node current = this;
            //noinspection ConstantConditions
            while (current != null) {
                if (current.leftChild == null) {
                    break;
                } else {
                    current = current.leftChild;
                }
            }
            return current;
        }

        protected final Node largestNodeInSubTree() {
            Node current = this;
            //noinspection ConstantConditions
            while (current != null) {
                if (current.rightChild == null) {
                    break;
                } else {
                    current = current.rightChild;
                }
            }
            return current;
        }

        /**
         * Returns the next largest node in the tree. Returns null if this is the largest node.
         *
         * @return The next largest node in the tree, or null if this is the largest node.
         */
        protected final Node successor() {
            Node successor = null;
            if (rightChild != null) {
                successor = rightChild.smallestNodeInSubTree();
            } else if (parent != null) {
                Node current = this;
                while (current != null && current.isRightChildOfParent()) {
                    current = current.parent;
                }
                successor = Objects.requireNonNull(current).parent;
            }
            return successor;
        }

        /**
         * Returns the next smallest node in the tree. Returns null if this is the smallest node.
         *
         * @return The next smallest node in the tree, or null if this is the smallest node.
         */
        protected final Node predecessor() {
            Node predecessor = null;
            if (leftChild != null) {
                predecessor = leftChild.largestNodeInSubTree();
            } else if (parent != null) {
                Node current = this;
                while (current != null && current.isLeftChildOfParent()) {
                    current = current.parent;
                }
                predecessor = Objects.requireNonNull(current).parent;
            }
            return predecessor;
        }

        // Set the child node as left/right only when the given node is null or a leaf, and the current child node is identical.
        private void setChild(boolean isLeft, Node leaf) {
            //perform the update
            if (leaf != null) {
                leaf.parent = this;
            }
            if (isLeft) {
                leftChild = leaf;
            } else {
                rightChild = leaf;
            }

            // Ensure that any changes in tree height are addressed.
            updateCachedValues();
            rebalanceTree(this);
        }

        /**
         * Returns whether this node is a leaf node. If both its left and right children are null, it is a leaf.
         *
         * @return true if this node is a leaf node, false otherwise.
         */
        public boolean isLeaf() {
            return (leftChild == null && rightChild == null);
        }

        @Override
        public String toString() {
            return "[Node: value: " + value +
                    ", leftChild value: " + ((leftChild == null) ? "null" : leftChild.value) +
                    ", rightChild value: " + ((rightChild == null) ? "null" : rightChild.value) +
                    ", height: " + height +
                    ", numChildren: " + numChildren + "]\n";
        }

        private void leftRotateAsPivot() {
            Preconditions.checkState(parent != null && parent.rightChild == this, "Can't left rotate as pivot has no valid parent node.");

            Node oldParent = this.rotateGrandParentToParent();

            Node oldLeftChild = leftChild;
            leftChild = oldParent;
            if (oldLeftChild != null) {
                oldLeftChild.parent = oldParent;
            }
            oldParent.rightChild = oldLeftChild;

            oldParent.updateCachedValues();
        }

        /**
         * Returns the number child nodes of this node plus one. This method uses cached variables to ensure it runs in constant time.
         *
         * @return The number child nodes of this node plus one
         */
        public int sizeOfSubTree() {
            return 1 + numChildren;
        }

        public T getValue() {
            return value;
        }

        private void rightRotateAsPivot() {
            Preconditions.checkState(parent != null && parent.leftChild == this, "Can't right rotate as pivot has no valid parent node.");

            Node oldParent = this.rotateGrandParentToParent();

            Node oldRightChild = rightChild;
            rightChild = oldParent;
            if (oldRightChild != null) {
                oldRightChild.parent = oldParent;
            }
            oldParent.leftChild = oldRightChild;

            oldParent.updateCachedValues();
        }

        /**
         * Sets the current grandparent node to as new parent
         * @return The old parent node
         */
        protected final Node rotateGrandParentToParent() {
            Node oldParent = parent;
            Node grandParent = getGrandParent();
            if (grandParent != null) {
                if (parent.isLeftChildOfParent()) {
                    grandParent.leftChild = this;
                } else {
                    grandParent.rightChild = this;
                }
            }
            this.parent = grandParent;
            oldParent.parent = this;
            return oldParent;
        }

        /**
         * Update the height and number of child nodes for this node on the path. Also call {@link #updateAdditionalCachedValues()} for each node on the path, including this node.
         */
        protected final void updateCachedValues() {
            Node current = this;
            while (current != null) {
                if (current.isLeaf()) {
                    current.height = 0;
                    current.numChildren = 0;

                } else {
                    int leftTreeHeight = (current.leftChild == null) ? 0 : current.leftChild.height;
                    int rightTreeHeight = (current.rightChild == null) ? 0 : current.rightChild.height;
                    current.height = 1 + Math.max(leftTreeHeight, rightTreeHeight);

                    int leftTreeSize = (current.leftChild == null) ? 0 : current.leftChild.sizeOfSubTree();
                    int rightTreeSize = (current.rightChild == null) ? 0 : current.rightChild.sizeOfSubTree();
                    current.numChildren = leftTreeSize + rightTreeSize;
                }

                current.updateAdditionalCachedValues();

                current = current.parent;
            }
        }

        /**
         * Called when inserting or removing a node from the tree, providing a hook for subclasses to update their cached values.
         * <p>
         * This method is invoked whenever the list changes. It is first called on the deepest node affected by the given change, then on that node's ancestors, continuing until it reaches the root node.
         * <p>
         * Therefore, it should only be used to update cache values when the parent node has the correct value at the time of computation and the cache value is not global.
         * <p>
         * This implementation is empty, left for subclasses to use (though I doubt anyone will).
         */
        protected void updateAdditionalCachedValues() {

        }

        // Replace the value in this node with the value from another node.
        // This method should only be called when deletion is required and only one value exists.
        private void switchValuesForThoseIn(Node other) {
            this.value = other.value;
        }

        private int getBalanceFactor() {
            return ((leftChild == null) ? 0 : leftChild.height + 1) -
                    ((rightChild == null) ? 0 : rightChild.height + 1);
        }

        private void setLeftChild(Node leaf) {
            boolean valid = (leaf == null || leaf.isLeaf()) && (leftChild == null || leftChild.isLeaf());
            Preconditions.checkState(valid, "setLeftChild should only be called with null or a leaf node, to replace a likewise child node.");
            setChild(true, leaf);
        }

        private void setRightChild(Node leaf) {
            boolean valid = (leaf == null || leaf.isLeaf()) && (rightChild == null || rightChild.isLeaf());
            Preconditions.checkState(valid, "setRightChild should only be called with null or a leaf node, to replace a likewise child node.");
            setChild(false, leaf);
        }
    }
}