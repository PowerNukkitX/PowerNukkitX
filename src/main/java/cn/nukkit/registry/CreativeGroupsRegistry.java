package cn.nukkit.registry;

import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemData;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemGroup;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeCustomGroups;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for registering and injecting custom creative groups into the runtime group list.
 */
@Slf4j
public class CreativeGroupsRegistry {
    private static final ObjectLinkedOpenHashSet<CreativeItemGroup> INJECTED_GROUPS = new ObjectLinkedOpenHashSet<>();

    /**
     * Registers a new custom creative group. If this is the first group being registered,
     * triggers the injection process into the existing creative registry.
     */
    public static void load(CreativeCustomGroups.CustomGroupDefinition def) {
        if (!isValid(def)) return;

        Item icon = resolveIcon(def);
        CreativeItemGroup group = new CreativeItemGroup(def.getCategory(), def.getName(), icon);
        INJECTED_GROUPS.add(group);
    }

    private static boolean isValid(CreativeCustomGroups.CustomGroupDefinition def) {
        return def != null && def.getName() != null && def.getCategory() != null;
    }

    private static Item resolveIcon(CreativeCustomGroups.CustomGroupDefinition def) {
        for (CreativeItemData data : CreativeItemRegistry.ITEM_DATA) {
            Item candidate = data.getItem();
            if (def.getIconId().equals(candidate.getName()) || def.getIconId().equalsIgnoreCase(candidate.getId())) {
                return candidate;
            }
        }
        log.warn("Icon '{}' could not be resolved for group '{}'. Falling back to stone.", def.getIconId(), def.getName());

        return Item.get("minecraft:stone");
    }

    /**
     * Injects all registered custom groups into the group index map and runtime list.
     */
    public static void register() {
        List<CreativeItemGroup> allOriginalGroups = new ArrayList<>(Registries.CREATIVE.getGroupList());
        Map<CreativeItemGroup, Integer> originalGroupIndices = extractOriginalGroupIndices(allOriginalGroups);

        Map<CreativeCategory, List<CreativeItemGroup>> groupedVanilla = groupByCategory(allOriginalGroups);
        Map<CreativeCategory, List<CreativeItemGroup>> groupedCustom = groupByCategory(new ArrayList<>(INJECTED_GROUPS));

        Map<Integer, Integer> groupIndexMap = new HashMap<>();
        List<CreativeItemGroup> rebuilt = rebuildGroupsAndRemap(groupedVanilla, groupedCustom, originalGroupIndices, groupIndexMap);

        Registries.CREATIVE.getGroupList().clear();
        Registries.CREATIVE.getGroupList().addAll(rebuilt);

        remapCreativeItemGroups(groupIndexMap);
    }

