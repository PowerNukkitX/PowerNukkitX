package org.powernukkitx.addons.parser;

import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.customblock.BlockDataDriven;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.block.customblock.data.CollisionBox;
import org.powernukkitx.block.customblock.data.CraftingTable;
import org.powernukkitx.block.customblock.data.Geometry;
import org.powernukkitx.block.customblock.data.Transformation;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.block.property.type.BooleanPropertyType;
import org.powernukkitx.block.property.type.IntPropertyType;
import org.powernukkitx.block.property.type.StringEnumPropertyType;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.molang.MolangExpression;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.plugin.InternalPlugin;
import org.powernukkitx.registry.BlockRegistry;
import org.powernukkitx.registry.RegisterException;
import org.powernukkitx.registry.Registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Turns a behaviour pack's {@code blocks/*.json} into registered blocks.
 * <p>
 * Gameplay components ({@code minecraft:destructible_by_mining}, {@code minecraft:friction},
 * light, boxes, geometry...) go through {@link CustomBlockDefinition.Builder}, which sets both the
 * definition NBT the client is sent and the {@link org.powernukkitx.block.definition.BlockDefinition}
 * values the server reads, so a parsed block behaves like a hand-written custom block. Only what the
 * builder does not model - material instances, disabled boxes and permutations - is written as raw
 * NBT, in the schema the client expects.
 * <p>
 * Permutation components are client-side only: the server's definition is per identifier, so a
 * permutation that changes a gameplay value such as friction changes what the client predicts but
 * not what the server applies. Permutation tags are the exception and are resolved per state.
 * <p>
 * A component without a mapping is recorded on the {@link AddonFeatureTracker} rather than failing
 * the block.
 */
@Slf4j
public final class BehaviorBlockParser {

    /**
     * Caps the state combinations of one block. Every combination is registered as a block state and
     * has its conditional tags resolved at load, so an unbounded product would cost memory and boot
     * time for no gameplay benefit.
     */
    static final int MAX_STATE_PERMUTATIONS = 1 << 16;

    private static final Vector3f DEFAULT_BOX_ORIGIN = new Vector3f(-8f, 0f, -8f);
    private static final Vector3f DEFAULT_BOX_SIZE = new Vector3f(16f, 16f, 16f);

    private final AddonFeatureTracker tracker;

