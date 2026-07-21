package org.powernukkitx.block.customblock.component;

import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

/**
 * Component defining the selection box (outline when looking at block).
 */
public class SelectionBoxComponent implements BlockComponent {
    private Vector3f origin = new Vector3f(-8, 0, -8);
    private Vector3f size = new Vector3f(16, 16, 16);
    private boolean enabled = true;

    public SelectionBoxComponent() {
    }

    public SelectionBoxComponent origin(@org.jetbrains.annotations.NotNull Vector3f origin) {
        this.origin = origin;
        return this;
    }

    public SelectionBoxComponent origin(float x, float y, float z) {
        this.origin = new Vector3f(x, y, z);
        return this;
    }

    public SelectionBoxComponent size(@org.jetbrains.annotations.NotNull Vector3f size) {
        this.size = size;
        return this;
    }

    public SelectionBoxComponent size(float x, float y, float z) {
        this.size = new Vector3f(x, y, z);
        return this;
    }

    public SelectionBoxComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.SELECTION_BOX;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enabled", enabled);
        tag.putList("origin", new ListTag<FloatTag>()
                .add(new FloatTag(origin.x))
                .add(new FloatTag(origin.y))
                .add(new FloatTag(origin.z)));
        tag.putList("size", new ListTag<FloatTag>()
                .add(new FloatTag(size.x))
                .add(new FloatTag(size.y))
                .add(new FloatTag(size.z)));
        return tag;
    }
}