    private static Map<CreativeItemGroup, Integer> extractOriginalGroupIndices(List<CreativeItemGroup> allGroups) {
        Map<CreativeItemGroup, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < allGroups.size(); i++) {
            indexMap.put(allGroups.get(i), i);
        }
        return indexMap;
    }

    private static Map<CreativeCategory, List<CreativeItemGroup>> groupByCategory(List<CreativeItemGroup> groups) {
        Map<CreativeCategory, List<CreativeItemGroup>> grouped = new EnumMap<>(CreativeCategory.class);
        for (CreativeItemGroup group : groups) {
            CreativeCategory category = CreativeCategory.valueOf(group.getCategory().name());
            grouped.computeIfAbsent(category, k -> new ArrayList<>()).add(group);
        }
        return grouped;
    }

    private static List<CreativeItemGroup> rebuildGroupsAndRemap(
            Map<CreativeCategory, List<CreativeItemGroup>> groupedVanilla,
            Map<CreativeCategory, List<CreativeItemGroup>> groupedCustom,
            Map<CreativeItemGroup, Integer> originalGroupIndices,
            Map<Integer, Integer> groupIndexMap
    ) {
        List<CreativeItemGroup> rebuilt = new ArrayList<>();
        int newIndex = 0;

        for (CreativeCategory category : CreativeCategory.values()) {
            List<CreativeItemGroup> vanilla = groupedVanilla.getOrDefault(category, Collections.emptyList());
            List<CreativeItemGroup> custom = groupedCustom.getOrDefault(category, Collections.emptyList());

            if (!vanilla.isEmpty()) {
                List<CreativeItemGroup> vanillaMain = vanilla.subList(0, vanilla.size() - 1);
                CreativeItemGroup wildcardGroup = vanilla.get(vanilla.size() - 1);

                for (CreativeItemGroup group : vanillaMain) {
                    rebuilt.add(group);
                    int originalIndex = originalGroupIndices.getOrDefault(group, -1);
                    if (originalIndex >= 0) {
                        groupIndexMap.put(originalIndex, newIndex);
                    }
                    CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP
                            .computeIfAbsent(category, k -> new HashMap<>())
                            .put(group.getName(), newIndex);
                    newIndex++;
                }

                for (CreativeItemGroup group : custom) {
                    rebuilt.add(group);
                    CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP
                            .computeIfAbsent(category, k -> new HashMap<>())
                            .put(group.getName(), newIndex);
                    log.debug("Injected custom creative group '{}' in category '{}' with new index {}", group.getName(), category, newIndex);
                    newIndex++;
                }

                rebuilt.add(wildcardGroup);
                int originalIndex = originalGroupIndices.getOrDefault(wildcardGroup, -1);
                if (originalIndex >= 0) {
                    groupIndexMap.put(originalIndex, newIndex);
                }
                CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP
                        .computeIfAbsent(category, k -> new HashMap<>())
                        .put(wildcardGroup.getName(), newIndex);
                newIndex++;
            }
        }
        return rebuilt;
    }

    /**
     * Rebuilds the creative item group assignments with updated group indices.
     */
    private static void remapCreativeItemGroups(Map<Integer, Integer> groupIndexMap) {
        ObjectLinkedOpenHashSet<CreativeItemData> current = CreativeItemRegistry.ITEM_DATA;
        ObjectLinkedOpenHashSet<CreativeItemData> rebuilt = new ObjectLinkedOpenHashSet<>();

        for (CreativeItemData data : current) {
            Item item = data.getItem();
            int originalGroupId = data.getGroupId();
            int newGroupId = CreativeItemRegistry.LAST_ITEMS_INDEX;

            // Vanilla item: remap based on old group index
            if (!isCategoryFallbackIndex(originalGroupId)) {
                Integer mapped = groupIndexMap.get(originalGroupId);
                if (mapped != null) {
                    newGroupId = mapped;
                }
            } else {
                // Custom item: use mapped group names from ITEM_GROUP_MAP saved on item/block registry
                String key = item.getIdentifier().toString();
                String groupName = CreativeItemRegistry.ITEM_GROUP_MAP.get(key);
                CreativeCategory fallbackCategory = getCategoryFromFallbackIndex(originalGroupId);

                if (groupName != null) {
                    Integer resolvedIndex = null;
                    CreativeCategory category = null;

                    for (Map.Entry<CreativeCategory, Map<String, Integer>> entry : CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP.entrySet()) {
                        resolvedIndex = entry.getValue().get(groupName);
                        if (resolvedIndex != null) {
                            category = entry.getKey();
                            break;
                        }
                    }

                    if (resolvedIndex != null) {
                        newGroupId = resolvedIndex;
                    } else {
                        CreativeCategory resolvedFallback = (category != null) ? category : fallbackCategory;
                        int fallbackIndex = CreativeItemRegistry.getLastGroupIndexFrom(resolvedFallback.name());
                        log.debug("Group '{}' not found; falling back to last index of category '{}' -> index {}", 
                                 groupName, resolvedFallback, fallbackIndex);
                        newGroupId = fallbackIndex;
                    }
                } else {
                    // No group set at all, fallback to last index of original category
                    int fallbackIndex = CreativeItemRegistry.getLastGroupIndexFrom(fallbackCategory.name());
                    log.debug("Group name not saved for item '{}'; falling back to last index {}",
                             item.getName(), fallbackIndex);
                    newGroupId = fallbackIndex;
                }
            }
            rebuilt.add(new CreativeItemData(item, newGroupId));
        }
        current.clear();
        current.addAll(rebuilt);
        CreativeItemRegistry.ITEM_GROUP_MAP.clear();
    }

    private static boolean isCategoryFallbackIndex(int groupId) {
        return groupId == CreativeItemRegistry.LAST_CONSTRUCTION_INDEX
            || groupId == CreativeItemRegistry.LAST_EQUIPMENTS_INDEX
            || groupId == CreativeItemRegistry.LAST_ITEMS_INDEX
            || groupId == CreativeItemRegistry.LAST_NATURE_INDEX;
    }

    public static CreativeCategory getCategoryFromFallbackIndex(int groupId) {
        if (groupId == CreativeItemRegistry.LAST_CONSTRUCTION_INDEX) return CreativeCategory.CONSTRUCTION;
        if (groupId == CreativeItemRegistry.LAST_EQUIPMENTS_INDEX) return CreativeCategory.EQUIPMENT;
        if (groupId == CreativeItemRegistry.LAST_ITEMS_INDEX) return CreativeCategory.ITEMS;
        if (groupId == CreativeItemRegistry.LAST_NATURE_INDEX) return CreativeCategory.NATURE;
        return CreativeCategory.ITEMS;
    }
}
