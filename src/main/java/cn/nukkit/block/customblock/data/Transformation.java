package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * supports rotation, scaling, and translation. The component can be added to the whole block and/or to individual block permutations. Transformed geometries still have the same restrictions that non-transformed geometries have such as a maximum size of 30/16 units.
 */
@PowerNukkitXOnly
@Since("1.19.80-r1")
public record Transformation(Vector3 translation, Vector3 scale, Vector3 rotation) implements NBTData {

    @Override
    public CompoundTag toCompoundTag() {
        return new CompoundTag("minecraft:transformation")
                .putInt("RX", rotation.getFloorX())
                .putInt("RY", rotation.getFloorY())
                .putInt("RZ", rotation.getFloorZ())
                .putFloat("SX", (float) scale.x)
                .putFloat("SY", (float) scale.y)
                .putFloat("SZ", (float) scale.z)
                .putFloat("TX", (float) translation.x)
                .putFloat("TY", (float) translation.x)
                .putFloat("TZ", (float) translation.x);
    }
}
