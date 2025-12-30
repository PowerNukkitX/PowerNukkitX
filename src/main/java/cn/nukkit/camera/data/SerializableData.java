package cn.nukkit.camera.data;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/11
 */


public interface SerializableData {
    CompoundTag serialize();
}
