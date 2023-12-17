package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;


public record CollisionBox(double originX, double originY, double originZ, double sizeX, double sizeY,
                           double sizeZ) implements NBTData {
    public CompoundTag toCompoundTag() {
        return new CompoundTag("minecraft:collision_box")
                .putBoolean("enabled", true)
                .putList(new ListTag<FloatTag>("origin")
                        .add(new FloatTag("", (float) originX))
                        .add(new FloatTag("", (float) originY))
                        .add(new FloatTag("", (float) originZ)))
                .putList(new ListTag<FloatTag>("size")
                        .add(new FloatTag("", (float) sizeX))
                        .add(new FloatTag("", (float) sizeY))
                        .add(new FloatTag("", (float) sizeZ)));
    }
}
