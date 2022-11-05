package cn.nukkit.item.customitem.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import javax.annotation.Nullable;

/**
 * RenderOffsets是设置 render_offsets 项目组件。可以设置参数来偏移物品的在不同视角下的呈现方式。
 * <p>
 * RenderOffsets is the component that sets the render_offsets item. Parameters can be set to offset the rendering of items in different views.
 */
@PowerNukkitXOnly
@Since("1.19.31-r1")
public class RenderOffsets {
    public final CompoundTag nbt = new CompoundTag();

    /**
     * 设置自定义物品在不同视角下的渲染偏移量
     * <p>
     * Set rendering offsets for custom items at different viewpoints
     *
     * @param mainHandFirstPerson 设置第一人称主手物品的偏移量<br>Set the offset of the first person main hand item
     * @param mainHandThirdPerson 设置第三人称主手物品的偏移量<br>Set the offset of the third person main hand item
     * @param offHandFirstPerson  设置第一人称副手物品的偏移量<br>Set the offset of the first person offhand item
     * @param offHandThirdPerson  设置第三人称副手物品的偏移量<br>Set the offset of the third person offhand item
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
     * 以指定参数调整第一人称主手的scale偏移量
     * <p>
     * Adjusts the scale offset of the first-person main hand with the specified multiplier
     *
     * @param multiplier 按照指定规模缩放物品,这只会影响scale,所以物品位置可能不正确<br>Scaling the item to the specified scale multiplier number, which only affects the scale, so the item position may not be correct.
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
     * 按照指定的物品材质大小缩放为标准16x16像素物品显示
     * <p>
     * Scale to a standard 16x16 pixel item display at the specified item texture size
     *
     * @param textureSize 指定物品材质的像素大小,只能为16的倍数<br>Specify the pixel size of the item texture, which can only be a multiple of 16.
     * @return the render offsets
     */
    public static RenderOffsets scaleOffset(int textureSize) {
        double multiplier = textureSize / 16f;
        return scaleOffset(multiplier);
    }

    private CompoundTag xyzToCompoundTag(Vector3f pos, Vector3f rot, Vector3f sc) {
        var result = new CompoundTag();
        if (pos != null) {
            var position = new ListTag<FloatTag>("position");
            position.add(new FloatTag("", pos.x));
            position.add(new FloatTag("", pos.y));
            position.add(new FloatTag("", pos.z));
            result.putList(position);
        }
        if (rot != null) {
            var rotation = new ListTag<FloatTag>("rotation");
            rotation.add(new FloatTag("", rot.x));
            rotation.add(new FloatTag("", rot.y));
            rotation.add(new FloatTag("", rot.z));
            result.putList(rotation);
        }
        if (sc != null) {
            var scale = new ListTag<FloatTag>("scale");
            scale.add(new FloatTag("", sc.x));
            scale.add(new FloatTag("", sc.y));
            scale.add(new FloatTag("", sc.z));
            result.putList(scale);
        }
        return result;
    }
}
