package cn.nukkit.block.customblock.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.List;


public record CollisionBox(double originX, double originY, double originZ, double sizeX, double sizeY,
                           double sizeZ) implements NBTData {
    public NbtMap toCompoundTag() {
        float minX = (float) (originX + 8f);
        float minY = (float) originY;
        float minZ = (float) (originZ + 8f);

        float maxX = minX + (float) sizeX;
        float maxY = minY + (float) sizeY;
        float maxZ = minZ + (float) sizeZ;

        List<NbtMap> boxes = new ObjectArrayList<>();
        boxes.add(NbtMap.builder()
                .putFloat("minX", minX)
                .putFloat("minY", minY)
                .putFloat("minZ", minZ)
                .putFloat("maxX", maxX)
                .putFloat("maxY", maxY)
                .putFloat("maxZ", maxZ)
                .build()
        );

        return NbtMap.builder()
                .putBoolean("enabled", true)
                .putList("boxes", NbtType.COMPOUND, boxes)
                .build();
    }
}
