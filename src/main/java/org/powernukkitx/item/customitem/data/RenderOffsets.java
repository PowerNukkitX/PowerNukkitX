package org.powernukkitx.item.customitem.data;

import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import javax.annotation.Nullable;

/**
 * RenderOffsets is the component that sets the render_offsets item. Parameters can be set to offset the rendering of items in different views.
 */


public class RenderOffsets {
    public final CompoundTag nbt = new CompoundTag();

    /**
     * Set rendering offsets for custom items at different viewpoints
     *
     * @param mainHandFirstPerson Set the offset of the first person main hand item
     * @param mainHandThirdPerson Set the offset of the third person main hand item
     * @param offHandFirstPerson  Set the offset of the first person offhand item
     * @param offHandThirdPerson  Set the offset of the third person offhand item
     */
    public RenderOffsets(@Nullable Offset mainHandFirstPerson, @Nullable Offset mainHandThirdPerson, @Nullable Offset offHandFirstPerson, @Nullable Offset offHandThirdPerson) {
        if (mainHandFirstPerson != null || mainHandThirdPerson != null) {
            this.nbt.putCompound("main_hand", new CompoundTag());
            if (mainHandFirstPerson != null) {
                this.nbt.getCompound("main_hand").putCompound("first_person", xyzToCompoundTag(mainHandFirstPerson.getPosition(), mainHandFirstPerson.getRotation(), mainHandFirstPerson.getScale()));
            }
            if (mainHandThirdPerson != null) {
                this.nbt.getCompound("main_hand").putCompound("third_person", xyzToCompoundTag(mainHandThirdPerson.getPosition(), mainHandThirdPerson.getRotation(), mainHandThirdPerson.getScale()));
            }
        }
        if (offHandFirstPerson != null || offHandThirdPerson != null) {
            this.nbt.putCompound("off_hand", new CompoundTag());
            if (offHandFirstPerson != null) {
                this.nbt.getCompound("off_hand").putCompound("first_person", xyzToCompoundTag(offHandFirstPerson.getPosition(), offHandFirstPerson.getRotation(), offHandFirstPerson.getScale()));
            }
            if (offHandThirdPerson != null) {
                this.nbt.getCompound("off_hand").putCompound("third_person", xyzToCompoundTag(offHandThirdPerson.getPosition(), offHandThirdPerson.getRotation(), offHandThirdPerson.getScale()));
            }
        } else if (mainHandFirstPerson == null && mainHandThirdPerson == null)
            throw new IllegalArgumentException("Do not allow all parameters to be empty, if you do not want to specify, please do not use the renderOffsets method");
    }

    /**
     * Adjusts the scale offset of the first-person main hand with the specified multiplier
     *
     * @param multiplier Scaling the item to the specified scale multiplier number, which only affects the scale, so the item position may not be correct.
     * @return the render offsets
     */
    public static RenderOffsets scaleOffset(double multiplier) {
        if (multiplier < 0) {
            multiplier = 1;
        }
        float scale1 = (float) (0.075 / multiplier);
        float scale2 = (float) (0.125 / multiplier);
        float scale3 = (float) (0.075 / (multiplier * 2.4f));
        return new RenderOffsets(
                Offset.builder().scale(scale3, scale3, scale3),
                Offset.builder().scale(scale1, scale2, scale1),
                Offset.builder().scale(scale1, scale2, scale1),
                Offset.builder().scale(scale1, scale2, scale1)
        );
    }

    /**
     * Scale to a standard 16x16 pixel item display at the specified item texture size
     *
     * @param textureSize Specify the pixel size of the item texture, which can only be a multiple of 16.
     * @return the render offsets
     */
    public static RenderOffsets scaleOffset(int textureSize) {
        double multiplier = textureSize / 16f;
        return scaleOffset(multiplier);
    }

    private CompoundTag xyzToCompoundTag(Vector3f pos, Vector3f rot, Vector3f sc) {
        var result = new CompoundTag();
        if (pos != null) {
            var position = new ListTag<FloatTag>();
            position.add(new FloatTag(pos.x));
            position.add(new FloatTag(pos.y));
            position.add(new FloatTag(pos.z));
            result.putList("position", position);
        }
        if (rot != null) {
            var rotation = new ListTag<FloatTag>();
            rotation.add(new FloatTag(rot.x));
            rotation.add(new FloatTag(rot.y));
            rotation.add(new FloatTag(rot.z));
            result.putList("rotation", rotation);
        }
        if (sc != null) {
            var scale = new ListTag<FloatTag>();
            scale.add(new FloatTag(sc.x));
            scale.add(new FloatTag(sc.y));
            scale.add(new FloatTag(sc.z));
            result.putList("scale", scale);
        }
        return result;
    }
}