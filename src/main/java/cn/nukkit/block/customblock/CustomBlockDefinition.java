package cn.nukkit.block.customblock;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.customblock.data.BlockCreativeCategory;
import cn.nukkit.block.customblock.data.BoneCondition;
import cn.nukkit.block.customblock.data.Materials;
import cn.nukkit.block.customblock.data.Permutations;
import cn.nukkit.blockproperty.*;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.*;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * CustomBlockDefinition用于获得发送给客户端的方块行为包数据。{@link CustomBlockDefinition.Builder}中提供的方法都是控制发送给客户端数据，如果需要控制服务端部分行为，请覆写{@link cn.nukkit.block.Block Block}中的方法。
 * <p>
 * CustomBlockDefinition is used to get the data of the block behavior_pack sent to the client. The methods provided in {@link CustomBlockDefinition.Builder} control the data sent to the client, if you need to control some of the server-side behavior, please override the methods in {@link cn.nukkit.block.Block Block}.
 */
@PowerNukkitXOnly
@Since("1.19.31-r1")
public record CustomBlockDefinition(String identifier, CompoundTag nbt) {


    public static CustomBlockDefinition.Builder builder(CustomBlock customBlock, String texture) {
        return builder(customBlock, Materials.builder().any(Materials.RenderMethod.OPAQUE, texture), BlockCreativeCategory.CONSTRUCTION);
    }

    public static CustomBlockDefinition.Builder builder(CustomBlock customBlock, String texture, BlockCreativeCategory blockCreativeCategory) {
        return builder(customBlock, Materials.builder().any(Materials.RenderMethod.OPAQUE, texture), blockCreativeCategory);
    }

    public static CustomBlockDefinition.Builder builder(CustomBlock customBlock, Materials materials) {
        return builder(customBlock, materials, BlockCreativeCategory.CONSTRUCTION);
    }

    /**
     * Builder custom block definition.
     *
     * @param customBlock           the custom block
     * @param materials             the materials
     * @param blockCreativeCategory 自定义方块在创造栏的大分类<br>the block creative category
     * @return the custom block definition builder.
     */
    public static CustomBlockDefinition.Builder builder(@NonNull CustomBlock customBlock, @NonNull Materials materials, BlockCreativeCategory blockCreativeCategory) {
        return new CustomBlockDefinition.Builder(customBlock, materials, blockCreativeCategory);
    }

    public static class Builder {
        protected final String identifier;
        protected final CustomBlock customBlock;

        protected CompoundTag nbt = new CompoundTag()
                .putCompound("components", new CompoundTag());

        protected Builder(CustomBlock customBlock, Materials materials, BlockCreativeCategory blockCreativeCategory) {
            this.identifier = customBlock.getNamespaceId();
            this.customBlock = customBlock;

            var components = this.nbt.getCompound("components");

            //设置一些与PNX内部对应的方块属性
            components.putCompound("minecraft:friction", new CompoundTag()
                            .putFloat("value", (float) Math.abs(1 - customBlock.getFrictionFactor()))) // in vanilla, the closer factor to 0, the more slippery the block is. But in PNX it's reversed.
                    .putCompound("minecraft:explosion_resistance", new CompoundTag()
                            .putInt("value", (int) customBlock.getResistance()))
                    .putCompound("minecraft:light_dampening", new CompoundTag()
                            .putByte("lightLevel", (byte) customBlock.getLightFilter()))
                    .putCompound("minecraft:light_emission", new CompoundTag()
                            .putByte("emission", (byte) customBlock.getLightLevel()))
                    .putCompound("minecraft:destructible_by_mining", new CompoundTag()
                            .putFloat("value", (float) (customBlock.calculateBreakTime() * 2 / 3)));
            //设置材质
            components.putCompound("minecraft:material_instances", new CompoundTag()
                    .putCompound("mappings", new CompoundTag())
                    .putCompound("materials", materials.build()));
            //默认单位立方体方块
            components.putCompound("minecraft:unit_cube", new CompoundTag());
            //设置方块在创造栏的分类
            this.nbt.putCompound("menu_category", new CompoundTag()
                    .putString("category", blockCreativeCategory.name().toLowerCase(Locale.ENGLISH)));
            //molang版本
            this.nbt.putInt("molangVersion", 6);

            //设置方块的properties
            var propertiesNBT = getPropertiesNBT();
            if (propertiesNBT != null) {
                propertiesNBT.setName("properties");
                nbt.putList(propertiesNBT);
            }
        }

