package cn.nukkit.block.customblock;

import cn.nukkit.block.Block;
import cn.nukkit.block.customblock.data.CraftingTable;
import cn.nukkit.block.customblock.data.Geometry;
import cn.nukkit.block.customblock.data.Materials;
import cn.nukkit.block.customblock.data.Permutation;
import cn.nukkit.block.customblock.data.Transformation;
import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.block.property.type.BooleanPropertyType;
import cn.nukkit.block.property.type.EnumPropertyType;
import cn.nukkit.block.property.type.IntPropertyType;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.item.customitem.data.CreativeGroup;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * CustomBlockDefinition用于获得发送给客户端的方块行为包数据。{@link CustomBlockDefinition.Builder}中提供的方法都是控制发送给客户端数据，如果需要控制服务端部分行为，请覆写{@link Block Block}中的方法。
 * <p>
 * CustomBlockDefinition is used to get the data of the block behavior_pack sent to the client. The methods provided in {@link CustomBlockDefinition.Builder} control the data sent to the client, if you need to control some of the server-side behavior, please override the methods in {@link Block Block}.
 */
@Slf4j
public record CustomBlockDefinition(String identifier, CompoundTag nbt) {
    private static final Object2IntOpenHashMap<String> INTERNAL_ALLOCATION_ID_MAP = new Object2IntOpenHashMap<>();
    private static final AtomicInteger CUSTOM_BLOCK_RUNTIMEID = new AtomicInteger(10000);

    public int getRuntimeId() {
        return CustomBlockDefinition.INTERNAL_ALLOCATION_ID_MAP.getInt(identifier);
    }

    public static int getRuntimeId(String identifier) {
        return CustomBlockDefinition.INTERNAL_ALLOCATION_ID_MAP.getInt(identifier);
    }

    /**
     * Builder custom block definition.
     *
     * @param customBlock the custom block
     * @return the custom block definition builder.
     */
    public static CustomBlockDefinition.Builder builder(@NotNull CustomBlock customBlock) {
        return new CustomBlockDefinition.Builder(customBlock);
    }

    public static class Builder {
        protected final String identifier;
        protected final CustomBlock customBlock;

        protected CompoundTag nbt = new CompoundTag()
                .putCompound("components", new CompoundTag());

        protected Builder(CustomBlock customBlock) {
            this.identifier = customBlock.getId();
            this.customBlock = customBlock;
            var b = (Block) customBlock;
            var components = this.nbt.getCompound("components");

            // Set some block properties corresponding to PNX internal
            components.putCompound("minecraft:friction", new CompoundTag()
                            .putFloat("value", (float) Math.min(0.9, Math.max(0, 1 - b.getFrictionFactor()))))
                    .putCompound("minecraft:destructible_by_explosion", new CompoundTag()
                            .putInt("explosion_resistance", (int) customBlock.getResistance()))
                    .putCompound("minecraft:light_dampening", new CompoundTag()
                            .putByte("lightLevel", (byte) customBlock.getLightFilter()))
                    .putCompound("minecraft:light_emission", new CompoundTag()
                            .putByte("emission", (byte) customBlock.getLightLevel()))
                    .putCompound("minecraft:destructible_by_mining", new CompoundTag()
                            .putFloat("value", 99999f));//default server-side mining time calculate
            // Setting up the material
            CompoundTag defaultMaterial = createDefaultMaterialInstance(null);
            components.putCompound("minecraft:material_instances", defaultMaterial);

            // Sets the default geometry
            components.putCompound("minecraft:geometry", createDefaultGeometry(null));

            // Set the category of the block in the creation column
            this.nbt.putCompound("menu_category", createDefaultMenuCategory());
            // Molang version
            this.nbt.putInt("molangVersion", 9);

            //设置方块的properties
            var propertiesNBT = getPropertiesNBT();
            if (propertiesNBT != null) {
                nbt.putList("properties", propertiesNBT);
            }

            int block_id;
            if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(identifier)) {
                while (INTERNAL_ALLOCATION_ID_MAP.containsValue(block_id = CUSTOM_BLOCK_RUNTIMEID.getAndIncrement())) {
                }
                INTERNAL_ALLOCATION_ID_MAP.put(identifier, block_id);
            } else {
                block_id = INTERNAL_ALLOCATION_ID_MAP.getInt(identifier);
            }
            nbt.putCompound("vanilla_block_data", new CompoundTag().putInt("block_id", block_id)
                    /*.putString("material", "")*/); //todo Figure what is dirt, maybe that corresponds to https://wiki.bedrock.dev/documentation/materials.html
        }

