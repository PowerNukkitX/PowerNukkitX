package org.powernukkitx.block.customblock.component;

import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Component defining the collision box of a block.
 * Allows defining multiple collision boxes for complex shapes.
 */
public class CollisionBoxComponent implements BlockComponent {
    private final List<CompoundTag> boxes = new ArrayList<>();
    private boolean enabled = true;

    public CollisionBoxComponent() {
    }

    /**
     * Add a collision box to this component.
     *
     * @param origin the origin of the box (relative to block center, in 16ths)
     * @param size   the size of the box (in 16ths)
     * @return this component for chaining
     */
    public CollisionBoxComponent addBox(@org.jetbrains.annotations.NotNull Vector3f origin, @org.jetbrains.annotations.NotNull Vector3f size) {
        float minX = origin.x - 8f;
        float minY = origin.y;
        float minZ = origin.z - 8f;
        float maxX = minX + size.x;
        float maxY = minY + size.y;
        float maxZ = minZ + size.z;

        CompoundTag box = new CompoundTag()
                .putFloat("minX", minX)
                .putFloat("minY", minY)
                .putFloat("minZ", minZ)
                .putFloat("maxX", maxX)
                .putFloat("maxY", maxY)
                .putFloat("maxZ", maxZ);

        boxes.add(box);
        return this;
    }

    /**
     * Add a collision box using min/max coordinates directly.
     *
     * @param minX minimum X coordinate
     * @param minY minimum Y coordinate
     * @param minZ minimum Z coordinate
     * @param maxX maximum X coordinate
     * @param maxY maximum Y coordinate
     * @param maxZ maximum Z coordinate
     * @return this component for chaining
     */
    public CollisionBoxComponent addBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        CompoundTag box = new CompoundTag()
                .putFloat("minX", minX)
                .putFloat("minY", minY)
                .putFloat("minZ", minZ)
                .putFloat("maxX", maxX)
                .putFloat("maxY", maxY)
                .putFloat("maxZ", maxZ);

        boxes.add(box);
        return this;
    }

    public CollisionBoxComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.COLLISION_BOX;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enabled", enabled);
        ListTag<CompoundTag> boxesTag = new ListTag<>(CompoundTag.class);
        boxes.forEach(boxesTag::add);
        tag.putList("boxes", boxesTag);
        return tag;
    }
}