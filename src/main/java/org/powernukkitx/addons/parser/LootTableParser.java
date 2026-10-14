package org.powernukkitx.addons.parser;

import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.loot.LootTable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Turns a behaviour pack's {@code loot_tables/**.json} into {@link LootTable}s.
 * <p>
 * Supported: pools with fixed or ranged rolls; weighted {@code item}, {@code loot_table} and
 * {@code empty} entries; the {@code random_chance}, {@code random_chance_with_looting} (without the
 * looting bonus) and {@code match_tool} conditions on both pools and entries; and the
 * {@code set_count}, {@code set_data}, {@code set_damage}, {@code set_name} and {@code set_lore}
 * functions.
 * <p>
 * Anything else is recorded on the {@link AddonFeatureTracker}. A condition that cannot be
 * evaluated, or a {@code match_tool} requirement that cannot be checked, never holds: loot gated
 * behind something the server cannot verify is withheld rather than handed out unconditionally,
 * which would let players duplicate it.
 */
public final class LootTableParser {

    private final AddonFeatureTracker tracker;

    public LootTableParser(AddonFeatureTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * @param packName the pack the document came from, for the tracker
     * @param root     the parsed document
     * @return the table; a document without pools yields a table that generates nothing
     */
    public @NotNull LootTable parse(@NotNull String packName, @NotNull JsonObject root) {
        List<LootTable.Pool> pools = new ArrayList<>();
        JsonElement poolsElement = root.get("pools");
        if (poolsElement != null && poolsElement.isJsonArray()) {
            for (JsonElement poolElement : poolsElement.getAsJsonArray()) {
                if (poolElement.isJsonObject()) {
                    LootTable.Pool pool = parsePool(packName, poolElement.getAsJsonObject());
                    if (pool != null) {
                        pools.add(pool);
                    }
                }
            }
        }
        return new LootTable(pools);
    }

    private @Nullable LootTable.Pool parsePool(String packName, JsonObject poolJson) {
        List<LootTable.Entry> entries = new ArrayList<>();
        JsonElement entriesElement = poolJson.get("entries");
        if (entriesElement != null && entriesElement.isJsonArray()) {
            for (JsonElement entryElement : entriesElement.getAsJsonArray()) {
                if (entryElement.isJsonObject()) {
                    LootTable.Entry entry = parseEntry(packName, entryElement.getAsJsonObject());
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
            }
        }
        if (entries.isEmpty()) {
            return null;
        }
        return new LootTable.Pool(parseRange(poolJson.get("rolls"), LootTable.NumberRange.ONE),
                parseConditions(packName, poolJson.get("conditions")), entries);
    }

    private @Nullable LootTable.Entry parseEntry(String packName, JsonObject entryJson) {
        String type = optString(entryJson, "type").toLowerCase(Locale.ENGLISH);
        LootTable.EntryType entryType = switch (type) {
            case "item" -> LootTable.EntryType.ITEM;
            case "loot_table" -> LootTable.EntryType.LOOT_TABLE;
            case "empty" -> LootTable.EntryType.EMPTY;
            default -> null;
        };
        if (entryType == null) {
            tracker.reportUnsupported(packName, "loot-entry-type", type.isEmpty() ? "(missing)" : type);
            return null;
        }
        int weight = asInt(entryJson.get("weight"), 1);
        List<LootTable.Condition> conditions = parseConditions(packName, entryJson.get("conditions"));
        if (entryType == LootTable.EntryType.EMPTY) {
            return new LootTable.Entry(entryType, "", weight, LootTable.NumberRange.ONE, null, null, null, conditions);
        }

        LootTable.NumberRange count = LootTable.NumberRange.ONE;
        Integer data = null;
        String displayName = null;
        List<String> lore = null;
        JsonElement functions = entryJson.get("functions");
        if (functions != null && functions.isJsonArray()) {
            for (JsonElement functionElement : functions.getAsJsonArray()) {
                if (!functionElement.isJsonObject()) {
                    continue;
                }
                JsonObject function = functionElement.getAsJsonObject();
                String functionName = optString(function, "function").toLowerCase(Locale.ENGLISH);
                switch (functionName) {
                    case "set_count" -> count = parseRange(function.get("count"), LootTable.NumberRange.ONE);
                    case "set_data" -> data = asInt(rangeMin(function.get("data")), 0);
                    case "set_damage" -> data = asInt(rangeMin(function.get("damage")), 0);
                    case "set_name" -> displayName = optString(function, "name");
                    case "set_lore" -> lore = asStringList(function.get("lore"));
                    default -> tracker.reportUnsupported(packName, "loot-function", functionName);
                }
            }
        }
        return new LootTable.Entry(entryType, optString(entryJson, "name"), weight, count, data, displayName, lore, conditions);
    }

    private List<LootTable.Condition> parseConditions(String packName, @Nullable JsonElement element) {
        List<LootTable.Condition> conditions = new ArrayList<>();
        if (element == null || !element.isJsonArray()) {
            return conditions;
        }
        for (JsonElement conditionElement : element.getAsJsonArray()) {
            if (!conditionElement.isJsonObject()) {
                continue;
            }
            JsonObject condition = conditionElement.getAsJsonObject();
            String name = optString(condition, "condition").toLowerCase(Locale.ENGLISH);
            switch (name) {
                case "random_chance", "random_chance_with_looting" ->
                        conditions.add(new LootTable.RandomChance(asFloat(condition.get("chance"), 1.0f)));
                case "match_tool" -> conditions.add(parseMatchTool(packName, condition));
                default -> {
                    tracker.reportUnsupported(packName, "loot-condition", name.isEmpty() ? "(missing)" : name);
                    conditions.add(new LootTable.MatchTool(null, List.of(), List.of(), List.of(), true));
                }
            }
        }
        return conditions;
    }

    private LootTable.MatchTool parseMatchTool(String packName, JsonObject condition) {
        String item = null;
        List<String> allTags = List.of();
        List<String> anyTags = List.of();
        List<String> noneTags = List.of();
        boolean unsatisfiable = false;
        for (Map.Entry<String, JsonElement> entry : condition.entrySet()) {
            switch (entry.getKey()) {
                case "condition", "count" -> {
                }
                case "item" -> item = asStringOrNull(entry.getValue());
                case "minecraft:match_tool_filter_all" -> allTags = asStringList(entry.getValue());
                case "minecraft:match_tool_filter_any" -> anyTags = asStringList(entry.getValue());
                case "minecraft:match_tool_filter_none" -> noneTags = asStringList(entry.getValue());
                default -> {
                    tracker.reportUnsupported(packName, "loot-match-tool", entry.getKey());
                    unsatisfiable = true;
                }
            }
        }
        return new LootTable.MatchTool(item, allTags, anyTags, noneTags, unsatisfiable);
    }

    private static LootTable.NumberRange parseRange(@Nullable JsonElement element, LootTable.NumberRange fallback) {
        if (element == null) {
            return fallback;
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            int value = element.getAsInt();
            return new LootTable.NumberRange(value, value);
        }
        if (element.isJsonObject()) {
            JsonObject range = element.getAsJsonObject();
            int min = asInt(range.get("min"), 1);
            int max = asInt(range.get("max"), min);
            return new LootTable.NumberRange(min, Math.max(min, max));
        }
        return fallback;
    }

    private static @Nullable JsonElement rangeMin(@Nullable JsonElement element) {
        if (element != null && element.isJsonObject() && element.getAsJsonObject().has("min")) {
            return element.getAsJsonObject().get("min");
        }
        return element;
    }

    private static String optString(JsonObject json, String key) {
        JsonElement element = json.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : "";
    }

    private static @Nullable String asStringOrNull(@Nullable JsonElement element) {
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    private static List<String> asStringList(@Nullable JsonElement element) {
        List<String> values = new ArrayList<>();
        if (element == null) {
            return values;
        }
        if (element.isJsonPrimitive()) {
            values.add(element.getAsString());
        } else if (element.isJsonArray()) {
            for (JsonElement value : element.getAsJsonArray()) {
                if (value.isJsonPrimitive()) {
                    values.add(value.getAsString());
                }
            }
        }
        return values;
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
}
