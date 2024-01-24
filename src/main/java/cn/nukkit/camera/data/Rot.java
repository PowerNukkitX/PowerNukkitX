package cn.nukkit.camera.data;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author daoge_cmd
 * @date 2023/6/11
 * PowerNukkitX Project
 */
public record Rot(float x, float y) implements SerializableData {
    public CompoundTag serialize() {
        return new CompoundTag()//"rot"
                .putFloat("x", x)
                .putFloat("y", y);
    }
}
