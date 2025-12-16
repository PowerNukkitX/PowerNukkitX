package cn.nukkit.camera.data;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/11
 */


public record Ease(float time, EaseType easeType) implements SerializableData {
    @Override
    public CompoundTag serialize() {
        return new CompoundTag()//ease
                .putFloat("time", time)
                .putString("type", easeType.getType());
    }
}