        /**
         * 控制自定义方块在创造栏中的组。
         * <p>
         * Control the grouping of custom blocks in the creation inventory.
         *
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
         */
        public Builder creativeGroup(String creativeGroup) {
            if (creativeGroup.isBlank()) {
                System.out.println("creativeGroup has an invalid value!");
                return this;
            }
            this.nbt.getCompound("components").getCompound("menu_category").putString("group", creativeGroup.toLowerCase(Locale.ENGLISH));
            return this;
        }

        /**
         * 以度为单位设置块围绕立方体中心的旋转,旋转顺序为 xyz.角度必须是90的倍数。
         * <p>
         * Set the rotation of the block around the center of the block in degrees, the rotation order is xyz. The angle must be a multiple of 90.
         */
        public Builder rotation(@NonNull Vector3f rotation) {
            this.nbt.putCompound("minecraft:rotation", new CompoundTag()
                    .putFloat("x", rotation.x)
                    .putFloat("y", rotation.y)
                    .putFloat("z", rotation.z));
            return this;
        }

        /**
         * 控制自定义方块的几何模型,如果不设置默认为单位立方体
         * <p>
         * Control the geometric model of the custom block, if not set the default is the unit cube.<br>
         * Geometry identifier from geo file in 'RP/models/blocks' folder
         */
        public Builder geometry(String geometry) {
            if (geometry.isBlank()) {
                System.out.println("geometry has an invalid value!");
                return this;
            }
            var components = this.nbt.getCompound("components");
            //默认单位立方体方块，如果定义几何模型需要移除
            if (components.contains("minecraft:unit_cube")) components.remove("minecraft:unit_cube");
            //设置方块对应的几何模型
            components.putCompound("minecraft:geometry", new CompoundTag()
                    .putString("value", geometry.toLowerCase(Locale.ENGLISH)));
            return this;
        }

        /**
         * 控制自定义方块的变化特征，例如条件渲染，部分渲染等
         * <p>
         * Control custom block permutation features such as conditional rendering, partial rendering, etc.
         */
        public Builder permutations(@NonNull Permutations permutations) {
            var per = permutations.data();
            per.setName("permutations");
            this.nbt.putList(per);
            return this;
        }

        /**
         * 条件渲染自定义方块的部分模型内容。
         * <p>
         * Partially render the content of the custom block with conditional rendering.
         */
        public Builder partVisibility(@NotNull BoneCondition... boneConditions) {
            var components = this.nbt.getCompound("components");
            var boneConditionsNBT = new CompoundTag("boneConditions");
            for (var boneCondition : boneConditions) {
                var tag = new CompoundTag(boneCondition.getConditionName());
                tag.putString("bone_condition", boneCondition.getConditionExpr());
                tag.putString("bone_name", boneCondition.getBoneName());
                tag.putInt("molang_version", boneCondition.getMolangVersion());
                boneConditionsNBT.putCompound(tag.getName(), tag);
            }
            components.put(boneConditionsNBT.getName(), boneConditionsNBT);
            return this;
        }

