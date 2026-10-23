package org.powernukkitx.addons.parser;

import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.block.BlockComposter;
import org.powernukkitx.item.customitem.CustomItemDefinition;
import org.powernukkitx.item.customitem.ItemDataDriven;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.item.utils.DiggerEntry;
import org.powernukkitx.registry.BlockRegistry;
import org.powernukkitx.registry.ItemRegistry;
import org.powernukkitx.registry.Registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Turns a behaviour pack's {@code items/*.json} into registered items.
 * <p>
 * Components are mapped onto {@link CustomItemDefinition.SimpleBuilder}, which produces
 * both the NBT the client is sent and the definition the {@link org.powernukkitx.item.Item}
 * core reads at runtime, so a parsed item behaves the same on both sides. A component
 * without a mapping is recorded on the {@link AddonFeatureTracker} rather than failing
 * the item: packs routinely carry components that only matter to the client.
 */
@Slf4j
public final class BehaviorItemParser {

    private final AddonFeatureTracker tracker;

    public BehaviorItemParser(AddonFeatureTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Parses one item document and registers the item it describes.
     * <p>
     * An item is skipped, with the reason recorded on the tracker, when it overrides a
     * vanilla identifier, when the identifier is already taken by a custom block, or when
     * another pack already registered it.
     *
     * @param packName the pack the document came from, for logging
     * @param fileName the entry name inside the pack, for logging
     * @param root     the parsed document
     * @return the registered identifier, or null when the item was skipped
     */
    public @Nullable String parseAndRegister(@NotNull String packName, @NotNull String fileName, @NotNull JsonObject root) {
        JsonObject itemJson = root.getAsJsonObject("minecraft:item");
        if (itemJson == null) {
            log.warn("Behavior pack '{}': {} has no minecraft:item element, skipped", packName, fileName);
            return null;
        }
        JsonObject description = itemJson.getAsJsonObject("description");
        if (description == null || !description.has("identifier")) {
            log.warn("Behavior pack '{}': {} has no item identifier, skipped", packName, fileName);
            return null;
        }
        String identifier = description.get("identifier").getAsString();

        if (identifier.startsWith("minecraft:")) {
            tracker.reportUnsupported(packName, "vanilla-item-override", identifier);
            return null;
        }
        JsonObject components = itemJson.getAsJsonObject("components");
        boolean replacesBlockItem = BlockRegistry.getCustomBlockDefinitionByIdStatic(identifier) != null;
        if (replacesBlockItem && !declaresBlockItemReplacement(identifier, components)) {
            tracker.reportUnsupported(packName, "item-id-taken-by-block", identifier);
            return null;
        }
        if (ItemRegistry.getCustomItemDefinitionByIdStatic(identifier) != null) {
            tracker.reportUnsupported(packName, "duplicate-item-definition", identifier);
            return null;
        }

        try {
            ItemDataDriven prototype = buildItem(packName, identifier, description, components);
            CustomItemDefinition definition = prototype.getDefinition();
            if (fireResistant(components)) {
                definition = new CustomItemDefinition(identifier, definition.nbt(),
                        definition.toBuilder().lavaResistant(true).build());
            }
            ItemDataDriven.ServerComponents serverComponents = serverComponents(components);
            prototype = new ItemDataDriven(identifier, definition, serverComponents);
            CustomItemDefinition registered = definition;
            if (replacesBlockItem) {
                Registries.ITEM.registerBlockItemReplacement(prototype, () -> new ItemDataDriven(identifier, registered, serverComponents));
            } else {
                Registries.ITEM.registerCustomItemDefinition(prototype, () -> new ItemDataDriven(identifier, registered, serverComponents));
            }
            registerCompostable(identifier, components);
            log.debug("Registered data-driven item {} from behavior pack '{}'", identifier, packName);
            return identifier;
        } catch (Exception e) {
            log.error("Behavior pack '{}': could not register item {}", packName, identifier, e);
            return null;
        }
    }

    /**
     * An item document may share its identifier with a block only to replace the block's own item, which
     * Bedrock expresses as a {@code minecraft:block_placer} for that block with {@code replace_block_item}.
     */
    private static boolean declaresBlockItemReplacement(String identifier, @Nullable JsonObject components) {
        JsonObject blockPlacer = components == null ? null : components.getAsJsonObject("minecraft:block_placer");
        return blockPlacer != null && identifier.equals(optString(blockPlacer, "block"))
                && optBoolean(blockPlacer, "replace_block_item", false);
    }

    /**
     * Whether a dropped stack survives fire and lava. Only the server acts on it, so the component is not
     * sent to clients.
     */
    private static boolean fireResistant(@Nullable JsonObject components) {
        JsonElement fireResistant = components == null ? null : components.get("minecraft:fire_resistant");
        if (fireResistant == null) {
            return false;
        }
        return fireResistant.isJsonObject() ? optBoolean(fireResistant.getAsJsonObject(), "value", true) : asBoolean(fireResistant, true);
    }

    private static ItemDataDriven.ServerComponents serverComponents(@Nullable JsonObject components) {
        if (components == null) {
            return ItemDataDriven.ServerComponents.NONE;
        }
        ItemDataDriven.Record record = null;
        JsonElement recordElement = components.get("minecraft:record");
        if (recordElement != null && recordElement.isJsonObject()) {
            JsonObject json = recordElement.getAsJsonObject();
            String sound = optString(json, "sound_event");
            if (!sound.isEmpty()) {
                record = new ItemDataDriven.Record(sound, asFloat(json.get("duration"), 0f),
                        Math.clamp(asInt(json.get("comparator_signal"), 1), 1, 13));
            }
        }
        List<String> causes = List.of();
        JsonElement absorption = components.get("minecraft:damage_absorption");
        if (absorption != null && absorption.isJsonObject()) {
            causes = asStringList(absorption.getAsJsonObject().get("absorbable_causes"));
        }
        return record == null && causes.isEmpty() ? ItemDataDriven.ServerComponents.NONE
                : new ItemDataDriven.ServerComponents(record, Set.copyOf(causes));
    }

    private static void registerCompostable(String identifier, @Nullable JsonObject components) {
        JsonElement compostable = components == null ? null : components.get("minecraft:compostable");
        if (compostable != null && compostable.isJsonObject()) {
            int chance = asInt(compostable.getAsJsonObject().get("composting_chance"), 0);
            if (chance > 0) {
                BlockComposter.registerItem(Math.min(chance, 100), identifier);
            }
        }
    }

    private ItemDataDriven buildItem(String packName, String identifier, JsonObject description, JsonObject components) {
        CustomItemDefinition.SimpleBuilder builder =
                CustomItemDefinition.simpleBuilder(new ItemDataDriven(identifier, null));

        applyMenuCategory(builder, description);
        if (components != null) {
            for (Map.Entry<String, JsonElement> component : components.entrySet()) {
                applyComponent(packName, builder, component.getKey(), component.getValue());
            }
        }
        return new ItemDataDriven(identifier, builder.build());
    }

    private void applyMenuCategory(CustomItemDefinition.SimpleBuilder builder, JsonObject description) {
        JsonObject menuCategory = description.getAsJsonObject("menu_category");
        if (menuCategory == null) {
            return;
        }
        String category = optString(menuCategory, "category");
        if (!category.isEmpty()) {
            try {
                builder.creativeCategory(CreativeCategory.valueOf(category.toUpperCase(Locale.ENGLISH)));
            } catch (IllegalArgumentException e) {
                builder.creativeCategory(CreativeCategory.ITEMS);
            }
        }
        String group = optString(menuCategory, "group");
        if (!group.isEmpty()) {
            builder.creativeGroup(CreativeGroupNames.normalize(group));
        }
        if (optBoolean(menuCategory, "is_hidden_in_commands", false)) {
            builder.isHiddenInCommands(true);
        }
    }

    private void applyComponent(String packName, CustomItemDefinition.SimpleBuilder builder, String key, JsonElement value) {
        switch (key) {
            case "minecraft:icon" -> {
                String texture = extractIconTexture(value);
                if (!texture.isEmpty()) {
                    builder.texture(texture);
                }
            }
            case "minecraft:display_name" -> {
                String name = value.isJsonObject() ? optString(value.getAsJsonObject(), "value") : asStringOrEmpty(value);
                if (!name.isEmpty()) {
                    builder.name(name);
                }
            }
            case "minecraft:max_stack_size" -> builder.maxStackSize(asInt(unwrapValue(value), 64));
            case "minecraft:hand_equipped" -> builder.handEquipped(asBoolean(unwrapValue(value), false));
            case "minecraft:glint", "minecraft:foil" -> builder.glint(asBoolean(unwrapValue(value), false));
            case "minecraft:allow_off_hand" -> builder.allowOffHand(asBoolean(unwrapValue(value), false));
            case "minecraft:can_destroy_in_creative" -> builder.canDestroyInCreative(asBoolean(unwrapValue(value), true));
            case "minecraft:stacked_by_data" -> builder.stackedByData(asBoolean(unwrapValue(value), false));
            case "minecraft:should_despawn" -> builder.shouldDespawn(asBoolean(unwrapValue(value), true));
            case "minecraft:damage" -> builder.damage(asInt(unwrapValue(value), 0));
            case "minecraft:fuel" -> {
                if (value.isJsonObject()) {
                    builder.fuel(asFloat(value.getAsJsonObject().get("duration"), 0f));
                }
            }
            case "minecraft:durability" -> applyDurability(builder, value);
            case "minecraft:food" -> applyFood(builder, value);
            case "minecraft:use_animation" -> builder.useAnimation(asStringOrEmpty(unwrapValue(value)));
            case "minecraft:use_modifiers" -> {
                if (value.isJsonObject()) {
                    JsonObject modifiers = value.getAsJsonObject();
                    builder.useModifiers(
                            asFloat(modifiers.get("movement_modifier"), 1.0f),
                            asFloat(modifiers.get("use_duration"), 0f));
                }
            }
            case "minecraft:cooldown" -> {
                if (value.isJsonObject()) {
                    JsonObject cooldown = value.getAsJsonObject();
                    builder.cooldown(optString(cooldown, "category"), asFloat(cooldown.get("duration"), 0f));
                }
            }
            case "minecraft:wearable" -> {
                if (value.isJsonObject()) {
                    JsonObject wearable = value.getAsJsonObject();
                    String slot = optString(wearable, "slot");
                    if (!slot.isEmpty()) {
                        builder.wearable(slot,
                                asInt(wearable.get("protection"), 0),
                                optBoolean(wearable, "hides_player_location", false));
                    }
                }
            }
            case "minecraft:enchantable" -> {
                if (value.isJsonObject()) {
                    JsonObject enchantable = value.getAsJsonObject();
                    String slot = optString(enchantable, "slot");
                    if (!slot.isEmpty()) {
                        builder.enchantable(slot, asInt(enchantable.get("value"), 0));
                    }
                }
            }
            case "minecraft:repairable" -> {
                if (value.isJsonObject()) {
                    applyRepairable(builder, value.getAsJsonObject());
                }
            }
            case "minecraft:block_placer" -> {
                if (value.isJsonObject()) {
                    JsonObject blockPlacer = value.getAsJsonObject();
                    String block = optString(blockPlacer, "block");
                    if (!block.isEmpty()) {
                        builder.blockPlacer(block, asStringList(blockPlacer.get("use_on")).toArray(new String[0]));
                    }
                }
            }
            case "minecraft:tags" -> {
                if (value.isJsonObject()) {
                    List<String> tags = asStringList(value.getAsJsonObject().get("tags"));
                    if (!tags.isEmpty()) {
                        builder.tag(tags.toArray(new String[0]));
                    }
                }
            }
            case "minecraft:digger" -> {
                if (value.isJsonObject()) {
                    applyDigger(builder, value.getAsJsonObject());
                }
            }
            case "minecraft:compostable", "minecraft:fire_resistant", "minecraft:record", "minecraft:damage_absorption" -> {
            }
            case "minecraft:seed" -> {
                if (value.isJsonObject()) {
                    applySeed(builder, value.getAsJsonObject());
                }
            }
            case "minecraft:use_duration" -> builder.useModifiers(1.0f, asFloat(unwrapValue(value), 0f));
            default -> tracker.reportUnsupported(packName, "item-component", key);
        }
    }

    /**
     * The server reads digger speeds from the item's components when it times block breaking; a rule
     * matching by tags keeps its Molang expression as written.
     */
    private static void applyDigger(CustomItemDefinition.SimpleBuilder builder, JsonObject digger) {
        List<DiggerEntry> entries = new ArrayList<>();
        JsonElement speeds = digger.get("destroy_speeds");
        if (speeds != null && speeds.isJsonArray()) {
            for (JsonElement speedElement : speeds.getAsJsonArray()) {
                if (!speedElement.isJsonObject()) {
                    continue;
                }
                JsonObject rule = speedElement.getAsJsonObject();
                int speed = asInt(rule.get("speed"), 1);
                JsonElement block = rule.get("block");
                if (block == null) {
                    continue;
                }
                if (block.isJsonPrimitive()) {
                    entries.add(DiggerEntry.block(block.getAsString(), speed));
                } else if (block.isJsonObject() && block.getAsJsonObject().has("tags")) {
                    String tags = optString(block.getAsJsonObject(), "tags");
                    if (!tags.isBlank()) {
                        entries.add(DiggerEntry.create().speed(speed).tagsExpression(tags));
                    }
                } else if (block.isJsonObject() && block.getAsJsonObject().has("name")) {
                    DiggerEntry entry = DiggerEntry.block(optString(block.getAsJsonObject(), "name"), speed);
                    JsonObject states = block.getAsJsonObject().getAsJsonObject("states");
                    if (states != null) {
                        for (Map.Entry<String, JsonElement> state : states.entrySet()) {
                            JsonElement stateValue = state.getValue();
                            if (stateValue.isJsonPrimitive() && stateValue.getAsJsonPrimitive().isBoolean()) {
                                entry.state(state.getKey(), stateValue.getAsBoolean());
                            } else if (stateValue.isJsonPrimitive() && stateValue.getAsJsonPrimitive().isNumber()) {
                                entry.state(state.getKey(), stateValue.getAsInt());
                            } else if (stateValue.isJsonPrimitive()) {
                                entry.state(state.getKey(), stateValue.getAsString());
                            }
                        }
                    }
                    entries.add(entry);
                }
            }
        }
        if (!entries.isEmpty()) {
            builder.digger(entries.toArray(new DiggerEntry[0]));
        }
        if (digger.has("use_efficiency")) {
            builder.useEfficiency(asBoolean(digger.get("use_efficiency"), false));
        }
    }

    /**
     * A seed is a block placer for its crop, limited to the blocks it may be planted on unless it may go on
     * any solid surface.
     */
    private static void applySeed(CustomItemDefinition.SimpleBuilder builder, JsonObject seed) {
        String crop = optString(seed, "crop_result");
        if (crop.isEmpty()) {
            return;
        }
        List<String> plantAt = new ArrayList<>();
        if (!optBoolean(seed, "plant_at_any_solid_surface", false)) {
            JsonElement entries = seed.get("plant_at");
            if (entries != null && entries.isJsonArray()) {
                for (JsonElement entry : entries.getAsJsonArray()) {
                    String block = entry.isJsonObject() ? optString(entry.getAsJsonObject(), "name") : asStringOrEmpty(entry);
                    if (!block.isEmpty()) {
                        plantAt.add(block.indexOf(':') >= 0 ? block : "minecraft:" + block);
                    }
                }
            } else if (entries != null) {
                String block = asStringOrEmpty(entries);
                if (!block.isEmpty()) {
                    plantAt.add(block.indexOf(':') >= 0 ? block : "minecraft:" + block);
                }
            }
        }
        builder.blockPlacer(crop, plantAt.toArray(new String[0]));
    }

    private static void applyDurability(CustomItemDefinition.SimpleBuilder builder, JsonElement value) {
        if (!value.isJsonObject()) {
            return;
        }
        JsonObject durability = value.getAsJsonObject();
        int max = asInt(durability.get("max_durability"), 0);
        JsonObject damageChance = durability.getAsJsonObject("damage_chance");
        if (damageChance != null) {
            builder.durability(max, asInt(damageChance.get("min"), 100), asInt(damageChance.get("max"), 100));
        } else {
            builder.durability(max);
        }
    }

    private static void applyFood(CustomItemDefinition.SimpleBuilder builder, JsonElement value) {
        if (!value.isJsonObject()) {
            return;
        }
        JsonObject food = value.getAsJsonObject();
        boolean canAlwaysEat = optBoolean(food, "can_always_eat", false);
        int nutrition = asInt(food.get("nutrition"), 0);
        float saturation = asFloat(food.get("saturation_modifier"), 0.6f);
        String convertsTo = extractUsingConvertsTo(food.get("using_converts_to"));
        if (convertsTo.isEmpty()) {
            builder.food(canAlwaysEat, nutrition, saturation);
        } else {
            builder.food(canAlwaysEat, nutrition, saturation, convertsTo);
        }
    }

    private static void applyRepairable(CustomItemDefinition.SimpleBuilder builder, JsonObject repairable) {
        JsonElement repairItems = repairable.get("repair_items");
        if (repairItems == null || !repairItems.isJsonArray()) {
            return;
        }
        for (JsonElement element : repairItems.getAsJsonArray()) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            List<String> items = asStringList(entry.get("items"));
            if (items.isEmpty()) {
                continue;
            }
            JsonElement amount = entry.get("repair_amount");
            if (amount == null || !amount.isJsonPrimitive()) {
                builder.repairable(items, 0f);
            } else if (amount.getAsJsonPrimitive().isNumber()) {
                builder.repairable(items, amount.getAsFloat());
            } else {
                builder.repairable(items, amount.getAsString());
            }
        }
    }

