package org.powernukkitx.block.customblock;

import org.powernukkitx.block.customblock.component.*;
import org.powernukkitx.block.customblock.data.Geometry;
import org.powernukkitx.block.customblock.data.Materials;
import org.powernukkitx.block.customblock.data.Permutation;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;

import java.util.*;

/**
 * Fluent builder for creating custom block definitions.
 * <p>
 * Components can be added individually or via shorthand methods.
 * The builder produces a {@link CustomBlockDefinition} that can be registered
 * with the server.
 * <p>
 * Example:
 * <pre>{@code
 * CustomBlockDefinition def = BlockBuilder.create("myplugin:my_block")
 *     .name("My Custom Block")
 *     .texture("my_texture")
 *     .geometry("geometry.my_model")
 *     .friction(0.6f)
 *     .lightEmission(10)
 *     .destructibleByMining(2.0f)
 *     .destructibleByExplosion(5)
 *     .creativeCategory("construction")
 *     .creativeGroup("itemGroup.name.planks")
 *     .build();
 * }</pre>
 */
public class BlockBuilder {
    private final String identifier;
    private final Map<BlockComponentIds, BlockComponent> components = new LinkedHashMap<>();
    private final List<Permutation> permutations = new ArrayList<>();
    private final List<String> blockTags = new ArrayList<>();
    private String name = "";
    private String texture = "";
    private Materials materials = null;
    private Geometry geometry = null;
    private boolean hasPlayerInteract = false;
    private boolean hasPlayerPlacing = false;
    private int blockTickMinTicks = -1;
    private int blockTickMaxTicks = -1;
    private boolean blockTickLooping = false;
    private boolean isStepSensor = false;
    private float friction = 0.4f;
    private float explosionResistance = 0.0f;
    private float miningTime = 0.0f;
    private int lightDampening = 15;
    private int lightEmission = 0;
    private String mapColor = "#ffffff";
    private String creativeCategory = "construction";
    private String creativeGroup = "";
    private boolean hiddenInCommands = false;

