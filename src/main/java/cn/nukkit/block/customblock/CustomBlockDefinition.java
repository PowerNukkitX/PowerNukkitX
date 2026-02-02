package cn.nukkit.block.customblock;

import cn.nukkit.block.Block;
import cn.nukkit.block.customblock.data.CraftingTable;
import cn.nukkit.block.customblock.data.Geometry;
import cn.nukkit.block.customblock.data.Materials;
import cn.nukkit.block.customblock.data.Permutation;
import cn.nukkit.block.customblock.data.Transformation;
import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.block.property.type.BooleanPropertyType;
import cn.nukkit.block.property.type.EnumPropertyType;
import cn.nukkit.block.property.type.IntPropertyType;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.item.customitem.data.CreativeGroup;
import cn.nukkit.math.AxisAlignedBB;
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
import lombok.Getter;
import lombok.experimental.Accessors;
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
 * CustomBlockDefinition is used to define and retrieve block data for both the server and the client.
 * <p>
 * The methods provided in {@link CustomBlockDefinition.Builder} configure how the block behaves and appears in the client
 * (via behavior pack data), and are now also used to register the block's behavior on the server side.
 * <p>
 * For further customization of runtime behavior, you can still override methods in {@link Block Block}.
 */
@Slf4j
@Getter
@Accessors(fluent = true)
public class CustomBlockDefinition extends BlockDefinition {
    private static final Object2IntOpenHashMap<String> INTERNAL_ALLOCATION_ID_MAP = new Object2IntOpenHashMap<>();
    private static final AtomicInteger CUSTOM_BLOCK_RUNTIMEID = new AtomicInteger(10000);

    protected String identifier;
    protected CompoundTag nbt;
    protected BlockTickSettings tickSettings;

    public CustomBlockDefinition(Builder b) {
        super(b);

        this.identifier = b.identifier;
        this.nbt = b.nbt;
        this.tickSettings = b.tickSettings;
    }

    public int getRuntimeId() {
        return getRuntimeId(identifier);
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

    public static class Builder extends BlockDefinitionBuilder<BlockDefinition, Builder> {
        protected final String identifier;
        protected final CustomBlock customBlock;
        private BlockTickSettings tickSettings = null;

        protected CompoundTag nbt = new CompoundTag()
                .putCompound("components", new CompoundTag());

        protected Builder(CustomBlock customBlock) {
            this.identifier = customBlock.getId();
            this.customBlock = customBlock;
            var components = this.nbt.getCompound("components");

            // Set default components using static default values
            CompoundTag defaults = createDefaultComponents(
                    0.4f,
                    0.0f,
                    0.0f,
                    15,
                    0,
                    "#ffffff"
            );
            for (Map.Entry<String, Tag> entry : defaults.getTags().entrySet()) {
                components.put(entry.getKey(), entry.getValue());
            }

            // Setting up  default material instances
            getOrCreateMaterialInstances(components);
            //CompoundTag defaultMaterial = getOrCreateMaterialInstances(components); << TO REMOVE???
            //components.putCompound("minecraft:material_instances", defaultMaterial); << TO REMOVE???

            // Sets the default geometry
            components.putCompound("minecraft:geometry", createDefaultGeometry(null));

            // Set the category of the block in the creation column
            this.nbt.putCompound("menu_category", createDefaultMenuCategory());
            // Molang version
            this.nbt.putInt("molangVersion", 9);

            // Set the properties of the block
            var propertiesNBT = getPropertiesNBT();
            if (propertiesNBT != null) {
                nbt.putList("properties", propertiesNBT);
            }

            int block_id = INTERNAL_ALLOCATION_ID_MAP.getOrDefault(identifier, -1);
            if (block_id == -1) {
                int newId;
                do {
                    newId = CUSTOM_BLOCK_RUNTIMEID.getAndIncrement();
                } while (INTERNAL_ALLOCATION_ID_MAP.containsValue(newId));

                block_id = INTERNAL_ALLOCATION_ID_MAP.putIfAbsent(identifier, newId);
                if (block_id == -1) {
                    block_id = newId;
                }
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

        /**
         * Sets the friction value of the block. Default is 0.6f.
         */
        @Override
        public Builder friction(double value) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:friction", new CompoundTag().putDouble("value", value));
            return super.friction(value);
        }

        /**
         * Sets the explosion resistance of the block. Default is 0. Accepted values are: false or any int value 0 or greater <p>
         * False means block can not be exploded, or any int value will make it destructible but as bigger the value more resistance.
         */
        public Builder destructibleByExplosion(boolean value) {
            int resistance = value ? 0 : -1;
            return this.destructibleByExplosion(resistance);
        }
        /**
         * Sets the explosion resistance of the block. Default is 0. Accepted values are: false or any int value 0 or greater <p>
         * False means block can not be exploded, or any int value will make it destructible but as bigger the value more resistance.
         */
        public Builder destructibleByExplosion(int resistance) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:destructible_by_explosion",
                            new CompoundTag().putInt("explosion_resistance", resistance));
            return super.resistance(resistance);
        }

        /**
         * Sets the mining time in seconds. Default is 0.0f. You can pass false to make it unbreakable.
         */
        public Builder destructibleByMining(boolean value) {
            float time = value ? 0.0f : -1.0f;
            return this.destructibleByMining(time);
        }
        /**
         * Sets the mining time in seconds. Default is 0.0f. Use false to make the block unbreakable by mining.
         */
        public Builder destructibleByMining(float seconds) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:destructible_by_mining", new CompoundTag().putFloat("value", seconds));
            return super.hardness(seconds);
        }
        /**
         * @deprecated Use {@link #destructibleByMining(float)} or {@link #destructibleByMining(boolean)} instead.
         */
        @Deprecated
        public Builder breakTime(float seconds) {
            return destructibleByMining(seconds);
        }

