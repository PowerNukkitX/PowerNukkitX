package cn.nukkit.camera.data;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author daoge_cmd
 * @date 2023/6/11
 * PowerNukkitX Project
 */


public record Ease(float time, EaseType easeType) implements SerializableData {
    @Override
    public CompoundTag serialize() {
        return new CompoundTag()//ease
                .putFloat("time", time)
                .putString("type", easeType.getType());
    }
}
