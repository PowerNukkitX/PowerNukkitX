package cn.nukkit.entity.path;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedSet;
import java.util.TreeSet;

public final class SortedNodeStoreImpl implements SortedNodeStore {
    private final SortedSet<Node> sortedNodes = new TreeSet<>();
    private final Long2ObjectOpenHashMap<Node> nodes = new Long2ObjectOpenHashMap<>();

    public void add(@NotNull Node node) {
        sortedNodes.add(node);
        nodes.put(node.nodeHashCode(), node);
    }

    public boolean has(long nodeHashCode) {
        return nodes.containsKey(nodeHashCode);
    }

    @Nullable
    public Node remove(long nodeHashCode) {
        var tmp = nodes.get(nodeHashCode);
        if (tmp == null) return null;
        sortedNodes.remove(tmp);
        return tmp;
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Nullable
    public Node peekMinCost() {
        return sortedNodes.first();
    }

    @Nullable
    public Node popMinCost() {
        var tmp = sortedNodes.first();
        sortedNodes.remove(tmp);
        return tmp;
    }

    @Nullable
    public Node peekMaxCost() {
        return sortedNodes.last();
    }

    @Nullable
    public Node popMaxCost() {
        var tmp = sortedNodes.last();
        sortedNodes.remove(tmp);
        return tmp;
    }

    public void resortNode(long nodeHashCode) {
        var tmp = nodes.get(nodeHashCode);
        if (tmp != null) {
            sortedNodes.remove(tmp);
            sortedNodes.add(tmp);
        }
    }

    public void clear() {
        nodes.clear();
        sortedNodes.clear();
    }
}
