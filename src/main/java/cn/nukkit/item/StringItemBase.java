package cn.nukkit.item;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public abstract class StringItemBase extends Item implements StringItem {
    private final String id;

    public StringItemBase(@NotNull String id, String name) {
        super(STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        Preconditions.checkNotNull(id, "id can't be null");
        Preconditions.checkArgument(id.contains(":"), "The ID must be a namespaced ID, like minecraft:stone");
        this.id = id;
        clearNamedTag();
    }

    public StringItemBase(@NotNull String id, int meta, int count, String name) {
        super(STRING_IDENTIFIED_ITEM, meta, count, StringItem.notEmpty(name));
        Preconditions.checkNotNull(id, "id can't be null");
        Preconditions.checkArgument(id.contains(":"), "The ID must be a namespaced ID, like minecraft:stone");
        this.id = id;
        clearNamedTag();
    }

    @Override
    public String getNamespaceId() {
        return this.id;
    }

    @Override
    public final int getId() {
        return StringItem.super.getId();
    }

    @Override
    public StringItemBase clone() {
        return (StringItemBase) super.clone();
    }
}
