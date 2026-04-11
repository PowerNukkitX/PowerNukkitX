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
import cn.nukkit.entity.Entity;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.item.customitem.data.CreativeGroup;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.BlockPropertyData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
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
public record CustomBlockDefinition(String identifier, NbtMap nbt, @Nullable BlockTickSettings tickSettings,
                                    boolean isStepSensor) {
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
        private BlockTickSettings tickSettings = null;
        private boolean isStepSensor = false;

        protected NbtMap nbt = NbtMap.builder()
                .putCompound("components", NbtMap.EMPTY)
                .build();

        protected Builder(CustomBlock customBlock) {
            this.identifier = customBlock.getId();
            this.customBlock = customBlock;
            var components = this.nbt.getCompound("components");

            // Set default components using static default values
            NbtMap defaults = createDefaultComponents(
                    0.4f,
                    0.0f,
                    0.0f,
                    15,
                    0,
                    "#ffffff"
            );
            components.putAll(defaults);

            // Setting up  default material instances
            this.nbt = getOrCreateMaterialInstances(this.nbt);
            //CompoundTag defaultMaterial = getOrCreateMaterialInstances(components); << TO REMOVE???
            //components.putCompound("minecraft:material_instances", defaultMaterial); << TO REMOVE???
            final NbtMapBuilder builder = this.nbt.toBuilder();

            // Sets the default geometry
            builder.putCompound("components", components.toBuilder().putCompound("minecraft:geometry", createDefaultGeometry(null)).build());

            // Set the category of the block in the creation column
            builder.putCompound("menu_category", createDefaultMenuCategory());
            // Molang version
            builder.putInt("molangVersion", 9);

            // Set the properties of the block
            var propertiesNBT = getPropertiesNBT();
            if (propertiesNBT != null) {
                builder.putList("properties", NbtType.COMPOUND, propertiesNBT);
            }

            int block_id;
            if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(identifier)) {
                while (INTERNAL_ALLOCATION_ID_MAP.containsValue(block_id = CUSTOM_BLOCK_RUNTIMEID.getAndIncrement())) {
                }
                INTERNAL_ALLOCATION_ID_MAP.put(identifier, block_id);
            } else {
                block_id = INTERNAL_ALLOCATION_ID_MAP.getInt(identifier);
            }
            builder.putCompound("vanilla_block_data", NbtMap.builder().putInt("block_id", block_id).build()
                    /*.putString("material", "")*/); //todo Figure what is dirt, maybe that corresponds to https://wiki.bedrock.dev/documentation/materials.html
        }

        public Builder name(String name) {
            Preconditions.checkArgument(!name.isBlank(), "name is blank");
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putCompound("minecraft:display_name", NbtMap.builder()
                            .putString("value", name)
                            .build())
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
        }

        /**
         * Sets the friction value of the block. Default is 0.6f.
         */
        public Builder friction(float value) {
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putCompound("minecraft:friction", NbtMap.builder()
                            .putFloat("value", value)
                            .build())
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
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
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putCompound("minecraft:destructible_by_explosion", NbtMap.builder()
                            .putInt("explosion_resistance", resistance)
                            .build())
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
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
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putCompound("minecraft:destructible_by_mining", NbtMap.builder()
                            .putFloat("value", seconds)
                            .build())
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
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
        public Builder lightDampening(int lightLevel) {
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putCompound("minecraft:light_dampening", NbtMap.builder()
                            .putByte("lightLevel", (byte) lightLevel)
                            .build())
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
        }

        /**
         * Sets the light emission level. Default is 0.
         */
        public Builder lightEmission(int emission) {
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putCompound("minecraft:light_emission", NbtMap.builder()
                            .putByte("emission", (byte) emission)
                            .build())
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
        }

        /**
         * Sets the map color used in maps. Default is #ffffff.
         */
        public Builder mapColor(String hexColor) {
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putString("minecraft:map_color", hexColor)
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
        }

        /**
         * Set the texture of the block, use only if you are setting simple texture, all the sides of the block will have this same texture.
         */
        public Builder texture(String texture) {
            String tex = (texture != null && !texture.isBlank()) ? texture : "missing_texture";

            NbtMap nbt = getOrCreateMaterialInstances(this.nbt);
            NbtMap components = nbt.getCompound("components");
            NbtMap mi = components.getCompound("minecraft:material_instances");
            NbtMap mats = mi.getCompound("materials");
            if (mats == null || mats.isEmpty()) {
                mats = NbtMap.EMPTY;
                mi = mi.toBuilder().putCompound("materials", mats).build();
            }

            NbtMapBuilder star = mats.containsKey("*") ? mats.getCompound("*").toBuilder() : NbtMap.builder();
            star.putString("texture", tex);
            mats = mats.toBuilder().putCompound("*", star.build()).build();

            for (Map.Entry<String, Object> entry : mats.entrySet()) {
                String face = entry.getKey();
                if ("*".equals(face)) continue;
                Object tag = entry.getValue();
                if (tag instanceof NbtMap faceTag) {
                    faceTag = faceTag.toBuilder().putString("texture", tex).build();
                    mats = mats.toBuilder().putCompound(face, faceTag).build();
                }
            }

            mi = mi.toBuilder().putCompound("materials", mats).build();
            components = components.toBuilder().putCompound("minecraft:material_instances", mi).build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
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
            NbtMap nbt = getOrCreateMaterialInstances(this.nbt);
            NbtMap components = nbt.getCompound("components");
            NbtMap mi = components.getCompound("minecraft:material_instances");
            NbtMap baseMaterials = mi.getCompound("materials");
            if (baseMaterials == null || baseMaterials.isEmpty()) {
                baseMaterials = NbtMap.EMPTY;
            }

            NbtMap customMaterials = materials.toCompoundTag();

            for (Map.Entry<String, Object> customEntry : customMaterials.entrySet()) {
                String key = customEntry.getKey();
                NbtMap customMat = (NbtMap) customEntry.getValue();
                NbtMap baseMat = baseMaterials.containsKey(key)
                        ? baseMaterials.getCompound(key)
                        : NbtMap.EMPTY;
                baseMat.putAll(customMat);

                baseMaterials = baseMaterials.toBuilder().putCompound(key, baseMat).build();
            }

            mi = mi.toBuilder().putCompound("materials", baseMaterials).build();
            components = components.toBuilder().putCompound("minecraft:material_instances", mi).build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
        }

        public Builder creativeCategory(String creativeCategory) {
            if (!this.nbt.containsKey("menu_category")) {
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", createDefaultMenuCategory()).build();
            } else {
                final NbtMap menuCategory = this.nbt.getCompound("menu_category").toBuilder()
                        .putString("category", creativeCategory.toLowerCase(Locale.ENGLISH))
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", menuCategory).build();
            }
            return this;
        }

        public Builder creativeCategory(CreativeCategory creativeCategory) {
            if (!this.nbt.containsKey("menu_category")) {
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", createDefaultMenuCategory()).build();
            } else {
                final NbtMap menuCategory = this.nbt.getCompound("menu_category").toBuilder()
                        .putString("category", creativeCategory.name().toLowerCase(Locale.ENGLISH))
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", menuCategory).build();
            }
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
            if (!this.nbt.containsKey("menu_category")) {
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", createDefaultMenuCategory()).build();
            } else {
                final NbtMap menuCategory = this.nbt.getCompound("menu_category").toBuilder()
                        .putString("group", creativeGroup)
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", menuCategory).build();
            }
            return this;
        }

        /**
         * Control the grouping of custom blocks in the creation inventory.
         *
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html">wiki.bedrock.dev</a>
         */
        public Builder creativeGroup(CreativeGroup creativeGroup) {
            if (!this.nbt.containsKey("menu_category")) {
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", createDefaultMenuCategory()).build();
            } else {
                final NbtMap menuCategory = this.nbt.getCompound("menu_category").toBuilder()
                        .putString("group", creativeGroup.getGroupName())
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", menuCategory).build();
            }
            return this;
        }

        /**
         * Sets whether the item/block should be hidden from commands like /give and /replaceitem.
         *
         * @param hidden true to hide, false to show (default: false)
         * @return this builder
         */
        public Builder isHiddenInCommands(boolean hidden) {
            if (!this.nbt.containsKey("menu_category")) {
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", createDefaultMenuCategory()).build();
            } else {
                final NbtMap menuCategory = this.nbt.getCompound("menu_category").toBuilder()
                        .putByte("is_hidden_in_commands", (byte) (hidden ? 1 : 0))
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("menu_category", menuCategory).build();
            }
            return this;
        }

        /**
         * @see <a href="https://wiki.bedrock.dev/blocks/block-components.html#crafting-table">wiki.bedrock.dev</a>
         */
        public Builder craftingTable(CraftingTable craftingTable) {
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putCompound("minecraft:crafting_table", craftingTable.toCompoundTag())
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
        }

        /**
         * supports rotation, scaling, and translation. The component can be added to the whole block and/or to individual block permutations. Transformed geometries still have the same restrictions that non-transformed geometries have such as a maximum size of 30/16 units.
         */
        public Builder transformation(@NotNull Transformation transformation) {
            final NbtMap components = this.nbt.getCompound("components").toBuilder()
                    .putCompound("minecraft:transformation", transformation.toCompoundTag())
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
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
            NbtMap mergedGeometry = createDefaultGeometry(geometry.toLowerCase(Locale.ENGLISH));
            var components = this.nbt.getCompound("components")
                    .toBuilder().putCompound("minecraft:geometry", mergedGeometry)
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
        }

        /**
         * Sets a full geometry definition with optional bone visibility. If not set the default is the full_block.
         * <p>
         * Use this to control visible bones or other geometry properties.
         */
        public Builder geometry(@NotNull Geometry geometry) {
            NbtMap base = createDefaultGeometry(null);
            NbtMap custom = geometry.toCompoundTag();
            for (Map.Entry<String, Object> entry : custom.entrySet()) {
                base.put(entry.getKey(), entry.getValue());
            }
            var components = this.nbt.getCompound("components")
                    .toBuilder().putCompound("minecraft:geometry", base)
                    .build();
            this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            return this;
        }

        /**
         * Control custom block permutation features such as conditional rendering, partial rendering, etc.
         */
        public Builder permutation(Permutation permutation) {
            if (!this.nbt.containsKey("permutations")) {
                this.nbt = this.nbt.toBuilder().putList("permutations", NbtType.COMPOUND, new ObjectArrayList<>()).build();
            }
            List<NbtMap> permutations = new ObjectArrayList<>(this.nbt.getList("permutations", NbtType.COMPOUND));
            permutations.add(permutation.toCompoundTag());
            this.nbt = this.nbt.toBuilder().putList("permutations", NbtType.COMPOUND, permutations).build();
            return this;
        }

        /**
         * Control custom block permutation features such as conditional rendering, partial rendering, etc.
         */
        public Builder permutations(Permutation... permutations) {
            final List<NbtMap> per = new ObjectArrayList<>();
            for (var permutation : permutations) {
                per.add(permutation.toCompoundTag());
            }
            this.nbt = this.nbt.toBuilder().putList("permutations", NbtType.COMPOUND, per).build();
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

            NbtMap components = this.nbt.getCompound("components");
            NbtMap collision = components.getCompound("minecraft:collision_box");
            if (collision.isEmpty()) collision = collision.toBuilder().putBoolean("enabled", true).build();

            List<NbtMap> boxes = new ObjectArrayList<>(collision.getList("boxes", NbtType.COMPOUND));
            boxes.add(NbtMap.builder()
                    .putFloat("minX", minX)
                    .putFloat("minY", minY)
                    .putFloat("minZ", minZ)
                    .putFloat("maxX", maxX)
                    .putFloat("maxY", maxY)
                    .putFloat("maxZ", maxZ)
                    .build());

            collision = collision.toBuilder().putList("boxes", NbtType.COMPOUND, boxes).build();
            this.nbt.toBuilder().putCompound("components", components.toBuilder().putCompound("minecraft:collision_box", collision).build()).build();
            return this;
        }

        /**
         * Sets the block selection box.
         *
         * @param origin The origin of the collision box
         * @param size   The size of the collision box
         */
        public Builder selectionBox(@NotNull Vector3f origin, @NotNull Vector3f size) {
            this.nbt = this.nbt.toBuilder()
                    .putCompound("components", this.nbt.getCompound("components").toBuilder()
                            .putCompound("minecraft:selection_box", NbtMap.builder()
                                    .putBoolean("enabled", true)
                                    .putList("origin", NbtType.FLOAT, Arrays.asList(origin.x, origin.y, origin.z))
                                    .putList("size", NbtType.FLOAT, Arrays.asList(size.x, size.y, size.z))
                                    .build())
                            .build()
                    ).build();
            return this;
        }

        public Builder blockTags(String... tag) {
            Preconditions.checkNotNull(tag);
            Preconditions.checkArgument(tag.length > 0);
            List<String> stringTagListTag = new ObjectArrayList<>();
            stringTagListTag.addAll(Arrays.asList(tag));
            this.nbt = this.nbt.toBuilder().putList("blockTags", NbtType.STRING, stringTagListTag).build();
            return this;
        }

        public Builder isPlayerInteractable(boolean value) {
            NbtMap components = this.nbt.getCompound("components");
            if (!components.containsKey("minecraft:custom_components")) {
                components = components.toBuilder()
                        .putCompound("minecraft:custom_components", createDefaultCustomComponents())
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            } else {
                final NbtMap customComponents = components.getCompound("minecraft:custom_components").toBuilder()
                        .putByte("hasPlayerInteract", (byte) (value ? 1 : 0))
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("components", components.toBuilder().putCompound("minecraft:custom_components", customComponents).build()).build();
            }
            return this;
        }

        public Builder hasPlayerPlacingSensor(boolean value) {
            NbtMap components = this.nbt.getCompound("components");
            if (!components.containsKey("minecraft:custom_components")) {
                components = components.toBuilder()
                        .putCompound("minecraft:custom_components", createDefaultCustomComponents())
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("components", components).build();
            } else {
                final NbtMap customComponents = components.getCompound("minecraft:custom_components").toBuilder()
                        .putByte("hasPlayerPlacing", (byte) (value ? 1 : 0))
                        .build();
                this.nbt = this.nbt.toBuilder().putCompound("components", components.toBuilder().putCompound("minecraft:custom_components", customComponents).build()).build();
            }
            return this;
        }

        /**
         * Defines how this custom block should tick over time.
         *
         * @param minTicks The minimum number of ticks before the block updates.
         * @param maxTicks The maximum number of ticks before the block updates. Must be ≥ {@code minTicks}.
         * @param looping  If {@code true}, the block will continue ticking; if {@code false}, it will tick only once.
         * @return This builder instance for chaining.
         * <p>
         * Example: {@code .blockTick(60, 60, true)} will schedule the block to tick every 3 seconds.
         */
        public Builder blockTick(int minTicks, int maxTicks, boolean looping) {
            Preconditions.checkArgument(minTicks >= 0 && maxTicks >= minTicks, "Invalid tick interval range");
            this.tickSettings = new BlockTickSettings(minTicks, maxTicks, looping);
            return this;
        }

        /**
         * Enables step sensor logic (entity step-on/off).
         * <p>
         * When enabled, override {@link Block#onEntityStepOn(Entity)} and {@link Block#onEntityStepOff(Entity)} for custom handling.
         */
        public Builder isStepSensor(boolean value) {
            this.isStepSensor = value;
            return this;
        }

        /**
         * @return Block Properties in NBT Tag format
         */
        @Nullable
        private List<NbtMap> getPropertiesNBT() {
            if (this.customBlock instanceof Block block) {
                var properties = block.getProperties();
                Set<BlockPropertyType<?>> propertyTypeSet = properties.getPropertyTypeSet();
                if (propertyTypeSet.isEmpty()) {
                    return null;
                }
                final List<NbtMap> nbtList = new ObjectArrayList<>();
                for (var each : propertyTypeSet) {
                    if (each instanceof BooleanPropertyType booleanBlockProperty) {
                        nbtList.add(NbtMap.builder().putString("name", booleanBlockProperty.getName())
                                .putList("enum", NbtType.BYTE, Arrays.asList((byte) 0, (byte) 1))
                                .build());
                    } else if (each instanceof IntPropertyType intBlockProperty) {
                        final List<Integer> enumList = new IntArrayList();
                        for (int i = intBlockProperty.getMin(); i <= intBlockProperty.getMax(); i++) {
                            enumList.add(i);
                        }
                        nbtList.add(NbtMap.builder().putString("name", intBlockProperty.getName()).putList("enum", NbtType.INT, enumList).build());
                    } else if (each instanceof EnumPropertyType<?> arrayBlockProperty) {
                        final List<String> enumList = new ObjectArrayList<>();
                        for (var e : arrayBlockProperty.getValidValues()) {
                            enumList.add(e.name().toLowerCase(Locale.ENGLISH));
                        }
                        nbtList.add(NbtMap.builder().putString("name", arrayBlockProperty.getName()).putList("enum", NbtType.STRING, enumList).build());
                    }
                }
                return nbtList;
            }
            return null;
        }

        /**
         * Custom processing of the block to be sent to the client ComponentNBT, which contains all definitions for custom block. You can modify them as much as you want, under the right conditions.
         */
        public CustomBlockDefinition customBuild(@NotNull Consumer<NbtMap> nbt) {
            var def = this.build();
            nbt.accept(def.nbt);
            return def;
        }

        public CustomBlockDefinition build() {
            return new CustomBlockDefinition(this.identifier, this.nbt, this.tickSettings, this.isStepSensor);
        }
    }

    // Creates default geometry
    public static NbtMap createDefaultGeometry(String identifierOverride) {
        return NbtMap.builder()
                .putCompound("bone_visibility", NbtMap.EMPTY)
                .putString("culling", "")
                .putString("culling_layer", "minecraft:culling_layer.undefined")
                .putString("identifier", identifierOverride != null ? identifierOverride : "minecraft:geometry.full_block")
                .putByte("ignoreGeometryForIsSolid", (byte) 1)
                .putByte("needsLegacyTopRotation", (byte) 0)
                .putByte("useLegacyBlockLightAbsorption", (byte) 0)
                .putByte("uv_lock", (byte) 0)
                .build();
    }

    // Creates default materials instance
    private static NbtMap getOrCreateMaterialInstances(NbtMap nbt) {
        NbtMap components = nbt.getCompound("components");
        NbtMap material = components.getCompound("minecraft:material_instances");
        if (material != null && !material.isEmpty()) return material;

        // create once
        NbtMapBuilder materialsBuilder = NbtMap.builder();
        NbtMap star = NbtMap.builder()
                .putFloat("ambient_occlusion", 1.0f)
                .putByte("packed_bools", (byte) 0x1)
                .putByte("isotropic", (byte) 0)
                .putString("render_method", "opaque")
                .putString("texture", "missing_texture")
                .putString("tint_method", "none")
                .build();
        materialsBuilder.putCompound("*", star);

        material = NbtMap.builder()
                .putCompound("mappings", NbtMap.EMPTY)
                .putCompound("materials", materialsBuilder.build())
                .build();

        components = components.toBuilder().putCompound("minecraft:material_instances", material).build();

        nbt = nbt.toBuilder().putCompound("components", components).build();
        return nbt;
    }

    // Creates default category
    public static NbtMap createDefaultMenuCategory() {
        return NbtMap.builder()
                .putString("category", "construction")
                .putString("group", "")
                .putByte("is_hidden_in_commands", (byte) 0)
                .build();
    }

    // Create default components
    public static NbtMap createDefaultComponents(
            float frictionFactor,
            float explosionResistance,
            float miningTime,
            int lightDampening,
            int lightEmission,
            String mapColor
    ) {
        return NbtMap.builder()
                .putCompound("minecraft:friction", NbtMap.builder()
                        .putFloat("value", 0.4f)
                        .build())
                .putCompound("minecraft:destructible_by_explosion", NbtMap.builder()
                        .putInt("explosion_resistance", 0)
                        .build())
                .putCompound("minecraft:destructible_by_mining", NbtMap.builder()
                        .putFloat("value", 0.0f)
                        .build())
                .putCompound("minecraft:light_dampening", NbtMap.builder()
                        .putByte("lightLevel", (byte) 15)
                        .build())
                .putCompound("minecraft:light_emission", NbtMap.builder()
                        .putByte("emission", (byte) 0)
                        .build())
                .putCompound("minecraft:map_color", NbtMap.builder()
                        .putString("", "#ffffff")
                        .build())
                .build();
    }

    // Creates default custom_components
    public static NbtMap createDefaultCustomComponents() {
        return NbtMap.builder()
                .putByte("hasPlayerInteract", (byte) 0)
                .putByte("hasPlayerPlacing", (byte) 0)
                .putByte("isV1Component", (byte) 1)
                .build();
    }

    public NbtMap getComponents() {
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

    public BlockPropertyData toNetwork() {
        return new BlockPropertyData(
                this.identifier,
                this.nbt
        );
    }
}