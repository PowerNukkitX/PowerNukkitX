package org.powernukkitx.block.customblock.component;

import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

/**
 * Transformation component for custom blocks.
 * Supports rotation, scaling, and translation.
 */
public class TransformationComponent implements BlockComponent {
    private Vector3 translation = new Vector3(0, 0, 0);
    private Vector3 scale = new Vector3(1, 1, 1);
    private Vector3 rotation = new Vector3(0, 0, 0);

    public TransformationComponent() {
    }

    public TransformationComponent translation(Vector3 translation) {
        this.translation = translation != null ? translation : new Vector3(0, 0, 0);
        return this;
    }

    public TransformationComponent scale(Vector3 scale) {
        this.scale = scale != null ? scale : new Vector3(1, 1, 1);
        return this;
    }

    public TransformationComponent rotation(Vector3 rotation) {
        this.rotation = rotation != null ? rotation : new Vector3(0, 0, 0);
        return this;
    }

    public TransformationComponent rotation(double x, double y, double z) {
        this.rotation = new Vector3(x, y, z);
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.TRANSFORMATION;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putList("translation", new ListTag<FloatTag>()
                .add(new FloatTag((float) translation.x))
                .add(new FloatTag((float) translation.y))
                .add(new FloatTag((float) translation.z)));
        tag.putList("scale", new ListTag<FloatTag>()
                .add(new FloatTag((float) scale.x))
                .add(new FloatTag((float) scale.y))
                .add(new FloatTag((float) scale.z)));
        tag.putList("rotation", new ListTag<FloatTag>()
                .add(new FloatTag((float) rotation.x))
                .add(new FloatTag((float) rotation.y))
                .add(new FloatTag((float) rotation.z)));
        return tag;
    }
}