package cn.nukkit.block.customblock.data;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;


public record CollisionBox(double originX, double originY, double originZ, double sizeX, double sizeY,
                           double sizeZ) implements NBTData {
    public CompoundTag toCompoundTag() {
        float minX = (float) (originX + 8f);
        float minY = (float) originY;
        float minZ = (float) (originZ + 8f);

        float maxX = minX + (float) sizeX;
        float maxY = minY + (float) sizeY;
        float maxZ = minZ + (float) sizeZ;

        ListTag<CompoundTag> boxes = new ListTag<>();
        boxes.add(new CompoundTag()
                .putFloat("minX", minX)
                .putFloat("minY", minY)
                .putFloat("minZ", minZ)
                .putFloat("maxX", maxX)
                .putFloat("maxY", maxY)
                .putFloat("maxZ", maxZ)
        );

        return new CompoundTag()
                .putBoolean("enabled", true)
                .putList("boxes", boxes);
    }
}