        public Builder name(String name) {
            Preconditions.checkArgument(!name.isBlank(), "name is blank");
            this.nbt.getCompound("components").putCompound("minecraft:display_name", new CompoundTag()
                    .putString("value", name));
            return this;
        }

        public Builder texture(String texture) {
            CompoundTag material = createDefaultMaterialInstance(texture);
            this.nbt.getCompound("components").putCompound("minecraft:material_instances", material);
            return this;
        }

        public Builder materials(Materials materials) {
            CompoundTag base = createDefaultMaterialInstance(null);
            CompoundTag baseMaterials = base.getCompound("materials");
            CompoundTag customMaterials = materials.toCompoundTag();

            for (Map.Entry<String, Tag> customEntry : customMaterials.getTags().entrySet()) {
                String key = customEntry.getKey();
                CompoundTag customMat = (CompoundTag) customEntry.getValue();
                CompoundTag baseMat = baseMaterials.contains(key)
                        ? baseMaterials.getCompound(key)
                        : new CompoundTag();

                for (Map.Entry<String, Tag> entry : customMat.getTags().entrySet()) {
                    baseMat.put(entry.getKey(), entry.getValue());
                }

                baseMaterials.putCompound(key, baseMat);
            }

            this.nbt.getCompound("components").putCompound("minecraft:material_instances", base);
            return this;
        }

        public Builder creativeCategory(String creativeCategory) {
            if (!this.nbt.contains("menu_category")) {
                this.nbt.putCompound("menu_category", createDefaultMenuCategory());
            }
            this.nbt.getCompound("menu_category")
                    .putString("category", creativeCategory.toLowerCase(Locale.ENGLISH));
            return this;
        }

        public Builder creativeCategory(CreativeCategory creativeCategory) {
            if (!this.nbt.contains("menu_category")) {
                this.nbt.putCompound("menu_category", createDefaultMenuCategory());
            }
            this.nbt.getCompound("menu_category")
                    .putString("category", creativeCategory.name().toLowerCase(Locale.ENGLISH));
            return this;
        }

        /**
         * Control the grouping of custom blocks in the creation inventory.
         *
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
         */
        public Builder creativeGroup(String creativeGroup) {
            if (creativeGroup.isBlank()) {
                log.error("creativeGroup has an invalid value!");
                return this;
            }
            if (!this.nbt.contains("menu_category")) {
                this.nbt.putCompound("menu_category", createDefaultMenuCategory());
            }
            this.nbt.getCompound("menu_category").putString("group", creativeGroup);
            return this;
        }

        /**
         * Control the grouping of custom blocks in the creation inventory.
         *
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
         */
        public Builder creativeGroup(CreativeGroup creativeGroup) {
            if (!this.nbt.contains("menu_category")) {
                this.nbt.putCompound("menu_category", createDefaultMenuCategory());
            }
            this.nbt.getCompound("menu_category").putString("group", creativeGroup.getGroupName());
            return this;
        }

        /**
         * Sets whether the item/block should be hidden from commands like /give and /replaceitem.
         * 
         * @param hidden true to hide, false to show (default: false)
         * @return this builder
         */
        public Builder isHiddenInCommands(boolean hidden) {
            if (!this.nbt.contains("menu_category")) {
                this.nbt.putCompound("menu_category", createDefaultMenuCategory());
            }
            this.nbt.getCompound("menu_category").putByte("is_hidden_in_commands", (byte) (hidden ? 1 : 0));
            return this;
        }