    public BehaviorBlockParser(AddonFeatureTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * The result of parsing one document, before anything is registered.
     */
    public record ParsedBlock(@NotNull String identifier, @NotNull BlockDataDriven.Spec spec) {
    }

    /**
     * Parses one block document and registers the block it describes.
     * <p>
     * A block is skipped, with the reason recorded on the tracker, when it overrides a vanilla
     * identifier, when another pack already registered it, or when the registry rejects it - most
     * notably when one of its states would collide with an existing state on the client.
     *
     * @param packName the pack the document came from, for logging
     * @param fileName the entry name inside the pack, for logging
     * @param root     the parsed document
     * @return the registered identifier, or null when the block was skipped
     */
    public @Nullable String parseAndRegister(@NotNull String packName, @NotNull String fileName, @NotNull JsonObject root) {
        ParsedBlock parsed = parseDefinition(packName, fileName, root);
        if (parsed == null) {
            return null;
        }
        if (BlockRegistry.getCustomBlockDefinitionByIdStatic(parsed.identifier()) != null) {
            tracker.reportUnsupported(packName, "duplicate-block-definition", parsed.identifier());
            return null;
        }
        BlockDataDriven.Spec spec = parsed.spec();
        try {
            Registries.BLOCK.registerCustomBlockDefinition(InternalPlugin.INSTANCE,
                    new BlockDataDriven(null, spec), spec.properties(), state -> new BlockDataDriven(state, spec));
            log.debug("Registered data-driven block {} from behavior pack '{}'", parsed.identifier(), packName);
            return parsed.identifier();
        } catch (RegisterException e) {
            tracker.reportUnsupported(packName, "block-rejected", parsed.identifier());
            log.warn("Behavior pack '{}': block {} was not registered: {}", packName, parsed.identifier(), e.getMessage());
            return null;
        }
    }

    /**
     * Parses one block document without touching any registry.
     *
     * @return the parsed block, or null when the document was skipped
     */
    public @Nullable ParsedBlock parseDefinition(@NotNull String packName, @NotNull String fileName, @NotNull JsonObject root) {
        JsonObject blockJson = root.getAsJsonObject("minecraft:block");
        if (blockJson == null) {
            log.warn("Behavior pack '{}': {} has no minecraft:block element, skipped", packName, fileName);
            return null;
        }
        JsonObject description = blockJson.getAsJsonObject("description");
        if (description == null || !description.has("identifier")) {
            log.warn("Behavior pack '{}': {} has no block identifier, skipped", packName, fileName);
            return null;
        }
        String identifier = description.get("identifier").getAsString();
        if (identifier.startsWith("minecraft:")) {
            tracker.reportUnsupported(packName, "vanilla-block-override", identifier);
            return null;
        }

        try {
            JsonObject components = blockJson.getAsJsonObject("components");
            JsonArray permutations = blockJson.getAsJsonArray("permutations");
            Set<String> baseTags = collectTags(components);
            BlockProperties properties = buildProperties(packName, identifier, baseTags,
                    description.getAsJsonObject("states"), description.getAsJsonObject("traits"));
            if (properties == null) {
                return null;
            }
            CustomBlockDefinition definition = buildDefinition(packName, identifier, properties, description,
                    components, permutations, baseTags);
            BlockDataDriven.Spec spec = BlockDataDriven.Spec.of(properties, definition, baseTags, collectTagRules(permutations));
            return new ParsedBlock(identifier, spec);
        } catch (RuntimeException e) {
            log.error("Behavior pack '{}': could not parse block {}", packName, identifier, e);
            return null;
        }
    }

    private @Nullable BlockProperties buildProperties(String packName, String identifier, Set<String> baseTags,
                                                      @Nullable JsonObject states, @Nullable JsonObject traits) {
        List<BlockPropertyType<?>> types = new ArrayList<>();
        if (states != null) {
            for (Map.Entry<String, JsonElement> state : states.entrySet()) {
                BlockPropertyType<?> type = buildStateType(packName, state.getKey(), state.getValue());
                if (type == null) {
                    return null;
                }
                types.add(type);
            }
        }
        types.addAll(buildTraitStates(packName, traits));

        long combinations = 1;
        for (BlockPropertyType<?> type : types) {
            combinations *= type.getValidValues().size();
            if (combinations > MAX_STATE_PERMUTATIONS) {
                log.error("Behavior pack '{}': block {} has more than {} state combinations, skipped",
                        packName, identifier, MAX_STATE_PERMUTATIONS);
                return null;
            }
        }
        return new BlockProperties(identifier, baseTags, types.toArray(new BlockPropertyType<?>[0]));
    }

    /**
     * Block traits implicitly add states such as {@code minecraft:cardinal_direction}. Permutation
     * conditions query those states, and a condition that queries a state the block does not declare
     * fails the client, so every enabled trait state is declared here.
     */
    private List<BlockPropertyType<?>> buildTraitStates(String packName, @Nullable JsonObject traits) {
        List<BlockPropertyType<?>> types = new ArrayList<>();
        if (traits == null) {
            return types;
        }
        for (Map.Entry<String, JsonElement> trait : traits.entrySet()) {
            String traitName = trait.getKey();
            if (!traitName.equals("minecraft:placement_direction") && !traitName.equals("minecraft:placement_position")) {
                tracker.reportUnsupported(packName, "block-trait", traitName);
                continue;
            }
            JsonElement enabledStates = trait.getValue().isJsonObject()
                    ? trait.getValue().getAsJsonObject().get("enabled_states") : null;
            if (enabledStates == null || !enabledStates.isJsonArray()) {
                continue;
            }
            for (JsonElement stateElement : enabledStates.getAsJsonArray()) {
                if (!stateElement.isJsonPrimitive()) {
                    continue;
                }
                switch (stateElement.getAsString()) {
                    case "minecraft:cardinal_direction" -> types.add(StringEnumPropertyType.of(
                            "minecraft:cardinal_direction", List.of("south", "west", "north", "east"), "south"));
                    case "minecraft:facing_direction" -> types.add(StringEnumPropertyType.of(
                            "minecraft:facing_direction", List.of("down", "up", "north", "south", "east", "west"), "down"));
                    case "minecraft:block_face" -> types.add(StringEnumPropertyType.of(
                            "minecraft:block_face", List.of("down", "up", "north", "south", "east", "west"), "down"));
                    case "minecraft:vertical_half" -> types.add(StringEnumPropertyType.of(
                            "minecraft:vertical_half", List.of("bottom", "top"), "bottom"));
                    default -> tracker.reportUnsupported(packName, "block-trait-state", stateElement.getAsString());
                }
            }
        }
        return types;
    }

    private @Nullable BlockPropertyType<?> buildStateType(String packName, String name, JsonElement definition) {
        if (definition.isJsonObject() && definition.getAsJsonObject().has("values")) {
            JsonElement values = definition.getAsJsonObject().get("values");
            if (values.isJsonObject() && values.getAsJsonObject().has("min") && values.getAsJsonObject().has("max")) {
                int min = values.getAsJsonObject().get("min").getAsInt();
                int max = values.getAsJsonObject().get("max").getAsInt();
                return IntPropertyType.of(name, min, max, min);
            }
            if (!values.isJsonArray()) {
                tracker.reportUnsupported(packName, "block-state", name);
                return null;
            }
            definition = values;
        }
        if (!definition.isJsonArray() || definition.getAsJsonArray().isEmpty()) {
            tracker.reportUnsupported(packName, "block-state", name);
            return null;
        }
        JsonArray values = definition.getAsJsonArray();
        JsonElement first = values.get(0);
        if (first.isJsonPrimitive() && first.getAsJsonPrimitive().isBoolean()) {
            return BooleanPropertyType.of(name, first.getAsBoolean());
        }
        if (first.isJsonPrimitive() && first.getAsJsonPrimitive().isNumber()) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (JsonElement value : values) {
                int number = value.getAsInt();
                min = Math.min(min, number);
                max = Math.max(max, number);
            }
            if (max - min + 1 != values.size()) {
                tracker.reportUnsupported(packName, "block-state-noncontiguous-int", name);
                return null;
            }
            return IntPropertyType.of(name, min, max, first.getAsInt());
        }
        List<String> strings = new ArrayList<>();
        for (JsonElement value : values) {
            strings.add(value.getAsString());
        }
        return StringEnumPropertyType.of(name, strings, strings.getFirst());
    }

