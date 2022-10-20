package cn.nukkit.item.customitem.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import lombok.Builder;

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
     * @param mainHandFirstPerson 设置第一人称主手物品的偏移<br>Set the offset of the first person main hand item
     * @param mainHandThirdPerson 设置第三人称主手物品的偏移<br>Set the offset of the third person main hand item
     * @param offHandFirstPerson  设置第一人称副手物品的偏移<br>Set the offset of the first person offhand item
     * @param offHandThirdPerson  设置第三人称副手物品的偏移<br>Set the offset of the third person offhand item
     */
    public RenderOffsets(@Nullable Offset.OffsetBuilder mainHandFirstPerson, @Nullable Offset.OffsetBuilder mainHandThirdPerson, @Nullable Offset.OffsetBuilder offHandFirstPerson, @Nullable Offset.OffsetBuilder offHandThirdPerson) {
        if (mainHandFirstPerson != null || mainHandThirdPerson != null) {
            this.nbt.putCompound("main_hand", new CompoundTag());
            if (mainHandFirstPerson != null) {
                this.nbt.getCompound("main_hand").putCompound("first_person", xyzToCompoundTag(mainHandFirstPerson.position, mainHandFirstPerson.rotation, mainHandFirstPerson.scale));
            } else {
                this.nbt.getCompound("main_hand").putCompound("third_person", xyzToCompoundTag(mainHandThirdPerson.position, mainHandThirdPerson.rotation, mainHandThirdPerson.scale));
            }
        }
        if (offHandFirstPerson != null || offHandThirdPerson != null) {
            this.nbt.putCompound("off_hand", new CompoundTag());
            if (offHandFirstPerson != null) {
                this.nbt.getCompound("off_hand").putCompound("first_person", xyzToCompoundTag(offHandFirstPerson.position, offHandFirstPerson.rotation, offHandFirstPerson.scale));
            } else {
                this.nbt.getCompound("off_hand").putCompound("third_person", xyzToCompoundTag(offHandThirdPerson.position, offHandThirdPerson.rotation, offHandThirdPerson.scale));
            }
        } else if (mainHandFirstPerson == null && mainHandThirdPerson == null)
            throw new IllegalArgumentException("Do not allow all parameters to be empty, if you do not want to specify, please do not use the renderOffsets method");
    }

    /**
     * 以指定参数调整第一人称主手的scale偏移量
     * <p>
     * Adjusts the scale offset of the first-person main hand with the specified multiplier
     *
     * @param multiplier 范围0-1->?  越接近0物品越靠近玩家,越远离1物品越远离玩家<br>(Range 0-1->?)The closer the multiplier is to 0,the closer the item is to the player.The further the multiplier is from 1,the farther the item is from the player
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
                Offset.builder().scale(new Vector3f(scale3, scale3, scale3)),
                Offset.builder().scale(new Vector3f(scale1, scale2, scale1)),
                Offset.builder().scale(new Vector3f(scale1, scale2, scale1)),
                Offset.builder().scale(new Vector3f(scale1, scale2, scale1))
        );
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

    @Builder
    public static class Offset {
        public Vector3f position;
        public Vector3f rotation;
        public Vector3f scale;
    }
}