    private static String extractIconTexture(JsonElement value) {
        if (value == null) {
            return "";
        }
        if (value.isJsonPrimitive()) {
            return value.getAsString();
        }
        if (value.isJsonObject()) {
            JsonObject icon = value.getAsJsonObject();
            if (icon.has("textures") && icon.get("textures").isJsonObject()) {
                JsonObject textures = icon.getAsJsonObject("textures");
                if (textures.has("default")) {
                    return textures.get("default").getAsString();
                }
                for (Map.Entry<String, JsonElement> first : textures.entrySet()) {
                    return first.getValue().getAsString();
                }
            }
            return optString(icon, "texture");
        }
        return "";
    }

    private static String extractUsingConvertsTo(JsonElement element) {
        if (element == null) {
            return "";
        }
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        }
        if (element.isJsonObject()) {
            return optString(element.getAsJsonObject(), "item");
        }
        return "";
    }

    /** Many components accept either a bare value or {@code {"value": x}}. */
    private static JsonElement unwrapValue(JsonElement element) {
        if (element != null && element.isJsonObject() && element.getAsJsonObject().has("value")) {
            return element.getAsJsonObject().get("value");
        }
        return element;
    }

    private static String optString(JsonObject json, String key) {
        return asStringOrEmpty(json.get(key));
    }

    private static boolean optBoolean(JsonObject json, String key, boolean fallback) {
        return asBoolean(json.get(key), fallback);
    }

    private static String asStringOrEmpty(JsonElement element) {
        return element != null && element.isJsonPrimitive() ? element.getAsString() : "";
    }

    private static boolean asBoolean(JsonElement element, boolean fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsBoolean() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static int asInt(JsonElement element, int fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsInt() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static float asFloat(JsonElement element, float fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsFloat() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static List<String> asStringList(JsonElement element) {
        List<String> values = new ArrayList<>();
        if (element == null) {
            return values;
        }
        if (element.isJsonPrimitive()) {
            values.add(element.getAsString());
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement entry : array) {
                if (entry.isJsonPrimitive()) {
                    values.add(entry.getAsString());
                }
            }
        }
        return values;
    }
}