    private CustomBlockDefinition buildDefinition(String packName, String identifier, BlockProperties properties,
                                                  JsonObject description, @Nullable JsonObject components,
                                                  @Nullable JsonArray permutations, Set<String> baseTags) {
        CustomBlockDefinition.Builder builder = CustomBlockDefinition.builder(properties);
        applyMenuCategory(builder, description);
        CompoundTag rawComponents = new CompoundTag(new LinkedHashMap<>());
        if (components != null) {
            for (Map.Entry<String, JsonElement> component : components.entrySet()) {
                applyComponent(packName, identifier, builder, rawComponents, component.getKey(), component.getValue());
            }
        }
        if (!baseTags.isEmpty()) {
            builder.blockTags(baseTags.toArray(new String[0]));
        }
        ListTag<CompoundTag> translatedPermutations = translatePermutations(packName, permutations);
        return builder.customBuild(nbt -> {
            CompoundTag target = nbt.getCompound("components");
            for (Map.Entry<String, Tag> entry : rawComponents.getTags().entrySet()) {
                target.put(entry.getKey(), entry.getValue());
            }
            if (translatedPermutations.size() > 0) {
                nbt.putList("permutations", translatedPermutations);
            }
        });
    }

    private void applyMenuCategory(CustomBlockDefinition.Builder builder, JsonObject description) {
        JsonObject menuCategory = description.getAsJsonObject("menu_category");
        if (menuCategory == null) {
            return;
        }
        String category = optString(menuCategory, "category");
        if (!category.isEmpty()) {
            builder.creativeCategory(category);
        }
        String group = optString(menuCategory, "group");
        if (!group.isEmpty()) {
            builder.creativeGroup(group);
        }
        if (asBoolean(menuCategory.get("is_hidden_in_commands"), false)) {
            builder.isHiddenInCommands(true);
        }
    }

