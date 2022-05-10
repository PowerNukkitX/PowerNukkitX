package cn.nukkit.entity.path;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedSet;
import java.util.TreeSet;

public final class RandomNodeStore {
    private final Long2ObjectOpenHashMap<Node> nodes = new Long2ObjectOpenHashMap<>();

    public void add(@NotNull Node node) {
        nodes.put(node.nodeHashCode(), node);
    }

    public boolean has(long nodeHashCode) {
        return nodes.containsKey(nodeHashCode);
    }

    @Nullable
    public Node remove(long nodeHashCode) {
        return nodes.remove(nodeHashCode);
    }

    public void clear() {
        nodes.clear();
    }
}
