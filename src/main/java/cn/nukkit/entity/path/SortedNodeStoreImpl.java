package cn.nukkit.entity.path;

import cn.nukkit.utils.SortedList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class SortedNodeStoreImpl implements SortedNodeStore {
    private final SortedList<Node> sortedNodes = new SortedList<>(Node::compareTo);
    private final Long2ObjectOpenHashMap<Node> nodes = new Long2ObjectOpenHashMap<>();

    public void add(@NotNull Node node) {
        if (has(node)) {
            return;
        }
        sortedNodes.add(node);
        nodes.put(node.nodeHashCode(), node);
        assertSize();
    }

    public boolean has(long nodeHashCode) {
        return nodes.containsKey(nodeHashCode);
    }

    @Nullable
    @Override
    public Node get(long nodeHashCode) {
        return nodes.get(nodeHashCode);
    }

    @Nullable
    public Node remove(long nodeHashCode) {
        var tmp = nodes.get(nodeHashCode);
        if (tmp == null) return null;
        sortedNodes.remove(tmp);
        nodes.remove(tmp.nodeHashCode());
        assertSize();
        return tmp;
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Nullable
    public Node peekMinCost() {
        return sortedNodes.get(0);
    }

    @Nullable
    public Node popMinCost() {
        var tmp = sortedNodes.get(0);
        sortedNodes.remove(tmp);
        if (tmp != null)
            nodes.remove(tmp.nodeHashCode());
        assertSize();
        return tmp;
    }

    @Nullable
    public Node peekMaxCost() {
        return sortedNodes.get(sortedNodes.size() - 1);
    }

    @Nullable
    public Node popMaxCost() {
        var tmp = sortedNodes.get(sortedNodes.size() - 1);
        sortedNodes.remove(tmp);
        if (tmp != null)
            nodes.remove(tmp.nodeHashCode());
        assertSize();
        return tmp;
    }

    public void resortNode(long nodeHashCode) {
        var tmp = nodes.get(nodeHashCode);
        if (tmp != null) {
            sortedNodes.remove(tmp);
            nodes.remove(nodeHashCode);
            sortedNodes.add(tmp);
            nodes.put(nodeHashCode, tmp);
        }
        assertSize();
    }

    private void assertSize() {
        if (nodes.size() != sortedNodes.size()) {
            throw new AssertionError("Wrong size!");
        }
    }

    public void clear() {
        nodes.clear();
        sortedNodes.clear();
    }

    @Override
    public String toString() {
        return "SortedNodeStoreImpl{" +
                "sortedNodes=" + sortedNodes +
                ", nodes=" + nodes +
                '}';
    }
}
