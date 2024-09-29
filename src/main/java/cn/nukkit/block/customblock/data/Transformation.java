package cn.nukkit.block.customblock.data;

import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * The Transformation class supports rotation, scaling, and translation. The component can be added to the whole block
 * and/or to individual block permutations. Transformed geometries still have the same restrictions that non-transformed
 * geometries have such as a maximum size of 30/16 units.
 */
public record Transformation(Vector3 translation, Vector3 scale, Vector3 rotation) implements NBTData {

    /**
     * Converts the Transformation instance to a CompoundTag.
     *
     * @return A CompoundTag representing the Transformation instance.
     */
    @Override
    public CompoundTag toCompoundTag() {
        int rx = (rotation.getFloorX() % 360) / 90;
        int ry = (rotation.getFloorY() % 360) / 90;
        int rz = (rotation.getFloorZ() % 360) / 90;
        return new CompoundTag()
                .putInt("RX", rx)
                .putInt("RY", ry)
                .putInt("RZ", rz)
                .putFloat("SX", (float) scale.x)
                .putFloat("SY", (float) scale.y)
                .putFloat("SZ", (float) scale.z)
                .putFloat("TX", (float) translation.x)
                .putFloat("TY", (float) translation.y)
                .putFloat("TZ", (float) translation.z);
    }
}