        /**
         * 设置此方块的客户端碰撞箱。
         * <p>
         * Set the client collision box for this block.
         *
         * @param origin 碰撞箱的原点 The origin of the collision box
         * @param size   碰撞箱的大小 The size of the collision box
         */
        public Builder collisionBox(@NotNull Vector3f origin, @NotNull Vector3f size) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:collision_box", new CompoundTag()
                            .putBoolean("enabled", true)
                            .putList(new ListTag<FloatTag>("origin")
                                    .add(new FloatTag("", -3.25f))
                                    .add(new FloatTag("", 4.75f))
                                    .add(new FloatTag("", -3.25f)))
                            .putList(new ListTag<FloatTag>("size")
                                    .add(new FloatTag("", 6.5f))
                                    .add(new FloatTag("", 6.5f))
                                    .add(new FloatTag("", 6.5f))));
            return this;
        }

        /**
         * 设置此方块的客户端选择箱。
         * <p>
         * Set the client collision box for this block.
         *
         * @param origin 选择箱的原点 The origin of the collision box
         * @param size   选择箱的大小 The size of the collision box
         */
        public Builder selectionBox(@NotNull Vector3f origin, @NotNull Vector3f size) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:selection_box", new CompoundTag()
                            .putBoolean("enabled", true)
                            .putList(new ListTag<FloatTag>("origin")
                                    .add(new FloatTag("", -3.25f))
                                    .add(new FloatTag("", 4.75f))
                                    .add(new FloatTag("", -3.25f)))
                            .putList(new ListTag<FloatTag>("size")
                                    .add(new FloatTag("", 6.5f))
                                    .add(new FloatTag("", 6.5f))
                                    .add(new FloatTag("", 6.5f))));
            return this;
        }

       /*public Builder blockTags(String creativeGroup) {
            if (creativeGroup.isBlank()) {
                System.out.println("creativeGroup has an invalid value!");
                return this;
            }
            this.nbt.getCompound("components").getCompound("menu_category").putString("group", creativeGroup.toLowerCase(Locale.ENGLISH));
            return this;
        }*/

        /**
         * PNX无法准确提供三个以上属性方块的属性解析,如果出现地图异常,请制作一个该方块相同属性的行为包,利用proxypass和bds抓包获取准确的属性解析。
         * <p>
         * PNX can not provide accurate properties analysis for more than three properties block, if there is a level exception to block error, please make a behavior_pack of the same properties of the block, and use proxypass and bds to get the packet to accurate get properties.
         *
         * @return Block Properties in NBT Tag format
         */
        @Nullable
        private ListTag<CompoundTag> getPropertiesNBT() {
            if (this.customBlock instanceof Block block) {
                var properties = block.getProperties();
                if (properties == CommonBlockProperties.EMPTY_PROPERTIES || properties.getAllProperties().size() == 0) {
                    return null;
                }
                var nbtList = new ListTag<CompoundTag>("properties");
                var tmpList = new ArrayList<>(properties.getAllProperties());
                if (this.customBlock.reverseSending()) {
                    Collections.reverse(tmpList);
                }
                for (var each : tmpList) {
                    if (each.getProperty() instanceof BooleanBlockProperty booleanBlockProperty) {
                        nbtList.add(new CompoundTag().putString("name", booleanBlockProperty.getName())
                                .putList(new ListTag<>("enum")
                                        .add(new IntTag("", 0))
                                        .add(new IntTag("", 1))));
                    } else if (each.getProperty() instanceof IntBlockProperty intBlockProperty) {
                        var enumList = new ListTag<IntTag>("enum");
                        for (int i = intBlockProperty.getMinValue(); i <= intBlockProperty.getMaxValue(); i++) {
                            enumList.add(new IntTag("", i));
                        }
                        nbtList.add(new CompoundTag().putString("name", intBlockProperty.getName()).putList(enumList));
                    } else if (each.getProperty() instanceof UnsignedIntBlockProperty unsignedIntBlockProperty) {
                        var enumList = new ListTag<LongTag>("enum");
                        for (long i = unsignedIntBlockProperty.getMinValue(); i <= unsignedIntBlockProperty.getMaxValue(); i++) {
                            enumList.add(new LongTag("", i));
                        }
                        nbtList.add(new CompoundTag().putString("name", unsignedIntBlockProperty.getName()).putList(enumList));
                    } else if (each.getProperty() instanceof ArrayBlockProperty<?> arrayBlockProperty) {
                        if (arrayBlockProperty.isOrdinal()) {
                            var enumList = new ListTag<IntTag>("enum");
                            var universe = arrayBlockProperty.getUniverse();
                            for (int i = 0, universeLength = universe.length; i < universeLength; i++) {
                                enumList.add(new IntTag("", i));
                            }
                            nbtList.add(new CompoundTag().putString("name", arrayBlockProperty.getName()).putList(enumList));
                        } else {
                            var enumList = new ListTag<StringTag>("enum");
                            for (var e : arrayBlockProperty.getUniverse()) {
                                enumList.add(new StringTag("", e.toString()));
                            }
                            nbtList.add(new CompoundTag().putString("name", arrayBlockProperty.getName()).putList(enumList));
                        }
                    }
                }
                return nbtList;
            }
            return null;
        }

        /**
         * 对要发送给客户端的方块ComponentNBT进行自定义处理，这里包含了所有对自定义方块的定义。在符合条件的情况下，你可以任意修改。
         * <p>
         * Custom processing of the block to be sent to the client ComponentNBT, which contains all definitions for custom block. You can modify them as much as you want, under the right conditions.
         */
        public CustomBlockDefinition customBuild(@NonNull Consumer<CompoundTag> nbt) {
            var def = this.build();
            nbt.accept(def.nbt);
            return def;
        }

        public CustomBlockDefinition build() {
            return new CustomBlockDefinition(this.identifier, this.nbt);
        }
    }
}
