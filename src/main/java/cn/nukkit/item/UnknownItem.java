package cn.nukkit.item;

import org.jetbrains.annotations.NotNull;

public class UnknownItem extends Item {
    public UnknownItem(@NotNull String id) {
        super(id);
    }

    public UnknownItem(@NotNull String id, int meta) {
        super(id, meta);
    }

    public UnknownItem(@NotNull String id, int meta, int count) {
        super(id, meta, count);
    }
}