        /**
         * 控制自定义方块客户端侧的挖掘时间(单位秒)
         * <p>
         * 自定义方块的挖掘时间取决于服务端侧和客户端侧中最小的一个
         * <p>
         * Control the digging time (in seconds) on the client side of the custom block
         * <p>
         * The digging time of a custom cube depends on the smallest of the server-side and client-side
         */
        public Builder breakTime(double second) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:destructible_by_mining", new CompoundTag()
                            .putFloat("value", (float) second));
            return this;
        }

        /**
         * @see <a href="https://wiki.bedrock.dev/blocks/block-components.html#crafting-table">wiki.bedrock.dev</a>
         */
        public Builder craftingTable(CraftingTable craftingTable) {
            this.nbt.getCompound("components").putCompound("minecraft:crafting_table", craftingTable.toCompoundTag());
            return this;
        }

        /**
         * supports rotation, scaling, and translation. The component can be added to the whole block and/or to individual block permutations. Transformed geometries still have the same restrictions that non-transformed geometries have such as a maximum size of 30/16 units.
         */
        public Builder transformation(@NotNull Transformation transformation) {
            this.nbt.getCompound("components").putCompound("minecraft:transformation", transformation.toCompoundTag());
            return this;
        }

        /**
         * 以度为单位设置块围绕立方体中心的旋转,旋转顺序为 xyz.角度必须是90的倍数。
         * <p>
         * Set the rotation of the block around the center of the block in degrees, the rotation order is xyz. The angle must be a multiple of 90.
         */
        public Builder rotation(@NotNull Vector3f rotation) {
            this.transformation(new Transformation(new Vector3(0, 0, 0), new Vector3(1, 1, 1), rotation.asVector3()));
            return this;
        }

        /**
         * @see #geometry(Geometry)
         * 默认不设置骨骼显示
         * <p>
         * defalut not set boneVisibilities
         */
        public Builder geometry(String geometry) {
            if (geometry.isBlank()) {
                log.error("geometry has an invalid value!");
                return this;
            }
            var components = this.nbt.getCompound("components");
            //设置方块对应的几何模型（合并默认结构）
            CompoundTag mergedGeometry = createDefaultGeometry(geometry.toLowerCase(Locale.ENGLISH));
            components.putCompound("minecraft:geometry", mergedGeometry);
            return this;
        }

        /**
         * 控制自定义方块的几何模型,如果不设置默认为单位立方体
         * <p>
         * Control the geometric model of the custom block, if not set the default is the full_block.<br>
         * Geometry identifier from geo file in 'RP/models/blocks' folder
         */
        public Builder geometry(@NotNull Geometry geometry) {
            var components = this.nbt.getCompound("components");
            //设置方块对应的几何模型（合并默认结构）
            CompoundTag base = createDefaultGeometry(null);
            CompoundTag custom = geometry.toCompoundTag();
            for (Map.Entry<String, cn.nukkit.nbt.tag.Tag> entry : custom.getTags().entrySet()) {
                base.put(entry.getKey(), entry.getValue());
            }
            components.putCompound("minecraft:geometry", base);
            return this;
        }

        /**
         * 控制自定义方块的变化特征，例如条件渲染，部分渲染等
         * <p>
         * Control custom block permutation features such as conditional rendering, partial rendering, etc.
         */

        public Builder permutation(Permutation permutation) {
            if (!this.nbt.contains("permutations")) {
                this.nbt.putList("permutations", new ListTag<CompoundTag>());
            }
            ListTag<CompoundTag> permutations = this.nbt.getList("permutations", CompoundTag.class);
            permutations.add(permutation.toCompoundTag());
            this.nbt.putList("permutations", permutations);
            return this;
        }

        /**
         * 控制自定义方块的变化特征，例如条件渲染，部分渲染等
         * <p>
         * Control custom block permutation features such as conditional rendering, partial rendering, etc.
         */
        public Builder permutations(Permutation... permutations) {
            var per = new ListTag<CompoundTag>();
            for (var permutation : permutations) {
                per.add(permutation.toCompoundTag());
            }
            this.nbt.putList("permutations", per);
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
                            .putList("origin", new ListTag<FloatTag>()
                                    .add(new FloatTag(origin.x))
                                    .add(new FloatTag(origin.y))
                                    .add(new FloatTag(origin.z)))
                            .putList("size", new ListTag<FloatTag>()
                                    .add(new FloatTag(size.x))
                                    .add(new FloatTag(size.y))
                                    .add(new FloatTag(size.z))));
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
                            .putList("origin", new ListTag<FloatTag>()
                                    .add(new FloatTag(origin.x))
                                    .add(new FloatTag(origin.y))
                                    .add(new FloatTag(origin.z)))
                            .putList("size", new ListTag<FloatTag>()
                                    .add(new FloatTag(size.x))
                                    .add(new FloatTag(size.y))
                                    .add(new FloatTag(size.z))));
            return this;
        }

        public Builder blockTags(String... tag) {
            Preconditions.checkNotNull(tag);
            Preconditions.checkArgument(tag.length > 0);
            ListTag<StringTag> stringTagListTag = new ListTag<>();
            for (String s : tag) {
                stringTagListTag.add(new StringTag(s));
            }
            this.nbt.putList("blockTags", stringTagListTag);
            return this;
        }

        /**
         * @return Block Properties in NBT Tag format
         */
        @Nullable
        private ListTag<CompoundTag> getPropertiesNBT() {
            if (this.customBlock instanceof Block block) {
                var properties = block.getProperties();
                Set<BlockPropertyType<?>> propertyTypeSet = properties.getPropertyTypeSet();
                if (propertyTypeSet.isEmpty()) {
                    return null;
                }
                var nbtList = new ListTag<CompoundTag>();
                for (var each : propertyTypeSet) {
                    if (each instanceof BooleanPropertyType booleanBlockProperty) {
                        nbtList.add(new CompoundTag().putString("name", booleanBlockProperty.getName())
                                .putList("enum", new ListTag<>()
                                        .add(new ByteTag(0))
                                        .add(new ByteTag(1))));
                    } else if (each instanceof IntPropertyType intBlockProperty) {
                        var enumList = new ListTag<IntTag>();
                        for (int i = intBlockProperty.getMin(); i <= intBlockProperty.getMax(); i++) {
                            enumList.add(new IntTag(i));
                        }
                        nbtList.add(new CompoundTag().putString("name", intBlockProperty.getName()).putList("enum", enumList));
                    } else if (each instanceof EnumPropertyType<?> arrayBlockProperty) {
                        var enumList = new ListTag<StringTag>();
                        for (var e : arrayBlockProperty.getValidValues()) {
                            enumList.add(new StringTag(e.name().toLowerCase(Locale.ENGLISH)));
                        }
                        nbtList.add(new CompoundTag().putString("name", arrayBlockProperty.getName()).putList("enum", enumList));
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
        public CustomBlockDefinition customBuild(@NotNull Consumer<CompoundTag> nbt) {
            var def = this.build();
            nbt.accept(def.nbt);
            return def;
        }

        public CustomBlockDefinition build() {
            return new CustomBlockDefinition(this.identifier, this.nbt);
        }
    }

    // Creates default geometry
    public static CompoundTag createDefaultGeometry(String identifierOverride) {
        CompoundTag geometry = new CompoundTag(new LinkedHashMap<>());
        geometry.putCompound("bone_visibility", new CompoundTag());
        geometry.putString("culling", "");
        geometry.putString("culling_layer", "minecraft:culling_layer.undefined");
        geometry.putString("identifier", identifierOverride != null ? identifierOverride : "minecraft:geometry.full_block");
        geometry.putByte("ignoreGeometryForIsSolid", (byte) 1);
        geometry.putByte("needsLegacyTopRotation", (byte) 0);
        geometry.putByte("useLegacyBlockLightAbsorption", (byte) 0);
        geometry.putByte("uv_lock", (byte) 0);
        return geometry;
    }

    // Creates default materials instance
    public static CompoundTag createDefaultMaterialInstance(String textureOverride) {
        CompoundTag materials = new CompoundTag(new LinkedHashMap<>());
        CompoundTag main = new CompoundTag(new LinkedHashMap<>());
        main.putFloat("ambient_occlusion", 1.0f);
        main.putByte("face_dimming", (byte) 1);
        main.putByte("isotropic", (byte) 0);
        main.putString("render_method", "opaque");
        main.putString("texture", textureOverride != null ? textureOverride : "missing_texture");
        main.putString("tint_method", "none");

        materials.putCompound("*", main);

        CompoundTag materialInstances = new CompoundTag(new LinkedHashMap<>());
        materialInstances.putCompound("mappings", new CompoundTag());
        materialInstances.putCompound("materials", materials);

        return materialInstances;
    }

    // Creates default category
    public static CompoundTag createDefaultMenuCategory() {
        return new CompoundTag(new LinkedHashMap<>())
            .putString("category", "construction")
            .putString("group", "")
            .putByte("is_hidden_in_commands", (byte) 0);
    }
}