    private BlockBuilder(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Create a new BlockBuilder for the given identifier.
     */
    public static BlockBuilder create(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null");
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        return new BlockBuilder(identifier);
    }

    // ---- Shorthand component methods ----

    /**
     * Set the display name of the block.
     */
    public BlockBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set a single texture for all faces.
     */
    public BlockBuilder texture(String texture) {
        this.texture = texture;
        return this;
    }

    /**
     * Set materials using the existing Materials builder.
     */
    public BlockBuilder materials(Materials materials) {
        this.materials = materials;
        return this;
    }

    /**
     * Set geometry by identifier string.
     */
    public BlockBuilder geometry(String geometryId) {
        this.geometry = new Geometry(geometryId);
        return this;
    }

    /**
     * Set geometry using a Geometry object (with bone visibility, culling, etc).
     */
    public BlockBuilder geometry(Geometry geometry) {
        this.geometry = geometry;
        return this;
    }

    /**
     * Set friction factor (default 0.4).
     */
    public BlockBuilder friction(float friction) {
        this.friction = friction;
        return this;
    }

    /**
     * Set explosion resistance. -1 = indestructible, 0+ = resistance value.
     */
    public BlockBuilder destructibleByExplosion(boolean destructible) {
        this.explosionResistance = destructible ? 0 : -1;
        return this;
    }

    /**
     * Set explosion resistance (int).
     */
    public BlockBuilder destructibleByExplosion(int resistance) {
        this.explosionResistance = resistance;
        return this;
    }

    /**
     * Set mining time in seconds. -1 = unbreakable.
     */
    public BlockBuilder destructibleByMining(float seconds) {
        this.miningTime = seconds;
        return this;
    }

    /**
     * Set whether the block is breakable by mining.
     */
    public BlockBuilder destructibleByMining(boolean breakable) {
        this.miningTime = breakable ? 0.0f : -1.0f;
        return this;
    }

    /**
     * Set light dampening level (0-15).
     */
    public BlockBuilder lightDampening(int level) {
        this.lightDampening = Math.max(0, Math.min(15, level));
        return this;
    }

    /**
     * Set light emission level (0-15).
     */
    public BlockBuilder lightEmission(int emission) {
        this.lightEmission = Math.max(0, Math.min(15, emission));
        return this;
    }

    /**
     * Set map color as hex string (e.g. "#ff0000").
     */
    public BlockBuilder mapColor(String hexColor) {
        this.mapColor = hexColor;
        return this;
    }

    /**
     * Set creative category in the inventory.
     */
    public BlockBuilder creativeCategory(String category) {
        this.creativeCategory = category;
        return this;
    }

    /**
     * Set creative group in the inventory.
     */
    public BlockBuilder creativeGroup(String group) {
        this.creativeGroup = group;
        return this;
    }

    /**
     * Hide from /give and /replaceitem commands.
     */
    public BlockBuilder hiddenInCommands(boolean hidden) {
        this.hiddenInCommands = hidden;
        return this;
    }

    /**
     * Add block tags (used for querying).
     */
    public BlockBuilder blockTags(String... tags) {
        Collections.addAll(this.blockTags, tags);
        return this;
    }

    /**
     * Enable player interact detection.
     */
    public BlockBuilder playerInteractable(boolean value) {
        this.hasPlayerInteract = value;
        return this;
    }

    /**
     * Enable player placing sensor.
     */
    public BlockBuilder playerPlacingSensor(boolean value) {
        this.hasPlayerPlacing = value;
        return this;
    }

    /**
     * Enable step sensor (entity step on/off).
     */
    public BlockBuilder stepSensor(boolean value) {
        this.isStepSensor = value;
        return this;
    }

    /**
     * Set block tick interval.
     */
    public BlockBuilder blockTick(int minTicks, int maxTicks, boolean looping) {
        this.blockTickMinTicks = minTicks;
        this.blockTickMaxTicks = maxTicks;
        this.blockTickLooping = looping;
        return this;
    }

    /**
     * Add a permutation (conditional component override).
     */
    public BlockBuilder permutation(Permutation permutation) {
        this.permutations.add(permutation);
        return this;
    }

    // ---- Generic component methods ----

    /**
     * Add or replace a component.
     */
    public BlockBuilder component(BlockComponent component) {
        components.put(component.getId(), component);
        return this;
    }

    /**
     * Remove a component by ID.
     */
    public BlockBuilder removeComponent(BlockComponentIds id) {
        components.remove(id);
        return this;
    }

    /**
     * Check if a component is present.
     */
    public boolean hasComponent(BlockComponentIds id) {
        return components.containsKey(id);
    }

    /**
     * Get a component by ID.
     */
    @SuppressWarnings("unchecked")
    public <T extends BlockComponent> T getComponent(BlockComponentIds id) {
        return (T) components.get(id);
    }

    // ---- Collision & Selection box shortcuts ----

    /**
     * Set collision box.
     */
    public BlockBuilder collisionBox(Vector3f origin, Vector3f size) {
        CollisionBoxComponent comp = new CollisionBoxComponent();
        comp.addBox(origin, size);
        components.put(BlockComponentIds.COLLISION_BOX, comp);
        return this;
    }

    /**
     * Set selection box.
     */
    public BlockBuilder selectionBox(Vector3f origin, Vector3f size) {
        SelectionBoxComponent comp = new SelectionBoxComponent();
        comp.origin(origin);
        comp.size(size);
        components.put(BlockComponentIds.SELECTION_BOX, comp);
        return this;
    }

    /**
     * Set selection box with float values.
     */
    public BlockBuilder selectionBox(float ox, float oy, float oz, float sx, float sy, float sz) {
        SelectionBoxComponent comp = new SelectionBoxComponent();
        comp.origin(ox, oy, oz);
        comp.size(sx, sy, sz);
        components.put(BlockComponentIds.SELECTION_BOX, comp);
        return this;
    }

    /**
     * Set transformation component.
     */
    public BlockBuilder transformation(double tx, double ty, double tz,
                                       double sx, double sy, double sz,
                                       double rx, double ry, double rz) {
        TransformationComponent comp = new TransformationComponent();
        comp.translation(new org.powernukkitx.math.Vector3(tx, ty, tz));
        comp.scale(new org.powernukkitx.math.Vector3(sx, sy, sz));
        comp.rotation(new org.powernukkitx.math.Vector3(rx, ry, rz));
        components.put(BlockComponentIds.TRANSFORMATION, comp);
        return this;
    }

    /**
     * Set transformation with only rotation (common case).
     */
    public BlockBuilder rotation(double rx, double ry, double rz) {
        TransformationComponent comp = new TransformationComponent();
        comp.rotation(new org.powernukkitx.math.Vector3(rx, ry, rz));
        components.put(BlockComponentIds.TRANSFORMATION, comp);
        return this;
    }

    // ---- Build ----

    /**
     * Build the CustomBlockDefinition.
     * <p>
     * Assembles all components and shorthand values into a CompoundTag
     * compatible with the existing CustomBlockDefinition.Builder format.
     */
    public CustomBlockDefinition build() {
        CompoundTag nbt = new CompoundTag()
                .putCompound("components", buildComponents())
                .putCompound("menu_category", buildMenuCategory())
                .putInt("molangVersion", 9);

        // Permutations
        if (!permutations.isEmpty()) {
            ListTag<CompoundTag> permList = new ListTag<>(CompoundTag.class);
            for (Permutation p : permutations) {
                permList.add(p.toCompoundTag());
            }
            nbt.putList("permutations", permList);
        }

        // Block tags
        if (!blockTags.isEmpty()) {
            ListTag<StringTag> tagList = new ListTag<>(StringTag.class);
            for (String t : blockTags) {
                tagList.add(new StringTag(t));
            }
            nbt.putList("blockTags", tagList);
        }

        // Block properties (from Block interface)
        nbt.putList("properties", new ListTag<CompoundTag>());

        // Vanilla block data
        int blockId = getOrAllocateRuntimeId(identifier);
        nbt.putCompound("vanilla_block_data",
                new CompoundTag()
                        .putInt("block_id", blockId)
                        .putString("material", "dirt"));

        // Tick settings
        CustomBlockDefinition.BlockTickSettings tickSettings = null;
        if (blockTickMinTicks >= 0) {
            tickSettings = new CustomBlockDefinition.BlockTickSettings(
                    blockTickMinTicks, blockTickMaxTicks, blockTickLooping);
        }

        return new CustomBlockDefinition(identifier, nbt, tickSettings, isStepSensor);
    }

    private CompoundTag buildComponents() {
        CompoundTag components = new CompoundTag();

        // Apply shorthand values as components
        applyFrictionComponent(components);
        applyExplosionComponent(components);
        applyMiningComponent(components);
        applyLightDampeningComponent(components);
        applyLightEmissionComponent(components);
        applyMapColorComponent(components);
        applyDisplayNameComponent(components);
        applyTextureComponent(components);
        applyMaterialsComponent(components);
        applyGeometryComponent(components);
        applyCustomComponents(components);

        // Apply explicitly added components
        for (Map.Entry<BlockComponentIds, BlockComponent> entry : components.entrySet()) {
            BlockComponentIds id = entry.getKey();
            if (!components.contains(id.getId())) {
                components.putCompound(id.getId(), entry.getValue().toNBT());
            }
        }

        return components;
    }

    private void applyFrictionComponent(CompoundTag components) {
        components.putCompound("minecraft:friction",
                new CompoundTag().putFloat("value", friction));
    }

    private void applyExplosionComponent(CompoundTag components) {
        components.putCompound("minecraft:destructible_by_explosion",
                new CompoundTag().putInt("explosion_resistance", (int) explosionResistance));
    }

    private void applyMiningComponent(CompoundTag components) {
        components.putCompound("minecraft:destructible_by_mining",
                new CompoundTag().putFloat("value", miningTime));
    }

    private void applyLightDampeningComponent(CompoundTag components) {
        components.putCompound("minecraft:light_dampening",
                new CompoundTag().putByte("lightLevel", (byte) lightDampening));
    }

    private void applyLightEmissionComponent(CompoundTag components) {
        components.putCompound("minecraft:light_emission",
                new CompoundTag().putByte("emission", (byte) lightEmission));
    }

    private void applyMapColorComponent(CompoundTag components) {
        components.putString("minecraft:map_color", mapColor);
    }

    private void applyDisplayNameComponent(CompoundTag components) {
        if (name != null && !name.isBlank()) {
            components.putCompound("minecraft:display_name",
                    new CompoundTag().putString("value", name));
        }
    }

    private void applyTextureComponent(CompoundTag components) {
        if (texture != null && !texture.isBlank()) {
            CompoundTag materialInstances = getOrCreateMaterialInstances(components);
            CompoundTag materials = materialInstances.getCompound("materials");
            if (materials == null || materials.isEmpty()) {
                materials = new CompoundTag(new LinkedHashMap<>());
                materialInstances.putCompound("materials", materials);
            }

            // Set "*" texture
            CompoundTag star = materials.contains("*") ? materials.getCompound("*") : new CompoundTag();
            star.putString("texture", texture);
            materials.putCompound("*", star);

            // Update all existing face textures
            for (String face : new String[]{"up", "down", "north", "south", "east", "west"}) {
                if (materials.contains(face)) {
                    CompoundTag faceTag = materials.getCompound(face);
                    faceTag.putString("texture", texture);
                    materials.putCompound(face, faceTag);
                }
            }

            materialInstances.putCompound("materials", materials);
            components.putCompound("minecraft:material_instances", materialInstances);
        }
    }

    private void applyMaterialsComponent(CompoundTag components) {
        if (materials != null) {
            CompoundTag materialInstances = getOrCreateMaterialInstances(components);
            CompoundTag baseMaterials = materialInstances.getCompound("materials");
            if (baseMaterials == null || baseMaterials.isEmpty()) {
                baseMaterials = new CompoundTag(new LinkedHashMap<>());
            }

            CompoundTag customMaterials = materials.toCompoundTag();
            for (String key : customMaterials.getTags().keySet()) {
                baseMaterials.putCompound(key, customMaterials.getCompound(key));
            }

            materialInstances.putCompound("materials", baseMaterials);
            components.putCompound("minecraft:material_instances", materialInstances);
        }
    }

    private void applyGeometryComponent(CompoundTag components) {
        if (geometry != null) {
            CompoundTag base = CustomBlockDefinition.createDefaultGeometry(null);
            CompoundTag custom = geometry.toCompoundTag();
            for (String key : custom.getTags().keySet()) {
                base.put(key, custom.get(key));
            }
            components.putCompound("minecraft:geometry", base);
        }
    }

    private void applyCustomComponents(CompoundTag components) {
        if (hasPlayerInteract || hasPlayerPlacing) {
            CompoundTag customComponents = new CompoundTag()
                    .putByte("hasPlayerInteract", (byte) (hasPlayerInteract ? 1 : 0))
                    .putByte("hasPlayerPlacing", (byte) (hasPlayerPlacing ? 1 : 0))
                    .putByte("isV1Component", (byte) 1);
            components.putCompound("minecraft:custom_components", customComponents);
        }
    }

    private CompoundTag buildMenuCategory() {
        return new CompoundTag(new LinkedHashMap<>())
                .putString("category", creativeCategory.toLowerCase(Locale.ROOT))
                .putString("group", creativeGroup)
                .putByte("is_hidden_in_commands", (byte) (hiddenInCommands ? 1 : 0));
    }

    private CompoundTag getOrCreateMaterialInstances(CompoundTag components) {
        CompoundTag existing = components.getCompound("minecraft:material_instances");
        if (existing != null && !existing.isEmpty()) {
            return existing;
        }
        CompoundTag mi = new CompoundTag(new LinkedHashMap<>());
        mi.putCompound("materials", new CompoundTag(new LinkedHashMap<>()));
        mi.putCompound("mappings", new CompoundTag());
        return mi;
    }

    // ---- Runtime ID allocation ----

    private static final Map<String, Integer> RUNTIME_ID_MAP = new LinkedHashMap<>();
    private static int NEXT_RUNTIME_ID = 10000;

    private static synchronized int getOrAllocateRuntimeId(String identifier) {
        return RUNTIME_ID_MAP.computeIfAbsent(identifier, k -> NEXT_RUNTIME_ID++);
    }
}