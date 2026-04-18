package cn.nukkit.block.customblock.data;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;


public record SelectionBox(double originX, double originY, double originZ, double sizeX, double sizeY,
                           double sizeZ) implements NBTData {
    public NbtMap toCompoundTag() {
        return NbtMap.builder()
                .putBoolean("enabled", true)
                .putList("origin", NbtType.FLOAT, Arrays.asList((float) originX, (float) originY, (float) originZ))
                .putList("size", NbtType.FLOAT, Arrays.asList((float) sizeX, (float) sizeY, (float) sizeZ))
                .build();
    }
}