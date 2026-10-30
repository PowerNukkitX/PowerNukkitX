package org.powernukkitx.addons.parser;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.powernukkitx.addons.parser.BehaviorItemParser.asBoolean;
import static org.powernukkitx.addons.parser.BehaviorItemParser.asFloat;
import static org.powernukkitx.addons.parser.BehaviorItemParser.asInt;
import static org.powernukkitx.addons.parser.BehaviorItemParser.asStringOrEmpty;
import static org.powernukkitx.addons.parser.BehaviorItemParser.optString;
import static org.powernukkitx.addons.parser.BehaviorItemParser.unwrapValue;

/**
 * Item components the client reads that {@link org.powernukkitx.item.customitem.CustomItemDefinition.SimpleBuilder}
 * does not model, in the schema Bedrock Dedicated Server 1.26.50 sends for the same behaviour pack JSON.
 * Most only change what the client shows or predicts; the server reads its own copy of the gameplay
 * ones from the pack.
 */
final class ClientItemComponents {

    /**
     * The components {@link #write(JsonObject, CompoundTag)} sends.
     */
    static final Set<String> HANDLED = Set.of(
            "minecraft:rarity", "minecraft:hover_text_color", "minecraft:interact_button", "minecraft:liquid_clipped",
            "minecraft:record", "minecraft:dyeable", "minecraft:fire_resistant", "minecraft:compostable",
            "minecraft:storage_item", "minecraft:storage_weight_limit", "minecraft:storage_weight_modifier",
            "minecraft:bundle_interaction", "minecraft:swing_duration", "minecraft:swing_sounds",
            "minecraft:piercing_weapon", "minecraft:kinetic_weapon");

    private ClientItemComponents() {
    }

    /**
     * Adds the client components of a pack item to the definition the builder produced, and completes
     * the builder's {@code minecraft:cooldown} and {@code minecraft:use_modifiers} with the fields it
     * leaves out.
     *
     * @param components the item's components in the pack
     * @param target     the {@code components} compound of the definition
     */
    static void write(JsonObject components, CompoundTag target) {
        for (Map.Entry<String, JsonElement> component : components.entrySet()) {
            CompoundTag translated = translate(component.getKey(), component.getValue());
            if (translated != null) {
                target.putCompound(component.getKey(), translated);
            }
        }
        if (components.has("minecraft:liquid_clipped") && target.containsCompound("item_properties")) {
            target.getCompound("item_properties")
                    .putBoolean("liquid_clipped", asBoolean(unwrapValue(components.get("minecraft:liquid_clipped")), false));
        }
        JsonElement cooldown = components.get("minecraft:cooldown");
        if (cooldown != null && cooldown.isJsonObject() && target.containsCompound("minecraft:cooldown")) {
            String type = optString(cooldown.getAsJsonObject(), "type");
            target.getCompound("minecraft:cooldown").putString("type", type.isEmpty() ? "use" : type);
        }
        JsonElement useModifiers = components.get("minecraft:use_modifiers");
        if (useModifiers != null && useModifiers.isJsonObject() && target.containsCompound("minecraft:use_modifiers")) {
            JsonObject json = useModifiers.getAsJsonObject();
            String startUsing = optString(json, "start_using");
            String startSound = optString(json, "start_sound");
            CompoundTag modifiers = target.getCompound("minecraft:use_modifiers")
                    .putBoolean("emit_vibrations", asBoolean(json.get("emit_vibrations"), true))
                    .putString("start_using", startUsing.isEmpty() ? "if_first" : startUsing);
            if (!startSound.isEmpty()) {
                modifiers.putString("start_sound", startSound);
            }
        }
        if (!target.contains("item_tags")) {
            target.putList("item_tags", new ListTag<StringTag>());
        }
    }

