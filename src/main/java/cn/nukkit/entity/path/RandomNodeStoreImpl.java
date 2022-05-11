package cn.nukkit.entity.path;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RandomNodeStoreImpl implements NodeStore {
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

    public int size() {
        return nodes.size();
    }
}
