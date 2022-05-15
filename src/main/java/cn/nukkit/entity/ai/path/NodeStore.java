package cn.nukkit.entity.ai.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NodeStore {
    void add(@NotNull Node node);

    boolean has(long nodeHashCode);

    default boolean has(@NotNull Node node) {
        return has(node.nodeHashCode());
    }

    @Nullable
    Node get(long nodeHashCode);

    @Nullable
    Node remove(long nodeHashCode);

    default Node remove(Node node) {
        return remove(node.nodeHashCode());
    }

    int size();

    void clear();
}
