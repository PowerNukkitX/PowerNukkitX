package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * The SelectionBox class represents a selection box with origin coordinates and size dimensions.
 */
public record SelectionBox(double originX, double originY, double originZ, double sizeX, double sizeY,
                           double sizeZ) implements NBTData {

    /**
     * Converts the SelectionBox instance to a CompoundTag.
     *
     * @return A CompoundTag representing the SelectionBox instance.
     */
    @Override
    public CompoundTag toCompoundTag() {
        return new CompoundTag()
                .putBoolean("enabled", true)
                .putList("origin", new ListTag<>()
                        .add(new FloatTag((float) originX))
                        .add(new FloatTag((float) originY))
                        .add(new FloatTag((float) originZ)))
                .putList("size", new ListTag<>()
                        .add(new FloatTag((float) sizeX))
                        .add(new FloatTag((float) sizeY))
                        .add(new FloatTag((float) sizeZ)));
    }
}