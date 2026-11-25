package org.powernukkitx.addons.parser;

import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.network.protocol.types.inventory.creative.CreativeCustomGroups;
import org.powernukkitx.registry.CreativeItemRegistry;
import org.powernukkitx.registry.Registries;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cloudburstmc.protocol.bedrock.data.payload.creative.CreativeItemCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Turns a behaviour pack's {@code item_catalog/crafting_item_catalog.json} into creative inventory groups.
 * <p>
 * Each group the catalog names is created in its category unless a group of that name exists already, in
 * which case the groups merge, and the pack's items and blocks listed in it are moved into it. Items listed
 * in a group without a {@code group_identifier} join the category's ungrouped items. Listed items appear
 * in the order the catalogs list them, after the group's existing items. The groups are injected, and the
 * items moved, when the server finalises the creative inventory after plugins have loaded, so the catalog
 * may list content any pack or plugin registers. Vanilla items listed in a catalog keep their own place.
 */
public final class ItemCatalogParser {

    private static final String CATALOG_KEY = "minecraft:crafting_items_catalog";

    private final AddonFeatureTracker tracker;

    public ItemCatalogParser(AddonFeatureTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * @param packName the pack the document came from, for the tracker
     * @param root     the parsed document
     * @return how many items were assigned a group
     */
    public int parse(@NotNull String packName, @NotNull JsonObject root) {
        JsonObject catalog = root.getAsJsonObject(CATALOG_KEY);
        JsonElement categories = catalog == null ? null : catalog.get("categories");
        if (categories == null || !categories.isJsonArray()) {
            return 0;
        }
        int assigned = 0;
        for (JsonElement categoryElement : categories.getAsJsonArray()) {
            if (!categoryElement.isJsonObject()) {
                continue;
            }
            JsonObject category = categoryElement.getAsJsonObject();
            CreativeItemCategory creativeCategory = category(packName, category);
            JsonElement groups = category.get("groups");
            if (creativeCategory == null || groups == null || !groups.isJsonArray()) {
                continue;
            }
            for (JsonElement groupElement : groups.getAsJsonArray()) {
                if (groupElement.isJsonObject()) {
                    assigned += parseGroup(creativeCategory, groupElement.getAsJsonObject());
                }
            }
        }
        return assigned;
    }

    private int parseGroup(CreativeItemCategory category, JsonObject group) {
        JsonObject identifier = group.getAsJsonObject("group_identifier");
        String name = identifier == null ? "" : stringOf(identifier.get("name"));
        JsonElement items = group.get("items");
        if (items == null || !items.isJsonArray() || (identifier != null && name.isEmpty())) {
            return 0;
        }
        String groupName = name.isEmpty() ? null : CreativeGroupNames.normalize(name);
        if (groupName != null && Registries.CREATIVE.getGroupIndexByName(groupName) < 0) {
            String icon = stringOf(identifier.get("icon"));
            CreativeCustomGroups.define(category, groupName, icon.isEmpty() ? "minecraft:stone" : icon);
        }
        int assigned = 0;
        for (JsonElement item : items.getAsJsonArray()) {
            String id = item.isJsonObject() ? stringOf(item.getAsJsonObject().get("name")) : stringOf(item);
            if (!CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.contains(id)) {
                continue;
            }
            if (groupName == null) {
                CreativeItemRegistry.ITEM_GROUP_MAP.remove(id);
                CreativeItemRegistry.ITEM_CATEGORY_MAP.put(id, CreativeCategory.valueOf(category.name()));
            } else {
                CreativeItemRegistry.ITEM_CATEGORY_MAP.remove(id);
                CreativeItemRegistry.ITEM_GROUP_MAP.put(id, groupName);
            }
            CreativeItemRegistry.ITEM_ORDER_MAP.putIfAbsent(id, CreativeItemRegistry.ITEM_ORDER_MAP.size());
            assigned++;
        }
        return assigned;
    }

    private CreativeItemCategory category(String packName, JsonObject category) {
        String name = stringOf(category.get("category_name"));
        return switch (name.toLowerCase(Locale.ENGLISH)) {
            case "construction" -> CreativeItemCategory.CONSTRUCTION;
            case "equipment" -> CreativeItemCategory.EQUIPMENT;
            case "items" -> CreativeItemCategory.ITEMS;
            case "nature" -> CreativeItemCategory.NATURE;
            default -> {
                tracker.reportUnsupported(packName, "item-catalog-category", name.isEmpty() ? "(missing)" : name);
                yield null;
            }
        };
    }

    private static String stringOf(JsonElement element) {
        return element != null && element.isJsonPrimitive() ? element.getAsString() : "";
    }
}