        /**
         * Sets the light dampening level. Default is 15.
         */
        @Override
        public Builder lightDampening(int lightLevel) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:light_dampening", new CompoundTag().putByte("lightLevel", (byte) lightLevel));
            return super.lightDampening(lightLevel);
        }

        /**
         * Sets the light emission level. Default is 0.
         */
        @Override
        public Builder lightEmission(int emission) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:light_emission", new CompoundTag().putByte("emission", (byte) emission));
            return super.lightEmission(emission);
        }

        /**
         * Sets the map color used in maps. Default is #ffffff.
         */
        public Builder mapColor(String hexColor) {
            this.nbt.getCompound("components")
                    .putString("minecraft:map_color", hexColor);
            return this;
        }

        /**
         * Set the texture of the block, use only if you are setting simple texture, all the sides of the block will have this same texture.
         */
        public Builder texture(String texture) {
            String tex = (texture != null && !texture.isBlank()) ? texture : "missing_texture";

            CompoundTag components = this.nbt.getCompound("components");
            CompoundTag mi = getOrCreateMaterialInstances(components);
            CompoundTag mats = mi.getCompound("materials");
            if (mats == null || mats.isEmpty()) {
                mats = new CompoundTag(new LinkedHashMap<>());
                mi.putCompound("materials", mats);
            }

            CompoundTag star = mats.contains("*") ? mats.getCompound("*") : new CompoundTag();
            star.putString("texture", tex);
            mats.putCompound("*", star);

            for (Map.Entry<String, Tag> entry : mats.getTags().entrySet()) {
                String face = entry.getKey();
                if ("*".equals(face)) continue;
                Tag tag = entry.getValue();
                if (tag instanceof CompoundTag faceTag) {
                    faceTag.putString("texture", tex);
                    mats.putCompound(face, faceTag);
                }
            }

            mi.putCompound("materials", mats);
            components.putCompound("minecraft:material_instances", mi);
            return this;
        }

        /**
         * Sets material instances
         *
         * <pre>
         * builder.materials(
         *     Materials.builder()
         *         .any(
         *             Materials.RenderMethod.OPAQUE,
         *             true,
         *             new Materials.PackedBools(true, false, true), // faceDimming, randomizedUV, textureVariation
         *             "my_texture"
         *         )
         *         .up(
         *             Materials.RenderMethod.OPAQUE,
         *             true,
         *             new Materials.PackedBools(true, true, true),
         *             "blue_concrete_00",
         *             Materials.TintMethod.GRASS
         *         )
         *         // down, north, south, east and west.
         * );
         * </pre>
         *
         * @param materials materials to set to block's material instances
         * @return this builder
         */
        public Builder materials(Materials materials) {
            CompoundTag components = this.nbt.getCompound("components");
            CompoundTag mi = getOrCreateMaterialInstances(components);

            CompoundTag baseMaterials = mi.getCompound("materials");
            if (baseMaterials == null || baseMaterials.isEmpty()) {
                baseMaterials = new CompoundTag(new LinkedHashMap<>());
            }

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

            mi.putCompound("materials", baseMaterials);
            components.putCompound("minecraft:material_instances", mi);
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
         * Set the rotation of the block around the center of the block in degrees, the rotation order is xyz. The angle must be a multiple of 90.
         */
        public Builder rotation(@NotNull Vector3f rotation) {
            this.transformation(new Transformation(new Vector3(0, 0, 0), new Vector3(1, 1, 1), rotation.asVector3()));
            return this;
        }

        /**
         * Sets the geometry identifier of the block using a string. If not set the default is the full_block.
         * <p>
         * This does not set bone visibility; bones will be hidden by default.
         * Use {@link #geometry(Geometry)} for advanced control.
         */
        public Builder geometry(String geometry) {
            if (geometry.isBlank()) {
                log.error("geometry has an invalid value!");
                return this;
            }
            var components = this.nbt.getCompound("components");
            CompoundTag mergedGeometry = createDefaultGeometry(geometry.toLowerCase(Locale.ENGLISH));
            components.putCompound("minecraft:geometry", mergedGeometry);
            return this;
        }

        /**
         * Sets a full geometry definition with optional bone visibility. If not set the default is the full_block.
         * <p>
         * Use this to control visible bones or other geometry properties.
         */
        public Builder geometry(@NotNull Geometry geometry) {
            var components = this.nbt.getCompound("components");
            CompoundTag base = createDefaultGeometry(null);
            CompoundTag custom = geometry.toCompoundTag();
            for (Map.Entry<String, Tag> entry : custom.getTags().entrySet()) {
                base.put(entry.getKey(), entry.getValue());
            }
            components.putCompound("minecraft:geometry", base);
            return this;
        }

        /**
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
         * Set the block collision box. You can add multiple boxes to create complex shapes by calling this method multiple times.
         *
         * @param origin The origin of the collision box
         * @param size   The size of the collision box
         */
        public Builder collisionBox(@NotNull Vector3f origin, @NotNull Vector3f size) {
            float minX = origin.x + 8f;
            float minY = origin.y;
            float minZ = origin.z + 8f;

            float maxX = minX + size.x;
            float maxY = minY + size.y;
            float maxZ = minZ + size.z;

            CompoundTag components = this.nbt.getCompound("components");
            CompoundTag collision = components.getCompound("minecraft:collision_box");
            if (collision.isEmpty()) collision.putBoolean("enabled", true);

            ListTag<CompoundTag> boxes = collision.getList("boxes", CompoundTag.class);
            boxes.add(new CompoundTag()
                    .putFloat("minX", minX)
                    .putFloat("minY", minY)
                    .putFloat("minZ", minZ)
                    .putFloat("maxX", maxX)
                    .putFloat("maxY", maxY)
                    .putFloat("maxZ", maxZ));

            collision.putList("boxes", boxes);
            components.putCompound("minecraft:collision_box", collision);

            return this;
        }

        /**
         * Sets the block selection box.
         *
         * @param origin The origin of the collision box
         * @param size   The size of the collision box
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

        public Builder isPlayerInteractable(boolean value) {
            if (!this.nbt.getCompound("components").contains("minecraft:custom_components")) {
                this.nbt.getCompound("components")
                        .putCompound("minecraft:custom_components", createDefaultCustomComponents());
            }
            this.nbt.getCompound("components")
                    .getCompound("minecraft:custom_components")
                    .putByte("hasPlayerInteract", (byte) (value ? 1 : 0));

            return this.canBeActivated(value);
        }

        public Builder hasPlayerPlacingSensor(boolean value) {
            if (!this.nbt.getCompound("components").contains("minecraft:custom_components")) {
                this.nbt.getCompound("components")
                        .putCompound("minecraft:custom_components", createDefaultCustomComponents());
            }
            this.nbt.getCompound("components")
                    .getCompound("minecraft:custom_components")
                    .putByte("hasPlayerPlacing", (byte) (value ? 1 : 0));
            return this;
        }

        /**
         * Defines how this custom block should tick over time.
         * Example: {@code .blockTick(60, 60, true)} will schedule the block to tick every 3 seconds.
         *
         * @param minTicks The minimum number of ticks before the block updates.
         * @param maxTicks The maximum number of ticks before the block updates. Must be ≥ {@code minTicks}.
         * @param looping  If {@code true}, the block will continue ticking; if {@code false}, it will tick only once.
         * @return This builder instance for chaining.
         */
        public Builder blockTick(int minTicks, int maxTicks, boolean looping) {
            Preconditions.checkArgument(minTicks >= 0 && maxTicks >= minTicks, "Invalid tick interval range");
            this.tickSettings = new BlockTickSettings(minTicks, maxTicks, looping);
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
         * Custom processing of the block to be sent to the client ComponentNBT, which contains all definitions for custom block. You can modify them as much as you want, under the right conditions.
         */
        public CustomBlockDefinition customBuild(@NotNull Consumer<CompoundTag> nbt) {
            var def = this.build();
            nbt.accept(def.nbt);
            return def;
        }

        public CustomBlockDefinition build() {
            return new CustomBlockDefinition(this);
        }

        @Override
        protected Builder self() {
            return this;
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
    private static CompoundTag getOrCreateMaterialInstances(CompoundTag components) {
        CompoundTag material = components.getCompound("minecraft:material_instances");
        if (material != null && !material.isEmpty()) return material;

        // create once
        CompoundTag materials = new CompoundTag(new LinkedHashMap<>());
        CompoundTag star = new CompoundTag(new LinkedHashMap<>());
        star.putFloat("ambient_occlusion", 1.0f);
        star.putByte("packed_bools", (byte) 0x1);
        star.putByte("isotropic", (byte) 0);
        star.putString("render_method", "opaque");
        star.putString("texture", "missing_texture");
        star.putString("tint_method", "none");
        materials.putCompound("*", star);

        material = new CompoundTag(new LinkedHashMap<>());
        material.putCompound("mappings", new CompoundTag());
        material.putCompound("materials", materials);

        components.putCompound("minecraft:material_instances", material);
        return material;
    }

    // Creates default category
    public static CompoundTag createDefaultMenuCategory() {
        return new CompoundTag(new LinkedHashMap<>())
                .putString("category", "construction")
                .putString("group", "")
                .putByte("is_hidden_in_commands", (byte) 0);
    }

    // Create default components
    public static CompoundTag createDefaultComponents(
            float frictionFactor,
            float explosionResistance,
            float miningTime,
            int lightDampening,
            int lightEmission,
            String mapColor
    ) {
        CompoundTag components = new CompoundTag(new LinkedHashMap<>());
        components.putCompound("minecraft:friction", new CompoundTag()
                .putFloat("value", 0.4f));
        components.putCompound("minecraft:destructible_by_explosion", new CompoundTag()
                .putInt("explosion_resistance", 0));
        components.putCompound("minecraft:destructible_by_mining", new CompoundTag()
                .putFloat("value", 0.0f));
        components.putCompound("minecraft:light_dampening", new CompoundTag()
                .putByte("lightLevel", (byte) 15));
        components.putCompound("minecraft:light_emission", new CompoundTag()
                .putByte("emission", (byte) 0));
        components.putString("minecraft:map_color", "#ffffff");
        return components;
    }

    // Creates default custom_components
    public static CompoundTag createDefaultCustomComponents() {
        return new CompoundTag(new LinkedHashMap<>())
                .putByte("hasPlayerInteract", (byte) 0)
                .putByte("hasPlayerPlacing", (byte) 0)
                .putByte("isV1Component", (byte) 1);
    }

    public CompoundTag getComponents() {
        return this.nbt.getCompound("components");
    }

    public boolean isSolidForBlock(@NotNull Block block) {
        return CustomBlockUtils.isSolidForBlock(this, block);
    }

    public @Nullable AxisAlignedBB getBoundingBox(Block block) {
        return CustomBlockUtils.getBoundingBox(this, block);
    }

    public record BlockTickSettings(int minTicks, int maxTicks, boolean looping) {
    }
}