    private void applyComponent(String packName, String identifier, CustomBlockDefinition.Builder builder,
                                CompoundTag raw, String key, JsonElement value) {
        if (key.startsWith("tag:")) {
            return;
        }
        switch (key) {
            case "minecraft:destructible_by_mining" -> {
                if (value.isJsonObject()) {
                    builder.destructibleByMining(asFloat(value.getAsJsonObject().get("seconds_to_destroy"), 0f));
                } else {
                    builder.destructibleByMining(asBoolean(value, true));
                }
            }
            case "minecraft:destructible_by_explosion" -> {
                if (value.isJsonObject()) {
                    builder.destructibleByExplosion((int) asFloat(value.getAsJsonObject().get("explosion_resistance"), 0f));
                } else {
                    builder.destructibleByExplosion(asBoolean(value, true));
                }
            }
            case "minecraft:friction" -> builder.friction(Math.min(asDouble(value, 0.4), 0.9));
            case "minecraft:light_dampening" -> builder.lightDampening(asInt(value, 15));
            case "minecraft:light_emission" -> builder.lightEmission(asInt(value, 0));
            case "minecraft:display_name" -> {
                String name = value.isJsonObject() ? optString(value.getAsJsonObject(), "value") : asStringOrEmpty(value);
                if (!name.isEmpty()) {
                    builder.name(name);
                }
            }
            case "minecraft:map_color" -> {
                String color = value.isJsonObject() ? optString(value.getAsJsonObject(), "color") : asStringOrEmpty(value);
                if (!color.isEmpty()) {
                    builder.mapColor(color);
                }
            }
            case "minecraft:geometry" -> {
                Geometry geometry = parseGeometry(value);
                if (geometry != null) {
                    builder.geometry(geometry);
                }
            }
            case "minecraft:collision_box" -> {
                if (isFalse(value)) {
                    raw.putCompound(key, disabledCollisionBox());
                } else {
                    builder.collisionBox(boxOrigin(value), boxSize(value));
                }
            }
            case "minecraft:selection_box" -> {
                if (isFalse(value)) {
                    raw.putCompound(key, selectionBox(false, new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f)));
                } else {
                    builder.selectionBox(boxOrigin(value), boxSize(value));
                }
            }
            case "minecraft:transformation" -> {
                Transformation transformation = parseTransformation(value);
                if (transformation != null) {
                    builder.transformation(transformation);
                }
            }
            case "minecraft:crafting_table" -> {
                if (value.isJsonObject()) {
                    builder.craftingTable(parseCraftingTable(identifier, value.getAsJsonObject()));
                }
            }
            case "minecraft:tick" -> {
                if (value.isJsonObject()) {
                    applyTick(builder, value.getAsJsonObject());
                }
            }
            case "minecraft:material_instances" -> {
                if (value.isJsonObject()) {
                    raw.putCompound(key, translateMaterialInstances(value.getAsJsonObject()));
                }
            }
            case "minecraft:unit_cube" -> raw.putCompound(key, new CompoundTag());
            default -> tracker.reportUnsupported(packName, "block-component", key);
        }
    }

    private static void applyTick(CustomBlockDefinition.Builder builder, JsonObject tick) {
        int min = 10;
        int max = 10;
        JsonElement range = tick.get("interval_range");
        if (range != null && range.isJsonArray() && range.getAsJsonArray().size() >= 2) {
            min = asInt(range.getAsJsonArray().get(0), 10);
            max = asInt(range.getAsJsonArray().get(1), min);
        }
        builder.blockTick(min, Math.max(min, max), asBoolean(tick.get("looping"), true));
    }

    private static CraftingTable parseCraftingTable(String identifier, JsonObject craftingTable) {
        List<String> craftingTags = asStringList(craftingTable.get("crafting_tags"));
        if (craftingTags.isEmpty()) {
            craftingTags.add(identifier);
        }
        String tableName = optString(craftingTable, "table_name");
        if (tableName.isEmpty()) {
            int colon = identifier.indexOf(':');
            tableName = colon >= 0 ? identifier.substring(colon + 1) : identifier;
        }
        return new CraftingTable(tableName, craftingTags);
    }

    private static Set<String> collectTags(@Nullable JsonObject components) {
        Set<String> tags = new LinkedHashSet<>();
        if (components != null) {
            for (String key : components.keySet()) {
                if (key.startsWith("tag:")) {
                    tags.add(key.substring("tag:".length()));
                }
            }
        }
        return tags;
    }

    private static List<BlockDataDriven.TagRule> collectTagRules(@Nullable JsonArray permutations) {
        List<BlockDataDriven.TagRule> rules = new ArrayList<>();
        if (permutations == null) {
            return rules;
        }
        for (JsonElement element : permutations) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject permutation = element.getAsJsonObject();
            Set<String> tags = collectTags(permutation.getAsJsonObject("components"));
            if (!tags.isEmpty()) {
                rules.add(new BlockDataDriven.TagRule(
                        MolangExpression.compile(optString(permutation, "condition")), List.copyOf(tags)));
            }
        }
        return rules;
    }

    /**
     * Translates permutations into the client schema. A permutation left with no translatable
     * component - one that only carries tags, say - is dropped rather than sent with an empty
     * components compound.
     */
    private ListTag<CompoundTag> translatePermutations(String packName, @Nullable JsonArray permutations) {
        ListTag<CompoundTag> list = new ListTag<>();
        if (permutations == null) {
            return list;
        }
        for (JsonElement element : permutations) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject permutation = element.getAsJsonObject();
            JsonObject components = permutation.getAsJsonObject("components");
            CompoundTag translated = new CompoundTag(new LinkedHashMap<>());
            if (components != null) {
                for (Map.Entry<String, JsonElement> entry : components.entrySet()) {
                    if (!entry.getKey().startsWith("tag:")) {
                        translatePermutationComponent(packName, translated, entry.getKey(), entry.getValue());
                    }
                }
            }
            if (translated.isEmpty()) {
                continue;
            }
            list.add(new CompoundTag()
                    .putString("condition", optString(permutation, "condition"))
                    .putCompound("components", translated));
        }
        return list;
    }

    private void translatePermutationComponent(String packName, CompoundTag target, String key, JsonElement value) {
        switch (key) {
            case "minecraft:material_instances" -> {
                if (value.isJsonObject()) {
                    target.putCompound(key, translateMaterialInstances(value.getAsJsonObject()));
                }
            }
            case "minecraft:geometry" -> {
                CompoundTag geometry = translatePermutationGeometry(value);
                if (geometry != null) {
                    target.putCompound(key, geometry);
                }
            }
            case "minecraft:friction" -> target.putCompound(key,
                    new CompoundTag().putFloat("value", Math.min(asFloat(value, 0.4f), 0.9f)));
            case "minecraft:light_dampening" -> target.putCompound(key,
                    new CompoundTag().putByte("lightLevel", (byte) asInt(value, 15)));
            case "minecraft:light_emission" -> target.putCompound(key,
                    new CompoundTag().putByte("emission", (byte) asInt(value, 0)));
            case "minecraft:destructible_by_mining" -> {
                float seconds = value.isJsonObject() ? asFloat(value.getAsJsonObject().get("seconds_to_destroy"), 0f) : 0f;
                target.putCompound(key, new CompoundTag().putFloat("value", seconds));
            }
            case "minecraft:collision_box" -> target.putCompound(key, isFalse(value)
                    ? disabledCollisionBox()
                    : collisionBox(boxOrigin(value), boxSize(value)));
            case "minecraft:selection_box" -> target.putCompound(key, isFalse(value)
                    ? selectionBox(false, new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f))
                    : selectionBox(true, boxOrigin(value), boxSize(value)));
            case "minecraft:transformation" -> {
                Transformation transformation = parseTransformation(value);
                if (transformation != null) {
                    target.putCompound(key, transformation.toCompoundTag());
                }
            }
            default -> tracker.reportUnsupported(packName, "block-permutation-component", key);
        }
    }

    private static CompoundTag collisionBox(Vector3f origin, Vector3f size) {
        return new CollisionBox(origin.x, origin.y, origin.z, size.x, size.y, size.z).toCompoundTag();
    }

    /**
     * A disabled collision box, sent as one zero-size box. An empty box list would serialise as an
     * NBT list of type 0, which the client fails to read.
     */
    private static CompoundTag disabledCollisionBox() {
        ListTag<CompoundTag> boxes = new ListTag<>();
        boxes.add(new CompoundTag()
                .putFloat("minX", 0f).putFloat("minY", 0f).putFloat("minZ", 0f)
                .putFloat("maxX", 0f).putFloat("maxY", 0f).putFloat("maxZ", 0f));
        return new CompoundTag()
                .putBoolean("enabled", false)
                .putList("boxes", boxes);
    }

    /**
     * The selection box schema, which differs from the collision box: origin and size as float lists
     * instead of a list of min/max boxes.
     */
    private static CompoundTag selectionBox(boolean enabled, Vector3f origin, Vector3f size) {
        return new CompoundTag()
                .putBoolean("enabled", enabled)
                .putList("origin", new ListTag<FloatTag>()
                        .add(new FloatTag(origin.x)).add(new FloatTag(origin.y)).add(new FloatTag(origin.z)))
                .putList("size", new ListTag<FloatTag>()
                        .add(new FloatTag(size.x)).add(new FloatTag(size.y)).add(new FloatTag(size.z)));
    }

    /**
     * Permutation geometry in the full client schema, starting from the same defaults the builder uses
     * for the base geometry. The sparse NBT that {@link Geometry} produces names its keys differently and is
     * not accepted inside a permutation.
     */
    private static @Nullable CompoundTag translatePermutationGeometry(JsonElement value) {
        String identifier;
        JsonObject geometryJson = null;
        if (value.isJsonPrimitive()) {
            identifier = value.getAsString();
        } else if (value.isJsonObject()) {
            geometryJson = value.getAsJsonObject();
            identifier = optString(geometryJson, "identifier");
        } else {
            return null;
        }
        if (identifier.isEmpty()) {
            return null;
        }
        CompoundTag geometry = CustomBlockDefinition.createDefaultGeometry(identifier);
        if (geometryJson != null) {
            JsonObject boneVisibility = geometryJson.getAsJsonObject("bone_visibility");
            if (boneVisibility != null) {
                CompoundTag bones = new CompoundTag(new LinkedHashMap<>());
                for (Map.Entry<String, JsonElement> bone : boneVisibility.entrySet()) {
                    JsonElement condition = bone.getValue();
                    if (condition.isJsonPrimitive() && condition.getAsJsonPrimitive().isBoolean()) {
                        bones.putString(bone.getKey(), condition.getAsBoolean() ? "true" : "false");
                    } else if (condition.isJsonPrimitive()) {
                        bones.putString(bone.getKey(), condition.getAsString());
                    }
                }
                geometry.putCompound("bone_visibility", bones);
            }
            String culling = optString(geometryJson, "culling");
            if (!culling.isEmpty()) {
                geometry.putString("culling", culling);
            }
        }
        return geometry;
    }

    private static @Nullable Geometry parseGeometry(JsonElement value) {
        if (value.isJsonPrimitive()) {
            return new Geometry(value.getAsString());
        }
        if (!value.isJsonObject()) {
            return null;
        }
        JsonObject geometryJson = value.getAsJsonObject();
        String name = optString(geometryJson, "identifier");
        if (name.isEmpty()) {
            return null;
        }
        Geometry geometry = new Geometry(name);
        JsonObject boneVisibility = geometryJson.getAsJsonObject("bone_visibility");
        if (boneVisibility != null) {
            for (Map.Entry<String, JsonElement> bone : boneVisibility.entrySet()) {
                JsonElement condition = bone.getValue();
                if (condition.isJsonPrimitive() && condition.getAsJsonPrimitive().isBoolean()) {
                    geometry.boneVisibility(bone.getKey(), condition.getAsBoolean());
                } else if (condition.isJsonPrimitive()) {
                    geometry.boneVisibility(bone.getKey(), condition.getAsString());
                }
            }
        }
        String culling = optString(geometryJson, "culling");
        if (!culling.isEmpty()) {
            geometry.culling(culling);
        }
        return geometry;
    }

    private static @Nullable Transformation parseTransformation(JsonElement value) {
        if (!value.isJsonObject()) {
            return null;
        }
        JsonObject transformation = value.getAsJsonObject();
        return new Transformation(
                asVector3(transformation.get("translation"), 0),
                asVector3(transformation.get("scale"), 1),
                asVector3(transformation.get("rotation"), 0));
    }

    /**
     * Flattens a pack's per-face material map into the client schema, which nests the materials and
     * carries an empty mapping compound alongside them.
     */
    private static CompoundTag translateMaterialInstances(JsonObject materialInstances) {
        CompoundTag materials = new CompoundTag(new LinkedHashMap<>());
        for (Map.Entry<String, JsonElement> entry : materialInstances.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject material = entry.getValue().getAsJsonObject();
            CompoundTag translated = new CompoundTag(new LinkedHashMap<>());
            translated.putFloat("ambient_occlusion", asBoolean(material.get("ambient_occlusion"), true) ? 1.0f : 0.0f);
            translated.putByte("packed_bools", (byte) (asBoolean(material.get("face_dimming"), true) ? 0x1 : 0x0));
            translated.putByte("isotropic", (byte) (asBoolean(material.get("isotropic"), false) ? 1 : 0));
            translated.putString("render_method", lowerOr(material, "render_method", "opaque"));
            translated.putString("texture", optString(material, "texture"));
            translated.putString("tint_method", lowerOr(material, "tint_method", "none"));
            materials.putCompound(entry.getKey(), translated);
        }
        return new CompoundTag(new LinkedHashMap<>())
                .putCompound("mappings", new CompoundTag())
                .putCompound("materials", materials);
    }

    private static Vector3f boxOrigin(JsonElement value) {
        return value.isJsonObject() ? asVector3f(value.getAsJsonObject().get("origin"), DEFAULT_BOX_ORIGIN) : DEFAULT_BOX_ORIGIN;
    }

    private static Vector3f boxSize(JsonElement value) {
        return value.isJsonObject() ? asVector3f(value.getAsJsonObject().get("size"), DEFAULT_BOX_SIZE) : DEFAULT_BOX_SIZE;
    }

    private static Vector3f asVector3f(@Nullable JsonElement element, Vector3f fallback) {
        if (element == null || !element.isJsonArray() || element.getAsJsonArray().size() < 3) {
            return fallback;
        }
        JsonArray array = element.getAsJsonArray();
        return new Vector3f(asFloat(array.get(0), 0f), asFloat(array.get(1), 0f), asFloat(array.get(2), 0f));
    }

    private static Vector3 asVector3(@Nullable JsonElement element, double fallback) {
        if (element == null || !element.isJsonArray() || element.getAsJsonArray().size() < 3) {
            return new Vector3(fallback, fallback, fallback);
        }
        JsonArray array = element.getAsJsonArray();
        return new Vector3(asFloat(array.get(0), (float) fallback), asFloat(array.get(1), (float) fallback),
                asFloat(array.get(2), (float) fallback));
    }

    private static String lowerOr(JsonObject json, String key, String fallback) {
        String value = optString(json, key);
        return value.isEmpty() ? fallback : value.toLowerCase(Locale.ENGLISH);
    }

    private static boolean isFalse(@Nullable JsonElement element) {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean() && !element.getAsBoolean();
    }

    private static String optString(JsonObject json, String key) {
        return asStringOrEmpty(json.get(key));
    }

    private static String asStringOrEmpty(@Nullable JsonElement element) {
        return element != null && element.isJsonPrimitive() ? element.getAsString() : "";
    }

    private static boolean asBoolean(@Nullable JsonElement element, boolean fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsBoolean() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static int asInt(@Nullable JsonElement element, int fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsInt() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static float asFloat(@Nullable JsonElement element, float fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsFloat() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static double asDouble(@Nullable JsonElement element, double fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsDouble() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static List<String> asStringList(@Nullable JsonElement element) {
        List<String> values = new ArrayList<>();
        if (element == null) {
            return values;
        }
        if (element.isJsonPrimitive()) {
            values.add(element.getAsString());
        } else if (element.isJsonArray()) {
            for (JsonElement entry : element.getAsJsonArray()) {
                if (entry.isJsonPrimitive()) {
                    values.add(entry.getAsString());
                }
            }
        }
        return values;
    }
}
