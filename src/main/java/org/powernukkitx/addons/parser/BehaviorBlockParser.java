package org.powernukkitx.addons.parser;

import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.block.BlockNoteblock;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.PrecipitationBehavior;
import org.powernukkitx.block.customblock.BlockDataDriven;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.block.customblock.PlacementFilter;
import org.powernukkitx.block.customblock.data.CollisionBox;
import org.powernukkitx.block.customblock.data.CraftingTable;
import org.powernukkitx.block.customblock.data.Movable;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.block.property.type.BooleanPropertyType;
import org.powernukkitx.block.property.type.IntListPropertyType;
import org.powernukkitx.block.property.type.IntPropertyType;
import org.powernukkitx.block.property.type.StringEnumPropertyType;
import org.powernukkitx.math.BlockFace;
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
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
 * Permutations apply on both sides: their components are sent to the client, and the gameplay
 * values, collision boxes, ticks, tags and loot tables they change are resolved per state on the
 * server when the block is parsed, so none of them is evaluated at runtime.
 * {@code minecraft:placement_filter} is enforced by the server only.
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

    /**
     * The Molang version the dedicated server writes into block definitions.
     */
    private static final int CLIENT_MOLANG_VERSION = 13;

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
                    description.getAsJsonObject("states"), description.getAsJsonObject("traits"),
                    precipitation(packName, components));
            if (properties == null) {
                return null;
            }
            List<Permutation> translated = translatePermutations(packName, permutations);
            CustomBlockDefinition definition = buildDefinition(packName, identifier, properties, description,
                    components, translated, baseTags);
            BlockDataDriven.Spec spec = BlockDataDriven.Spec.of(properties, definition, baseTags, collectTagRules(permutations),
                    lootTableOf(components), collectLootRules(permutations), permutationRules(translated),
                    behavior(packName, components, description.getAsJsonObject("traits")));
            return new ParsedBlock(identifier, spec);
        } catch (RuntimeException e) {
            log.error("Behavior pack '{}': could not parse block {}", packName, identifier, e);
            return null;
        }
    }

    private @Nullable BlockProperties buildProperties(String packName, String identifier, Set<String> baseTags,
                                                      @Nullable JsonObject states, @Nullable JsonObject traits,
                                                      @Nullable PrecipitationBehavior precipitation) {
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
        BlockPropertyType<?>[] typeArray = types.toArray(new BlockPropertyType<?>[0]);
        return precipitation == null
                ? new BlockProperties(identifier, baseTags, typeArray)
                : new BlockProperties(identifier, baseTags, precipitation, typeArray);
    }

    private @Nullable PrecipitationBehavior precipitation(String packName, @Nullable JsonObject components) {
        JsonElement element = components == null ? null : components.get("minecraft:precipitation_interactions");
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        String behavior = optString(element.getAsJsonObject(), "precipitation_behavior");
        return switch (behavior) {
            case "none" -> PrecipitationBehavior.NONE;
            case "obstruct_rain" -> PrecipitationBehavior.OBSTRUCT_RAIN;
            case "obstruct_rain_accumulate_snow", "" -> PrecipitationBehavior.OBSTRUCT_RAIN_ACCUMULATE_SNOW;
            case "snowlogging", "snow_log_no_collision" -> PrecipitationBehavior.SNOWLOGGING;
            default -> {
                tracker.reportUnsupported(packName, "block-precipitation-behavior", behavior);
                yield null;
            }
        };
    }

    /**
     * Reads the components that change what the block does on the server whatever its state.
     */
    private BlockDataDriven.Behavior behavior(String packName, @Nullable JsonObject components, @Nullable JsonObject traits) {
        if (components == null) {
            components = new JsonObject();
        }
        BlockDataDriven.SupportShape support = null;
        JsonElement supportElement = components.get("minecraft:support");
        if (supportElement != null && supportElement.isJsonObject()) {
            support = switch (optString(supportElement.getAsJsonObject(), "shape")) {
                case "fence" -> BlockDataDriven.SupportShape.FENCE;
                case "stair" -> BlockDataDriven.SupportShape.STAIR;
                default -> null;
            };
        }
        BlockDataDriven.ChestObstruction chestObstruction = BlockDataDriven.ChestObstruction.SHAPE;
        JsonElement obstruction = components.get("minecraft:chest_obstruction");
        if (obstruction != null && obstruction.isJsonObject()) {
            chestObstruction = switch (optString(obstruction.getAsJsonObject(), "obstruction_rule")) {
                case "always" -> BlockDataDriven.ChestObstruction.ALWAYS;
                case "never" -> BlockDataDriven.ChestObstruction.NEVER;
                default -> BlockDataDriven.ChestObstruction.SHAPE;
            };
        }
        Boolean conductor = null;
        boolean wireStepsDown = true;
        JsonElement conductivity = components.get("minecraft:redstone_conductivity");
        JsonElement consumer = components.get("minecraft:redstone_consumer");
        if (conductivity != null && conductivity.isJsonObject()) {
            conductor = asBoolean(conductivity.getAsJsonObject().get("redstone_conductor"), false);
            wireStepsDown = asBoolean(conductivity.getAsJsonObject().get("allows_wire_to_step_down"), true);
        } else if (consumer != null && consumer.isJsonObject()) {
            JsonObject json = consumer.getAsJsonObject();
            conductor = asBoolean(json.has("propagates_power") ? json.get("propagates_power") : json.get("propogates_power"), false);
        }
        return new BlockDataDriven.Behavior(
                instrumentSound(components.get("minecraft:instrument_sound")),
                parsePlacementFilter(packName, components.get("minecraft:placement_filter")),
                traitPlacement(traits),
                itemSpeeds(components.get("minecraft:destructible_by_mining")),
                support,
                chestObstruction,
                components.has("minecraft:flower_pottable"),
                conductor,
                connectionRule(components.get("minecraft:connection_rule")),
                redstoneProducer(components.get("minecraft:redstone_producer")),
                multiBlock(traits),
                traitStates(traits, "minecraft:connection").contains("minecraft:cardinal_connections"),
                cornerWith(packName, traits),
                liquidRule(components.get("minecraft:liquid_detection")),
                wireStepsDown);
    }

    /**
     * @return the first water rule's effects beyond what {@link #applyLiquidDetection} sets, or null when it
     * has none
     */
    private static @Nullable BlockDataDriven.LiquidRule liquidRule(@Nullable JsonElement value) {
        JsonElement rules = value != null && value.isJsonObject() ? value.getAsJsonObject().get("detection_rules") : null;
        if (rules == null || !rules.isJsonArray()) {
            return null;
        }
        for (JsonElement ruleElement : rules.getAsJsonArray()) {
            if (!ruleElement.isJsonObject()) {
                continue;
            }
            JsonObject rule = ruleElement.getAsJsonObject();
            String liquid = optString(rule, "liquid_type");
            if (!liquid.isEmpty() && !"water".equals(liquid)) {
                continue;
            }
            String touches = optString(rule, "on_liquid_touches");
            Set<BlockFace> stops = faces(rule.get("stops_liquid_flowing_from_direction"));
            boolean noReaction = "no_reaction".equals(touches);
            boolean broken = "broken".equals(touches);
            return stops.isEmpty() && !broken ? null : new BlockDataDriven.LiquidRule(stops, noReaction, broken);
        }
        return null;
    }

    private static List<String> traitStates(@Nullable JsonObject traits, String trait) {
        JsonObject definition = traits == null ? null : traits.getAsJsonObject(trait);
        return definition == null ? List.of() : asStringList(definition.get("enabled_states"));
    }

    private static @Nullable BlockDataDriven.MultiBlock multiBlock(@Nullable JsonObject traits) {
        JsonObject multiBlock = traits == null ? null : traits.getAsJsonObject("minecraft:multi_block");
        if (multiBlock == null || !asStringList(multiBlock.get("enabled_states")).contains("minecraft:multi_block_part")) {
            return null;
        }
        Set<BlockFace> direction = faces(multiBlock.get("direction"));
        return new BlockDataDriven.MultiBlock(multiBlockParts(multiBlock),
                direction.isEmpty() ? BlockFace.UP : direction.iterator().next());
    }

    private static int multiBlockParts(JsonObject multiBlock) {
        return Math.clamp(asInt(multiBlock.get("parts"), 2), 2, 4);
    }

    /**
     * @return the blocks {@code blocks_to_corner_with} names, empty to corner with the same block, or null when
     * the block does not form corners
     */
    private @Nullable List<PlacementFilter.BlockMatcher> cornerWith(String packName, @Nullable JsonObject traits) {
        if (!traitStates(traits, "minecraft:placement_direction").contains("minecraft:corner_and_cardinal_direction")) {
            return null;
        }
        List<PlacementFilter.BlockMatcher> matchers = new ArrayList<>();
        JsonElement cornerWith = traits.getAsJsonObject("minecraft:placement_direction").get("blocks_to_corner_with");
        if (cornerWith != null && cornerWith.isJsonArray()) {
            for (JsonElement entry : cornerWith.getAsJsonArray()) {
                PlacementFilter.BlockMatcher matcher = blockMatcher(entry);
                if (matcher != null) {
                    matchers.add(matcher);
                } else {
                    tracker.reportUnsupported(packName, "block-corner-filter", entry.toString());
                }
            }
        }
        return matchers;
    }

    private static final List<String> INSTRUMENT_SOUNDS = List.of("note.harp", "note.bd", "note.snare", "note.hat",
            "note.bassattack", "note.flute", "note.bell", "note.guitar", "note.chime", "note.xylophone", "note.iron_xylophone",
            "note.cow_bell", "note.didgeridoo", "note.bit", "note.banjo", "note.pling", "note.trumpet", "note.trumpet_exposed",
            "note.trumpet_weathered", "note.trumpet_oxidized", "note.zombie", "note.skeleton", "note.creeper",
            "note.enderdragon", "note.witherskeleton", "note.piglin");

    /**
     * The sounds are listed in the order of the note block's instruments, so a sound's position is its
     * instrument.
     */
    private static BlockDataDriven.@Nullable InstrumentSound instrumentSound(@Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonObject sound = element.getAsJsonObject();
        return new BlockDataDriven.InstrumentSound(
                instrument(optString(sound, "up"), BlockNoteblock.Instrument.HARP),
                instrument(optString(sound, "down"), null));
    }

    private static BlockNoteblock.@Nullable Instrument instrument(String sound, BlockNoteblock.@Nullable Instrument fallback) {
        if (sound.isEmpty()) {
            return fallback;
        }
        int index = INSTRUMENT_SOUNDS.indexOf(sound);
        return index < 0 ? null : BlockNoteblock.Instrument.values()[index];
    }

    private static List<BlockDataDriven.ItemSpeed> itemSpeeds(@Nullable JsonElement destructible) {
        List<BlockDataDriven.ItemSpeed> speeds = new ArrayList<>();
        JsonElement entries = destructible != null && destructible.isJsonObject()
                ? destructible.getAsJsonObject().get("item_specific_speeds") : null;
        if (entries == null || !entries.isJsonArray()) {
            return speeds;
        }
        for (JsonElement entry : entries.getAsJsonArray()) {
            if (!entry.isJsonObject()) {
                continue;
            }
            JsonObject speed = entry.getAsJsonObject();
            float seconds = asFloat(speed.get("destroy_speed"), 0f);
            JsonElement item = speed.get("item");
            if (item != null && item.isJsonPrimitive()) {
                speeds.add(BlockDataDriven.ItemSpeed.ofItem(namespaced(item.getAsString()), seconds));
            } else if (item != null && item.isJsonObject() && item.getAsJsonObject().has("tags")) {
                speeds.add(BlockDataDriven.ItemSpeed.ofTags(MolangExpression.compile(optString(item.getAsJsonObject(), "tags")), seconds));
            }
        }
        return speeds;
    }

    private static @Nullable BlockDataDriven.ConnectionRule connectionRule(@Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonObject rule = element.getAsJsonObject();
        BlockDataDriven.ConnectionSource accepts = switch (optString(rule, "accepts_connections_from")) {
            case "none" -> BlockDataDriven.ConnectionSource.NONE;
            case "only_fences" -> BlockDataDriven.ConnectionSource.ONLY_FENCES;
            default -> BlockDataDriven.ConnectionSource.ALL;
        };
        Set<BlockFace> directions = rule.has("enabled_directions")
                ? faces(rule.get("enabled_directions"))
                : EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
        return new BlockDataDriven.ConnectionRule(accepts, directions);
    }

    private static @Nullable BlockDataDriven.RedstoneProducer redstoneProducer(@Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonObject producer = element.getAsJsonObject();
        Set<BlockFace> connected = producer.has("connected_faces")
                ? faces(producer.get("connected_faces"))
                : EnumSet.allOf(BlockFace.class);
        Set<BlockFace> strong = faces(producer.get("strongly_powered_face"));
        return new BlockDataDriven.RedstoneProducer(Math.clamp(asInt(producer.get("power"), 0), 0, 15),
                strong.isEmpty() ? null : strong.iterator().next(), connected,
                asBoolean(producer.get("transform_relative"), false));
    }

    static Set<BlockFace> faces(@Nullable JsonElement element) {
        Set<BlockFace> faces = EnumSet.noneOf(BlockFace.class);
        for (String face : asStringList(element)) {
            for (BlockFace candidate : BlockFace.values()) {
                if (candidate.getName().equalsIgnoreCase(face)) {
                    faces.add(candidate);
                }
            }
        }
        return faces;
    }

    private static Movable movable(JsonElement value) {
        if (!value.isJsonObject()) {
            return Movable.DEFAULT;
        }
        JsonObject movable = value.getAsJsonObject();
        Movable.MovementType type = switch (optString(movable, "movement_type")) {
            case "push" -> Movable.MovementType.PUSH;
            case "popped" -> Movable.MovementType.POPPED;
            case "immovable" -> Movable.MovementType.IMMOVABLE;
            default -> Movable.MovementType.PUSH_PULL;
        };
        Movable.StickyType sticky = "same".equals(optString(movable, "sticky")) ? Movable.StickyType.SAME : Movable.StickyType.NONE;
        return new Movable(type, sticky);
    }

    /**
     * Water is the only liquid Bedrock lets blocks react to. A block that holds water is waterlogged; one
     * the water flows through without breaking it shares its space with the water, as waterlogged blocks do.
     * The flow directions and whether {@code broken} drops loot are read by {@link #liquidRule(JsonElement)}.
     */
    private static void applyLiquidDetection(CustomBlockDefinition.Builder builder, JsonElement value) {
        JsonElement rules = value.isJsonObject() ? value.getAsJsonObject().get("detection_rules") : null;
        if (rules == null || !rules.isJsonArray()) {
            return;
        }
        for (JsonElement ruleElement : rules.getAsJsonArray()) {
            if (!ruleElement.isJsonObject()) {
                continue;
            }
            JsonObject rule = ruleElement.getAsJsonObject();
            String liquid = optString(rule, "liquid_type");
            if (!liquid.isEmpty() && !"water".equals(liquid)) {
                continue;
            }
            String touches = optString(rule, "on_liquid_touches");
            boolean containsLiquid = asBoolean(rule.get("can_contain_liquid"), false);
            builder.canBeFlowedInto("broken".equals(touches) || "popped".equals(touches));
            builder.waterloggingLevel("no_reaction".equals(touches) ? 2 : containsLiquid ? 1 : 0);
            return;
        }
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
            if (traitName.equals("minecraft:multi_block")) {
                if (trait.getValue().isJsonObject()
                        && asStringList(trait.getValue().getAsJsonObject().get("enabled_states")).contains("minecraft:multi_block_part")) {
                    int parts = multiBlockParts(trait.getValue().getAsJsonObject());
                    types.add(IntPropertyType.of("minecraft:multi_block_part", 0, parts - 1, 0));
                }
                continue;
            }
            if (!traitName.equals("minecraft:placement_direction") && !traitName.equals("minecraft:placement_position")
                    && !traitName.equals("minecraft:connection")) {
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
                    case "minecraft:corner_and_cardinal_direction" -> {
                        types.add(StringEnumPropertyType.of(
                                "minecraft:cardinal_direction", List.of("south", "west", "north", "east"), "south"));
                        types.add(StringEnumPropertyType.of("minecraft:corner",
                                List.of("none", "inner_left", "inner_right", "outer_left", "outer_right"), "none"));
                    }
                    case "minecraft:cardinal_connections" -> {
                        for (String direction : List.of("north", "south", "east", "west")) {
                            types.add(BooleanPropertyType.of("minecraft:connection_" + direction, false));
                        }
                    }
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

    /**
     * Reads which trait states the server sets on placement. The states themselves are declared by
     * {@link #buildTraitStates(String, JsonObject)}, which also reports what is not supported.
     */
    private static BlockDataDriven.TraitPlacement traitPlacement(@Nullable JsonObject traits) {
        if (traits == null) {
            return BlockDataDriven.TraitPlacement.NONE;
        }
        JsonObject direction = traits.getAsJsonObject("minecraft:placement_direction");
        JsonObject position = traits.getAsJsonObject("minecraft:placement_position");
        List<String> directionStates = direction == null ? List.of() : asStringList(direction.get("enabled_states"));
        List<String> positionStates = position == null ? List.of() : asStringList(position.get("enabled_states"));
        return new BlockDataDriven.TraitPlacement(
                directionStates.contains("minecraft:cardinal_direction")
                        || directionStates.contains("minecraft:corner_and_cardinal_direction"),
                directionStates.contains("minecraft:facing_direction"),
                direction == null ? 0 : (int) asFloat(direction.get("y_rotation_offset"), 0f),
                positionStates.contains("minecraft:block_face"),
                positionStates.contains("minecraft:vertical_half"));
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
                List<Integer> listed = new ArrayList<>();
                for (JsonElement value : values) {
                    listed.add(value.getAsInt());
                }
                return IntListPropertyType.of(name, listed, first.getAsInt());
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
                                                  List<Permutation> permutations, Set<String> baseTags) {
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
        ListTag<CompoundTag> translatedPermutations = clientPermutations(permutations);
        Set<String> traitStates = new LinkedHashSet<>();
        ListTag<CompoundTag> traits = ClientBlockComponents.traits(description.getAsJsonObject("traits"), traitStates);
        return builder.customBuild(nbt -> {
            CompoundTag target = nbt.getCompound("components");
            for (Map.Entry<String, Tag> entry : rawComponents.getTags().entrySet()) {
                target.put(entry.getKey(), entry.getValue());
            }
            if (translatedPermutations.size() > 0) {
                nbt.putList("permutations", translatedPermutations);
            }
            nbt.putInt("molangVersion", CLIENT_MOLANG_VERSION);
            if (traits.size() > 0) {
                nbt.putList("traits", traits);
                removeProperties(nbt, traitStates);
            }
        });
    }

    /**
     * Drops the states a trait declares from the property list, since the client adds them itself.
     */
    private static void removeProperties(CompoundTag nbt, Set<String> names) {
        if (!nbt.containsList("properties")) {
            return;
        }
        ListTag<CompoundTag> kept = new ListTag<>();
        for (CompoundTag property : nbt.getList("properties", CompoundTag.class).getAll()) {
            if (!names.contains(property.getString("name"))) {
                kept.add(property);
            }
        }
        if (kept.size() > 0) {
            nbt.putList("properties", kept);
        } else {
            nbt.remove("properties");
        }
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
            builder.creativeGroup(CreativeGroupNames.normalize(group));
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
            case "minecraft:destructible_by_mining" -> builder.destructibleByMining(miningSeconds(value));
            case "minecraft:destructible_by_explosion" -> builder.destructibleByExplosion(explosionResistance(value));
            case "minecraft:friction" -> builder.friction(Math.min(asDouble(value, 0.4), 0.9));
            case "minecraft:light_dampening", "minecraft:block_light_absorption" -> builder.lightDampening(asInt(value, 15));
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
            case "minecraft:geometry" -> putIfPresent(raw, key, ClientBlockComponents.geometry(value));
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
            case "minecraft:transformation" -> putIfPresent(raw, key, ClientBlockComponents.transformation(value));
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
                    raw.putCompound(key, ClientBlockComponents.materialInstances(value.getAsJsonObject()));
                }
            }
            case "minecraft:unit_cube" -> raw.putCompound(key, new CompoundTag());
            case "minecraft:flammable" -> applyFlammable(builder, value);
            case "minecraft:movable" -> builder.movable(movable(value));
            case "minecraft:replaceable" -> {
                builder.canBeReplaced(true);
                raw.putCompound(key, new CompoundTag());
            }
            case "minecraft:liquid_detection" -> {
                applyLiquidDetection(builder, value);
                putIfPresent(raw, key, ClientBlockComponents.liquidDetection(value));
            }
            case "minecraft:placement_filter" -> putIfPresent(raw, key, ClientBlockComponents.placementFilter(value));
            case "minecraft:precipitation_interactions" ->
                    putIfPresent(raw, key, ClientBlockComponents.precipitationInteractions(value));
            case "minecraft:redstone_conductivity" -> raw.putCompound(key, ClientBlockComponents.redstoneConductivity(value));
            case "minecraft:redstone_producer" -> putIfPresent(raw, key, ClientBlockComponents.redstoneProducer(value));
            case "minecraft:support" -> putIfPresent(raw, key, ClientBlockComponents.support(value));
            case "minecraft:flower_pottable" -> raw.putCompound(key, ClientBlockComponents.flowerPottable());
            case "minecraft:connection_rule" -> raw.putCompound(key, ClientBlockComponents.connectionRule(value));
            case "minecraft:destruction_particles" -> raw.putCompound(key, ClientBlockComponents.destructionParticles(value));
            case "minecraft:random_offset" -> raw.putCompound(key, ClientBlockComponents.randomOffset(value));
            case "minecraft:embedded_visual" -> putIfPresent(raw, key, ClientBlockComponents.embeddedVisual(value));
            case "minecraft:item_visual" -> putIfPresent(raw, key, ClientBlockComponents.itemVisual(value));
            case "minecraft:block_light_emission" -> builder.lightEmission(Math.round(Math.clamp(asFloat(value, 0f), 0f, 1f) * 15));
            case "minecraft:destroy_time" -> builder.destructibleByMining(asFloat(value, 0f));
            case "minecraft:explosion_resistance" -> builder.destructibleByExplosion((int) asFloat(value, 0f));
            case "minecraft:loot", "minecraft:chest_obstruction", "minecraft:instrument_sound", "minecraft:redstone_consumer" -> {
            }
            default -> tracker.reportUnsupported(packName, "block-component", key);
        }
    }

    /**
     * Fire spread reads these on the server; Bedrock's defaults for {@code true} are those of planks.
     */
    private static void applyFlammable(CustomBlockDefinition.Builder builder, JsonElement value) {
        int catchChance = 0;
        int destroyChance = 0;
        if (value.isJsonObject()) {
            catchChance = asInt(value.getAsJsonObject().get("catch_chance_modifier"), 5);
            destroyChance = asInt(value.getAsJsonObject().get("destroy_chance_modifier"), 20);
        } else if (asBoolean(value, false)) {
            catchChance = 5;
            destroyChance = 20;
        }
        builder.burnChance(catchChance).burnAbility(destroyChance);
    }

    private @Nullable PlacementFilter parsePlacementFilter(String packName, @Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonElement conditionsElement = element.getAsJsonObject().get("conditions");
        if (conditionsElement == null || !conditionsElement.isJsonArray()) {
            return null;
        }
        List<PlacementFilter.Condition> conditions = new ArrayList<>();
        for (JsonElement conditionElement : conditionsElement.getAsJsonArray()) {
            if (!conditionElement.isJsonObject()) {
                continue;
            }
            JsonObject condition = conditionElement.getAsJsonObject();
            Set<BlockFace> faces = EnumSet.noneOf(BlockFace.class);
            JsonElement allowedFaces = condition.get("allowed_faces");
            if (allowedFaces == null) {
                faces.addAll(List.of(BlockFace.values()));
            }
            for (String face : asStringList(allowedFaces)) {
                switch (face.toLowerCase(Locale.ENGLISH)) {
                    case "all" -> faces.addAll(List.of(BlockFace.values()));
                    case "side" -> faces.addAll(List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST));
                    case "up" -> faces.add(BlockFace.UP);
                    case "down" -> faces.add(BlockFace.DOWN);
                    case "north" -> faces.add(BlockFace.NORTH);
                    case "south" -> faces.add(BlockFace.SOUTH);
                    case "east" -> faces.add(BlockFace.EAST);
                    case "west" -> faces.add(BlockFace.WEST);
                    default -> tracker.reportUnsupported(packName, "block-placement-face", face);
                }
            }
            List<PlacementFilter.BlockMatcher> matchers = new ArrayList<>();
            JsonElement blockFilter = condition.get("block_filter");
            if (blockFilter != null && blockFilter.isJsonArray()) {
                for (JsonElement entry : blockFilter.getAsJsonArray()) {
                    PlacementFilter.BlockMatcher matcher = blockMatcher(entry);
                    if (matcher != null) {
                        matchers.add(matcher);
                    } else {
                        tracker.reportUnsupported(packName, "block-placement-filter", entry.toString());
                    }
                }
            }
            conditions.add(new PlacementFilter.Condition(faces, matchers));
        }
        return conditions.isEmpty() ? null : new PlacementFilter(conditions);
    }

    private static @Nullable PlacementFilter.BlockMatcher blockMatcher(JsonElement entry) {
        if (entry.isJsonPrimitive()) {
            return new PlacementFilter.IdMatcher(namespaced(entry.getAsString()), Map.of());
        }
        if (!entry.isJsonObject()) {
            return null;
        }
        JsonObject json = entry.getAsJsonObject();
        if (json.has("tags")) {
            return new PlacementFilter.TagMatcher(MolangExpression.compile(optString(json, "tags")));
        }
        String name = optString(json, "name");
        if (name.isEmpty()) {
            return null;
        }
        Map<String, String> states = new LinkedHashMap<>();
        JsonObject statesJson = json.getAsJsonObject("states");
        if (statesJson != null) {
            for (Map.Entry<String, JsonElement> state : statesJson.entrySet()) {
                states.put(state.getKey(), state.getValue().getAsString());
            }
        }
        return new PlacementFilter.IdMatcher(namespaced(name), states);
    }

    private static String namespaced(String identifier) {
        return identifier.indexOf(':') >= 0 ? identifier : "minecraft:" + identifier;
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
     * @return the table a {@code minecraft:loot} component names, in either its object or its
     * string form, or null when the components declare none
     */
    private static @Nullable String lootTableOf(@Nullable JsonObject components) {
        if (components == null) {
            return null;
        }
        JsonElement loot = components.get("minecraft:loot");
        if (loot == null) {
            return null;
        }
        if (loot.isJsonObject()) {
            return asStringOrNull(loot.getAsJsonObject().get("table"));
        }
        return asStringOrNull(loot);
    }

    private static List<BlockDataDriven.LootRule> collectLootRules(@Nullable JsonArray permutations) {
        List<BlockDataDriven.LootRule> rules = new ArrayList<>();
        if (permutations == null) {
            return rules;
        }
        for (JsonElement element : permutations) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject permutation = element.getAsJsonObject();
            String table = lootTableOf(permutation.getAsJsonObject("components"));
            if (table != null) {
                rules.add(new BlockDataDriven.LootRule(MolangExpression.compile(optString(permutation, "condition")), table));
            }
        }
        return rules;
    }

    /**
     * One permutation, translated once for both sides: its components in the client schema and the
     * gameplay values the server applies while its condition holds.
     */
    private record Permutation(String condition, CompoundTag components,
                               List<Consumer<CustomBlockDefinition.Builder>> serverValues,
                               BlockDataDriven.@Nullable RedstoneProducer redstoneProducer) {
    }

    private List<Permutation> translatePermutations(String packName, @Nullable JsonArray permutations) {
        List<Permutation> translated = new ArrayList<>();
        if (permutations == null) {
            return translated;
        }
        for (JsonElement element : permutations) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject permutation = element.getAsJsonObject();
            JsonObject components = permutation.getAsJsonObject("components");
            CompoundTag clientComponents = new CompoundTag(new LinkedHashMap<>());
            List<Consumer<CustomBlockDefinition.Builder>> serverValues = new ArrayList<>();
            BlockDataDriven.RedstoneProducer producer = null;
            if (components != null) {
                for (Map.Entry<String, JsonElement> entry : components.entrySet()) {
                    if (entry.getKey().equals("minecraft:redstone_producer")) {
                        producer = redstoneProducer(entry.getValue());
                        putIfPresent(clientComponents, entry.getKey(), ClientBlockComponents.redstoneProducer(entry.getValue()));
                    } else if (!entry.getKey().startsWith("tag:")) {
                        translatePermutationComponent(packName, clientComponents, serverValues, entry.getKey(), entry.getValue());
                    }
                }
            }
            translated.add(new Permutation(optString(permutation, "condition"), clientComponents, serverValues, producer));
        }
        return translated;
    }

    /**
     * The permutations in the client schema. A permutation left with no client component - one that
     * only carries tags or a tick, say - is dropped rather than sent with an empty components compound.
     */
    private static ListTag<CompoundTag> clientPermutations(List<Permutation> permutations) {
        ListTag<CompoundTag> list = new ListTag<>();
        for (Permutation permutation : permutations) {
            if (!permutation.components().isEmpty()) {
                list.add(new CompoundTag()
                        .putString("condition", permutation.condition())
                        .putCompound("components", permutation.components()));
            }
        }
        return list;
    }

    private static List<BlockDataDriven.PermutationRule> permutationRules(List<Permutation> permutations) {
        List<BlockDataDriven.PermutationRule> rules = new ArrayList<>();
        for (Permutation permutation : permutations) {
            if (!permutation.components().isEmpty() || !permutation.serverValues().isEmpty()
                    || permutation.redstoneProducer() != null) {
                rules.add(new BlockDataDriven.PermutationRule(MolangExpression.compile(permutation.condition()),
                        permutation.components(), permutation.serverValues(), permutation.redstoneProducer()));
            }
        }
        return rules;
    }

    private void translatePermutationComponent(String packName, CompoundTag target,
                                               List<Consumer<CustomBlockDefinition.Builder>> serverValues,
                                               String key, JsonElement value) {
        switch (key) {
            case "minecraft:material_instances" -> {
                if (value.isJsonObject()) {
                    target.putCompound(key, ClientBlockComponents.materialInstances(value.getAsJsonObject()));
                }
            }
            case "minecraft:geometry" -> putIfPresent(target, key, ClientBlockComponents.geometry(value));
            case "minecraft:friction" -> {
                double friction = Math.min(asDouble(value, 0.4), 0.9);
                target.putCompound(key, new CompoundTag().putFloat("value", (float) friction));
                serverValues.add(builder -> builder.friction(friction));
            }
            case "minecraft:light_dampening" -> {
                int dampening = asInt(value, 15);
                target.putCompound(key, new CompoundTag().putByte("lightLevel", (byte) dampening));
                serverValues.add(builder -> builder.lightDampening(dampening));
            }
            case "minecraft:light_emission" -> {
                int emission = asInt(value, 0);
                target.putCompound(key, new CompoundTag().putByte("emission", (byte) emission));
                serverValues.add(builder -> builder.lightEmission(emission));
            }
            case "minecraft:destructible_by_mining" -> {
                float seconds = miningSeconds(value);
                target.putCompound(key, new CompoundTag().putFloat("value", seconds));
                serverValues.add(builder -> builder.destructibleByMining(seconds));
            }
            case "minecraft:destructible_by_explosion" -> {
                int resistance = explosionResistance(value);
                target.putCompound(key, new CompoundTag().putInt("explosion_resistance", resistance));
                serverValues.add(builder -> builder.destructibleByExplosion(resistance));
            }
            case "minecraft:collision_box" -> target.putCompound(key, isFalse(value)
                    ? disabledCollisionBox()
                    : collisionBox(boxOrigin(value), boxSize(value)));
            case "minecraft:selection_box" -> target.putCompound(key, isFalse(value)
                    ? selectionBox(false, new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f))
                    : selectionBox(true, boxOrigin(value), boxSize(value)));
            case "minecraft:transformation" -> putIfPresent(target, key, ClientBlockComponents.transformation(value));
            case "minecraft:tick" -> {
                if (value.isJsonObject()) {
                    JsonObject tick = value.getAsJsonObject();
                    serverValues.add(builder -> applyTick(builder, tick));
                }
            }
            case "minecraft:flammable" -> serverValues.add(builder -> applyFlammable(builder, value));
            case "minecraft:movable" -> {
                Movable movable = movable(value);
                serverValues.add(builder -> builder.movable(movable));
            }
            case "minecraft:replaceable" -> {
                target.putCompound(key, new CompoundTag());
                serverValues.add(builder -> builder.canBeReplaced(true));
            }
            case "minecraft:liquid_detection" -> {
                putIfPresent(target, key, ClientBlockComponents.liquidDetection(value));
                serverValues.add(builder -> applyLiquidDetection(builder, value));
            }
            case "minecraft:destruction_particles" -> target.putCompound(key, ClientBlockComponents.destructionParticles(value));
            case "minecraft:random_offset" -> target.putCompound(key, ClientBlockComponents.randomOffset(value));
            case "minecraft:loot" -> {
            }
            default -> tracker.reportUnsupported(packName, "block-permutation-component", key);
        }
    }

    /**
     * @return the mining time a {@code minecraft:destructible_by_mining} value gives, or {@code -1}
     * when it makes the block unbreakable
     */
    private static float miningSeconds(JsonElement value) {
        if (value.isJsonObject()) {
            return asFloat(value.getAsJsonObject().get("seconds_to_destroy"), 0f);
        }
        return asBoolean(value, true) ? 0f : -1f;
    }

    /**
     * @return the resistance a {@code minecraft:destructible_by_explosion} value gives, or {@code -1}
     * when it makes the block immune to explosions
     */
    private static int explosionResistance(JsonElement value) {
        if (value.isJsonObject()) {
            return (int) asFloat(value.getAsJsonObject().get("explosion_resistance"), 0f);
        }
        return asBoolean(value, true) ? 0 : -1;
    }

    private static CompoundTag collisionBox(Vector3f origin, Vector3f size) {
        return new CollisionBox(origin.x, origin.y, origin.z, size.x, size.y, size.z).toCompoundTag();
    }

    /**
     * A disabled collision box, sent as the dedicated server sends it: disabled, with an empty box list.
     */
    private static CompoundTag disabledCollisionBox() {
        return new CompoundTag()
                .putList("boxes", new ListTag<CompoundTag>())
                .putBoolean("enabled", false);
    }

    private static void putIfPresent(CompoundTag target, String key, @Nullable CompoundTag value) {
        if (value != null) {
            target.putCompound(key, value);
        }
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

    static Vector3 asVector3(@Nullable JsonElement element, double fallback) {
        if (element == null || !element.isJsonArray() || element.getAsJsonArray().size() < 3) {
            return new Vector3(fallback, fallback, fallback);
        }
        JsonArray array = element.getAsJsonArray();
        return new Vector3(asFloat(array.get(0), (float) fallback), asFloat(array.get(1), (float) fallback),
                asFloat(array.get(2), (float) fallback));
    }

    static String lowerOr(JsonObject json, String key, String fallback) {
        String value = optString(json, key);
        return value.isEmpty() ? fallback : value.toLowerCase(Locale.ENGLISH);
    }

    private static boolean isFalse(@Nullable JsonElement element) {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean() && !element.getAsBoolean();
    }

    static String optString(JsonObject json, String key) {
        return asStringOrEmpty(json.get(key));
    }

    private static String asStringOrEmpty(@Nullable JsonElement element) {
        return element != null && element.isJsonPrimitive() ? element.getAsString() : "";
    }

    private static @Nullable String asStringOrNull(@Nullable JsonElement element) {
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    static boolean asBoolean(@Nullable JsonElement element, boolean fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsBoolean() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    static int asInt(@Nullable JsonElement element, int fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsInt() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    static float asFloat(@Nullable JsonElement element, float fallback) {
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

    static List<String> asStringList(@Nullable JsonElement element) {
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
