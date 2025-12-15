package cn.nukkit.camera.data;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/11
 */


public record Pos(float x, float y, float z) implements SerializableData {
    public CompoundTag serialize() {
        return new CompoundTag()//pos
                .putList("pos", new ListTag<>()
                        .add(new FloatTag(x))
                        .add(new FloatTag(y))
                        .add(new FloatTag(z))
                );
    }
}
