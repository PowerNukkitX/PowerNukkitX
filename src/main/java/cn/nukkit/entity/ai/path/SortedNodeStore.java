package cn.nukkit.entity.ai.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SortedNodeStore extends NodeStore {
    @Nullable
    Node peekMinCost();

    @Nullable
    Node popMinCost();

    @Nullable
    Node peekMaxCost();

    @Nullable
    Node popMaxCost();

    default void resortNode(@NotNull Node node) {
        resortNode(node.nodeHashCode());
    }

    void resortNode(long nodeHashCode);
}
