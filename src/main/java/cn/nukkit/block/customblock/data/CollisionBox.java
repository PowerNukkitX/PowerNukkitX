package cn.nukkit.block.customblock.data;

import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;

public record CollisionBox(@NotNull Vector3f origin, @NotNull Vector3f size) implements NBTData {
    public CompoundTag toCompoundTag() {
        return new CompoundTag("minecraft:collision_box")
                .putBoolean("enabled", true)
                .putList(new ListTag<FloatTag>("origin")
                        .add(new FloatTag("", origin.x))
                        .add(new FloatTag("", origin.y))
                        .add(new FloatTag("", origin.z)))
                .putList(new ListTag<FloatTag>("size")
                        .add(new FloatTag("", size.x))
                        .add(new FloatTag("", size.y))
                        .add(new FloatTag("", size.z)));
    }
}