    private static @Nullable CompoundTag translate(String key, JsonElement value) {
        JsonObject json = value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
        return switch (key) {
            case "minecraft:rarity", "minecraft:hover_text_color" -> {
                String text = asStringOrEmpty(unwrapValue(value));
                yield text.isEmpty() ? null : new CompoundTag().putString("value", text);
            }
            case "minecraft:interact_button" -> {
                JsonElement text = unwrapValue(value);
                boolean enabled = text == null || !text.isJsonPrimitive() || !text.getAsJsonPrimitive().isBoolean() || text.getAsBoolean();
                yield enabled ? new CompoundTag(new LinkedHashMap<>())
                        .putString("interact_text", text != null && text.isJsonPrimitive() && !text.getAsJsonPrimitive().isBoolean()
                                ? text.getAsString() : "")
                        .putBoolean("requires_interact", true) : null;
            }
            case "minecraft:liquid_clipped", "minecraft:fire_resistant" ->
                    new CompoundTag().putBoolean("value", asBoolean(unwrapValue(value), false));
            case "minecraft:record" -> new CompoundTag(new LinkedHashMap<>())
                    .putInt("comparator_signal", asInt(json.get("comparator_signal"), 1))
                    .putFloat("duration", asFloat(json.get("duration"), 0f))
                    .putString("sound_event", optString(json, "sound_event"));
            case "minecraft:dyeable" -> {
                int[] rgb = color(json.get("default_color"));
                yield rgb == null ? new CompoundTag() : new CompoundTag().putList("default_color", new ListTag<IntTag>()
                        .add(new IntTag(rgb[0])).add(new IntTag(rgb[1])).add(new IntTag(rgb[2])));
            }
            case "minecraft:compostable" ->
                    new CompoundTag().putByte("composting_chance", Math.clamp(asInt(json.get("composting_chance"), 0), 0, 100));
            case "minecraft:storage_item" -> new CompoundTag(new LinkedHashMap<>())
                    .putBoolean("allow_nested_storage_items", asBoolean(json.get("allow_nested_storage_items"), true))
                    .putList("allowed_items", itemNames(json.get("allowed_items")))
                    .putList("banned_items", itemNames(json.get("banned_items")))
                    .putInt("max_slots", asInt(json.get("max_slots"), 64));
            case "minecraft:storage_weight_limit" ->
                    new CompoundTag().putInt("max_weight_limit", asInt(json.get("max_weight_limit"), 64));
            case "minecraft:storage_weight_modifier" ->
                    new CompoundTag().putInt("weight_in_storage_item", asInt(json.get("weight_in_storage_item"), 4));
            case "minecraft:bundle_interaction" ->
                    new CompoundTag().putInt("num_viewable_slots", asInt(json.get("num_viewable_slots"), 12));
            case "minecraft:swing_duration" -> new CompoundTag().putFloat("value", asFloat(unwrapValue(value), 0.3f));
            case "minecraft:swing_sounds" -> {
                CompoundTag sounds = new CompoundTag(new LinkedHashMap<>());
                for (Map.Entry<String, JsonElement> sound : json.entrySet()) {
                    if (sound.getValue().isJsonPrimitive()) {
                        sounds.putString(sound.getKey(), sound.getValue().getAsString());
                    }
                }
                yield sounds;
            }
            case "minecraft:piercing_weapon" -> {
                CompoundTag reach = range(json.get("reach"), 0f, 3f);
                CompoundTag creativeReach = json.has("creative_reach") ? range(json.get("creative_reach"), 0f, 3f) : reach.copy();
                yield new CompoundTag(new LinkedHashMap<>())
                        .putCompound("creative_reach", creativeReach)
                        .putFloat("hitbox_margin", asFloat(json.get("hitbox_margin"), 0f))
                        .putCompound("reach", reach);
            }
            case "minecraft:kinetic_weapon" -> {
                CompoundTag reach = range(json.get("reach"), 0f, 3f);
                CompoundTag weapon = new CompoundTag(new LinkedHashMap<>())
                        .putCompound("creative_reach", json.has("creative_reach") ? range(json.get("creative_reach"), 0f, 3f) : reach.copy());
                putConditions(weapon, "damage_conditions", json.get("damage_conditions"));
                weapon.putFloat("damage_modifier", asFloat(json.get("damage_modifier"), 0f))
                        .putFloat("damage_multiplier", asFloat(json.get("damage_multiplier"), 1f))
                        .putShort("delay", Math.max(asInt(json.get("delay"), 0), 0));
                putConditions(weapon, "dismount_conditions", json.get("dismount_conditions"));
                weapon.putFloat("hitbox_margin", asFloat(json.get("hitbox_margin"), 0f));
                putConditions(weapon, "knockback_conditions", json.get("knockback_conditions"));
                weapon.putCompound("reach", reach);
                yield new CompoundTag().putCompound("minecraft:kinetic_weapon", weapon);
            }
            default -> null;
        };
    }

    /**
     * Kinetic weapon conditions as the dedicated server nests them: a component compound inside a compound
     * of the same name, with every condition field present.
     */
    private static void putConditions(CompoundTag weapon, String key, @Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject json = element.getAsJsonObject();
        weapon.putCompound(key, new CompoundTag(new LinkedHashMap<>())
                .putShort("max_duration", asInt(json.get("max_duration"), -1))
                .putFloat("min_relative_speed", asFloat(json.get("min_relative_speed"), 0f))
                .putFloat("min_speed", asFloat(json.get("min_speed"), 0f)));
    }

    private static CompoundTag range(@Nullable JsonElement element, float min, float max) {
        JsonObject json = element != null && element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        return new CompoundTag(new LinkedHashMap<>())
                .putFloat("max", asFloat(json.get("max"), max))
                .putFloat("min", asFloat(json.get("min"), min));
    }

    /**
     * Item descriptors as the client lists them, one {@code name} compound each.
     */
    private static ListTag<CompoundTag> itemNames(@Nullable JsonElement element) {
        ListTag<CompoundTag> names = new ListTag<>();
        if (element == null || !element.isJsonArray()) {
            return names;
        }
        for (JsonElement entry : element.getAsJsonArray()) {
            String name = entry.isJsonObject() ? optString(entry.getAsJsonObject(), "item") : asStringOrEmpty(entry);
            if (name.isEmpty() && entry.isJsonObject()) {
                name = optString(entry.getAsJsonObject(), "name");
            }
            if (!name.isEmpty()) {
                names.add(new CompoundTag().putString("name", name));
            }
        }
        return names;
    }

    /**
     * @return the colour as red, green and blue, from a {@code #rrggbb} string or a three-number array,
     * or null when the value is neither
     */
    private static int @Nullable [] color(@Nullable JsonElement element) {
        if (element != null && element.isJsonArray() && element.getAsJsonArray().size() >= 3) {
            return new int[]{asInt(element.getAsJsonArray().get(0), 0), asInt(element.getAsJsonArray().get(1), 0),
                    asInt(element.getAsJsonArray().get(2), 0)};
        }
        String hex = asStringOrEmpty(element);
        if (hex.startsWith("#") && hex.length() == 7) {
            try {
                int value = Integer.parseInt(hex.substring(1), 16);
                return new int[]{(value >> 16) & 0xff, (value >> 8) & 0xff, value & 0xff};
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
