package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.base.Preconditions;
import io.netty.util.internal.EmptyArrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-12
 */
@PowerNukkitOnly
@Since("FUTURE")
public class StringItem extends Item {

    private final String id;

    private byte[] customCompound = EmptyArrays.EMPTY_BYTES;

    private static String notEmpty(String value) {
        Preconditions.checkArgument(value != null && !value.trim().isEmpty(), "The name cannot be empty");
        return value;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public StringItem(@Nonnull String id, @Nullable String name) {
        super(STRING_IDENTIFIED_ITEM, 0, 1, notEmpty(name));
        Preconditions.checkNotNull(id, "id can't be null");
        Preconditions.checkArgument(id.contains(":"), "The ID must be a namespaced ID, like minecraft:stone");
        this.id = id;
        clearNamedTag();
    }

    @Override
    public boolean hasCompoundTag() {
        return true;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean hasCustomCompoundTag() {
        return customCompound != null && customCompound.length > 0;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public byte[] getCustomCompoundTag() {
        return customCompound;
    }

    @Override
    public CompoundTag getNamedTag() {
        if (!super.hasCompoundTag()) {
            clearNamedTag();
            return super.getNamedTag();
        }

        return super.getNamedTag().putString("Name", id);
    }

    @Override
    public Item setCompoundTag(byte[] tags) {
        CompoundTag compoundTag = (tags == null || tags.length == 0)? new CompoundTag() : parseCompoundTag(tags);
        return this.setNamedTag(compoundTag);
    }

    @Override
    public Item setNamedTag(CompoundTag tag) {
        tag = tag.clone().remove("Name");
        if (tag.isEmpty()) {
            customCompound = EmptyArrays.EMPTY_BYTES;
        } else {
            customCompound = writeCompoundTag(tag);
        }
        return super.setNamedTag(tag.putString("Name", id));
    }

    @Override
    public Item clearNamedTag() {
        customCompound = EmptyArrays.EMPTY_BYTES;
        return super.setCompoundTag(new CompoundTag().putString("Name", id));
    }

    @Override
    public final int getId() {
        return super.getId();
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Override
    public final String getNamespaceId() {
        return id;
    }

    @Override
    public StringItem clone() {
        return (StringItem) super.clone();
    }
}
