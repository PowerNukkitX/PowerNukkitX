package cn.nukkit.utils;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * SortedList是{@link List}的一个有序实现，内部是用平衡二叉树实现的。
 * <p>
 * 请注意，你不能对这个列表做指定项操作，除了{@code remove(int)}，{@code remove(Object)}，{@code clear}和{@code add()}。
 * <p>
 * 此列表操作的时间复杂度如下：{@code contains}，{@code add}，{@code remove}以及{@code get}
 * 的时间复杂度为<i>O(log(n))</i>。
 * <p>
 * 此列表不保证线程安全，若有必要，请使用{@link Collections#synchronizedList(List)}包装以确保线程安全。
 * <p>
 * 这个列表提供的迭代器是快速失效的，所以除了通过迭代器本身之外的任何结构修改都会导致它抛出{@link ConcurrentModificationException}。
 *
 * @param <T> 列表类型.
 * @see List
 * @see Collection
 * @see AbstractList
 */
public class SortedList<T> extends AbstractList<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -7115342129716877152L;

    // 用以获取下一个节点的id
    private int NEXT_NODE_ID = Integer.MIN_VALUE;

    // 平衡树的根节点
    private Node root;

    // 元素排序比较器
    private final Comparator<? super T> comparator;

    /**
     * 构造一个新的空SortedList，根据给定的比较器对元素进行排序。
     *
     * @param comparator 给元素排序的比较器
     */
    public SortedList(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    /**
     * 将给定对象插入SortedList的适当位置，以确保列表中的元素按给定比较器指定的顺序保存。
     * <p>
     * 此方法仅允许添加非null值，如果给定对象为null，则列表保持不变并返回false。
     *
     * @param object 要添加的元素
     * @return 当给定对象为null时为false，否则为true
     */
    @Override
    public boolean add(T object) {
        boolean treeAltered = false;
        if (object != null) {
            // 将值包装在节点中并添加它到树上
            add(new Node(object)); //这将确保modcount自增
            treeAltered = true;
        }
        return treeAltered;
    }

    /**
     * 将给定节点添加到此SortedList。
     * <p>
     * 此方法可以被子类重写，以便更改此列表将存储的节点的定义。
     * <p>
     * 此实现使用{@link Node#compareTo(Node)}方法来确定给定节点应该存储在哪里。它还会增加此列表的modCount。
     *
     * @param toAdd 要新增的节点
     */
    protected void add(Node toAdd) {
        if (root == null) {
            root = toAdd;
        } else {
            Node current = root;
            //noinspection ConstantConditions
            while (current != null) { // 理论上这玩意==true，但是为了确保使用动态代理、JVMTI、调试器或JVMCI时仍然能正常，我们需要判断下
                int comparison = toAdd.compareTo(current);
                if (comparison < 0) { // toAdd < node
                    if (current.leftChild == null) {
                        current.setLeftChild(toAdd);
                        break;
                    } else {
                        current = current.leftChild;
                    }
                } else { // toAdd > node （==应该不太可能会发生，即使发生了上面的逃生门也能发挥作用）
                    if (current.rightChild == null) {
                        current.setRightChild(toAdd);
                        break;
                    } else {
                        current = current.rightChild;
                    }
                }
            }
        }
        modCount++; // 参阅AbstractList#modCount，增加这个值可以使迭代器快速失效。
    }

    /**
     * 测试是否此树与给定树的结构和值完全相同。仅供测试使用。
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
     * 提供一个迭代器，该迭代器按照给定比较器确定的顺序提供此SortedList的元素。
     * <p>
     * 此迭代器实现允许以O(n)时间复杂度迭代整个列表，其中n是列表中的元素数。
     *
     * @return 一个按照给定比较器确定的顺序提供这个分类列表的元素的迭代器。
     */
    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    // 使用后继方法的迭代器接口的实现
    // 为了提高速度至O(n)，我们通过列表进行迭代，而不是O(n*log(n))的排序。
    private class Itr implements Iterator<T> {
        private Node nextNode = (isEmpty() ? null : findNodeAtIndex(0));
        private int nextIndex = 0;
        private Node lastReturned = null;
        /**
         * 此迭代器预期的modCount
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

            // 下一个节点现在可能不正确，所以需要再次获取它。
            nextIndex--;
            if (nextIndex < size()) { // 检查具有此索引的节点是否确实存在。
                nextNode = findNodeAtIndex(nextIndex);
            } else {
                nextNode = null;
            }

            expectedModCount = modCount;
        }

        /**
         * 检查modcount是否为预期值
         */
        private void checkModCount() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * @return 存储在此SortedList中的元素数量。
     */
    @Override
    public int size() {
        return (root == null) ? 0 : 1 + root.numChildren;
    }

    /**
     * @return 此SortedList的根节点，如果此列表为空，则为空。
     */
    protected Node getRoot() {
        return root;
    }

    /**
     * 返回给定对象是否在此SortedList中。
     * <p>
     * 元素比较使用{@link Object#equals(Object)}方法，并假设给定的obj必须具有与此SortedList中的元素相等的T类型。
     * 时间复杂度为<i>O(log(n))</i>，其中n是列表中的元素数。
     *
     * @param obj 要检查存在性的对象
     * @return 是否存在于此SortedList中
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object obj) {
        return obj != null
                && !isEmpty()
                && findFirstNodeWithValue((T) obj) != null;
    }

    /**
     * 返回表示树中给定值的节点，如果不存在此类节点，则该节点可以为null。
     * <p>
     * 该方法使用给定的比较器执行二进制搜索，时间复杂度为O(log(n))。
     *
     * @param value 要搜索的值
     * @return 此列表中具有给定值的第一个节点
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
     * 移除并返回此SortedList中给定索引处的元素。由于列表已排序，这是从0-n-1开始计数的第四个最小元素。
     * <p>
     * 例如，调用remove(0)将删除列表中最小的元素。
     *
     * @param index 要删除的元素的索引
     * @return 被删除的元素
     * @throws IllegalArgumentException 如果索引不是有效的索引则抛出此异常
     */
    @Override
    public T remove(int index) {
        // 在索引处获取节点，如果节点索引不存在，将抛出异常。
        Node nodeAtIndex = findNodeAtIndex(index);
        remove(nodeAtIndex);
        return nodeAtIndex.value;
    }

    /**
     * 删除列表中具有给定值的第一个元素（如果存在这样的节点），否则不执行任何操作。使用给定的比较器对元素进行比较。
     * <p>
     * 返回是否找到并删除了匹配的元素。
     *
     * @param value 要移除的元素
     * @return 是否找到并删除了匹配的元素。
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
     * 从这个SortedList中删除给定的节点，如果需要重新平衡，也会添加modCount。
     * 时间复杂度O(log(n))。
     *
     * @param toRemove 此SortedList中的节点
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
     * 返回此SortedList中给定索引处的元素。由于列表已排序，因此这是从0-n-1开始计算的第四个最小元素的索引。
     * <p>
     * 例如，调用get(0)将返回列表中最小的元素。
     *
     * @param index 要获取的元素的索引
     * @return 此SortedList中给定索引处的元素
     * @throws IllegalArgumentException 如果索引不是有效的索引则抛出此异常
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
     * 返回整个列表中的最小平衡因子。仅供测试使用
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
     * 返回整个列表中的最大平衡因子。仅供测试使用
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

    //从startNode开始执行二叉树的再平衡，并向上递归树。..
    private void rebalanceTree(Node startNode) {
        Node current = startNode;
        while (current != null) {
            //获取此时左右子树之间的差异。
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
     * 用于表示树中位置的内部类。每个节点存储一个相等值的列表，包含它们的子节点和父节点、在该点扎根的子树的高度以及它们拥有的子元素的总数。
     * <p>
     * 顺便提一句，这里的left和right仅仅是处于中国人描述数据结构的习惯，并不是影射某些买办/新官僚政治立场左右摇晃，本人坚定信仰中国特色社会主义。
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
         * 此节点的唯一id：自动生成，节点越新，此值越高。
         */
        protected final int id;

        protected Node(T t) {
            this.value = t;
            this.id = NEXT_NODE_ID++;
        }

        protected boolean hasTwoChildren() {
            return leftChild != null && rightChild != null;
        }

        // 如果是叶节点，则删除该节点，并更新树中的子节点数和高度。
        private void detachFromParentIfLeaf() {
            if (!isLeaf() || parent == null) {
                throw new RuntimeException("Call made to detachFromParentIfLeaf, but this is not a leaf node with a parent!");
            }
            if (isLeftChildOfParent()) {
                parent.setLeftChild(null);
            } else {
                parent.setRightChild(null);
            }
        }

        /**
         * 返回此节点的父节点，该节点可能为空。
         *
         * @return 此节点的父节点（如果存在），否则为null
         */
        protected Node getGrandParent() {
            return (parent != null && parent.parent != null) ? parent.parent : null;
        }

        // 将此节点在树上向上移动一个槽口，更新值并重新平衡树。
        private void contractParent() {
            if (parent == null || parent.hasTwoChildren()) {
                throw new RuntimeException("Can not call contractParent on root node or when the parent has two children!");
            }
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
                root = this; // 如果在其他地方没有进行更新就重设根
            }

            // 最后，更新值并重新平衡这颗平衡二叉树。
            updateCachedValues();
            rebalanceTree(this);
        }

        /**
         * 返回它是否是其父节点的左子节点；如果这是根节点，则返回false。
         *
         * @return 如果这是其父节点的左子节点，则为true，否则为false
         */
        public boolean isLeftChildOfParent() {
            return parent != null && parent.leftChild == this;
        }

        /**
         * 返回它是否是其父节点的右子节点；如果这是根节点，则返回false。
         *
         * @return 如果这是其父节点的右子节点，则为true，否则为false
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
         * 使用比较器将存储在该节点上的值与给定节点上的值进行比较，如果这些值相等，则比较其ID上的节点。我们认定较老的节点较小。
         *
         * @return 如果比较器在比较存储在该节点和给定节点上的值时返回一个非零数字，则返回该数字，否则返回该节点的id减去给定节点的id
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
         * 获取树中下一个最大的节点，如果这是值最大的节点，则为null。
         *
         * @return 树中下一个最大的节点，如果这是值最大的节点，则为null
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
         * 获取树中下一个最小的节点，如果这是值最小的节点，则为null。
         *
         * @return 树中下一个最小的节点，如果这是值最小的节点，则为null
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

        // 将子节点设置为左/右，仅当给定节点为null或叶，且当前子节点相同时才应如此
        private void setChild(boolean isLeft, Node leaf) {
            //perform the update..
            if (leaf != null) {
                leaf.parent = this;
            }
            if (isLeft) {
                leftChild = leaf;
            } else {
                rightChild = leaf;
            }

            // 确保树高的任何变化都得到了处理。
            updateCachedValues();
            rebalanceTree(this);
        }

        /**
         * 返回此节点是否为叶节点，如果其左和右子级都设置为null，则它就是叶子<del>姐姐</del>。
         *
         * @return 如果此节点为叶节点，则为true，否则为false。
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

        // 使用当前节点作为轴左旋。
        private void leftRotateAsPivot() {
            if (parent == null || parent.rightChild != this) {
                throw new RuntimeException("Can't left rotate as pivot has no valid parent node.");
            }

            // 首先将此节点向上移动，分离父节点。
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

            Node oldLeftChild = leftChild;
            oldParent.parent = this;
            leftChild = oldParent;
            if (oldLeftChild != null) {
                oldLeftChild.parent = oldParent;
            }
            oldParent.rightChild = oldLeftChild;

            oldParent.updateCachedValues();
        }

        /**
         * 返回此节点的子节点数加一。此方法使用缓存的变量，确保它在恒定时间内运行。
         *
         * @return 此节点的子节点数加一
         */
        public int sizeOfSubTree() {
            return 1 + numChildren;
        }

        public T getValue() {
            return value;
        }

        private void rightRotateAsPivot() {
            if (parent == null || parent.leftChild != this) {
                throw new RuntimeException("Can't right rotate as pivot has no valid parent node.");
            }

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
            Node oldRightChild = rightChild;
            rightChild = oldParent;
            if (oldRightChild != null) {
                oldRightChild.parent = oldParent;
            }
            oldParent.leftChild = oldRightChild;

            oldParent.updateCachedValues();
        }

        /**
         * 更新此路径上节点的高度和子节点数。还为路径上的每个节点（包括此节点）调用{@link #updateAdditionalCachedValues()}
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
         * 当从树中插入或删除节点时被调用，并为子类提供一个钩子来更新其缓存值。
         * <p>
         * 每次更改列表时都会调用此方法。它首先在受给定更改影响的最深节点上调用，然后在该节点的祖先上调用，直到在根节点上调用为止。
         * <p>
         * 因此，它仅适用于在缓存值非全局且不依赖于计算时具有正确值的父节点的情况下更新缓存值。
         * <p>
         * 这个实现是空的，留给子类使用（虽然我觉得没人会用）。
         */
        protected void updateAdditionalCachedValues() {

        }

        // 将此节点中的值替换为其他节点中的值。
        // 应该只在需要删除并且只有一个值时调用。
        private void switchValuesForThoseIn(Node other) {
            this.value = other.value;
        }

        private int getBalanceFactor() {
            return ((leftChild == null) ? 0 : leftChild.height + 1) -
                    ((rightChild == null) ? 0 : rightChild.height + 1);
        }

        private void setLeftChild(Node leaf) {
            if ((leaf != null && !leaf.isLeaf()) || (leftChild != null && !leftChild.isLeaf())) {
                throw new RuntimeException("setLeftChild should only be called with null or a leaf node, to replace a likewise child node.");
            }
            setChild(true, leaf);
        }

        private void setRightChild(Node leaf) {
            if ((leaf != null && !leaf.isLeaf()) || (rightChild != null && !rightChild.isLeaf())) {
                throw new RuntimeException("setRightChild should only be called with null or a leaf node, to replace a likewise child node.");
            }
            setChild(false, leaf);
        }
    }
}
