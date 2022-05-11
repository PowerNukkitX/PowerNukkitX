package cn.nukkit.entity.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NodeStore {
    void add(@NotNull Node node);

    boolean has(long nodeHashCode);

    @Nullable
    Node remove(long nodeHashCode);

    int size();

    void clear();
}
