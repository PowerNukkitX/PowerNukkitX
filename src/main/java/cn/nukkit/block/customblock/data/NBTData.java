package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Interface for converting objects to a CompoundTag.
 */
public interface NBTData {
    /**
     * Converts the implementing object to a CompoundTag.
     *
     * @return A CompoundTag representing the implementing object.
     */
    CompoundTag toCompoundTag();
}