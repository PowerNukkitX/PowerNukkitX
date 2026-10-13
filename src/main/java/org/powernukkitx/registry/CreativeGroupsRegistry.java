package org.powernukkitx.registry;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.payload.creative.CreativeGroupInfoPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.creative.CreativeItemEntryPayload;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.network.protocol.types.inventory.creative.CreativeCustomGroups;

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
    private static final ObjectLinkedOpenHashSet<CreativeCustomGroups.CustomGroupDefinition> PENDING_DEFINITIONS = new ObjectLinkedOpenHashSet<>();

    /**
     * Stages a custom creative group for later registration.
     * This only records the definition (group name, category and icon ID); nothing is injected
     * into the creative registry and the icon is <b>not</b> resolved yet. The actual finalization
     * happens in {@link #register()}, which PNX invokes automatically after all plugins (and their
     * custom items/blocks) have been loaded. Plugins should therefore not call {@link #register()}
     * manually.
     */
    public static void load(CreativeCustomGroups.CustomGroupDefinition def) {
        if (!isValid(def)) return;
        PENDING_DEFINITIONS.add(def);
    }

    private static boolean isValid(CreativeCustomGroups.CustomGroupDefinition def) {
        return def != null && def.getName() != null && def.getCategory() != null;
    }

    private static Item resolveIcon(CreativeCustomGroups.CustomGroupDefinition def) {
        for (CreativeItemEntryPayload data : CreativeItemRegistry.ITEM_DATA) {
            Item candidate = Item.fromNetwork(data.getItemInstance());
            if (def.getIconId().equals(candidate.getName()) || def.getIconId().equalsIgnoreCase(candidate.getId())) {
                return candidate;
            }
        }
        log.warn("Icon '{}' could not be resolved for group '{}'. Falling back to stone.", def.getIconId(), def.getName());

        return Item.get("minecraft:stone");
    }

    /**
     * Finalizes all staged custom groups and injects them into the group index map and runtime list.
     * Icons are resolved here (not when the group is staged in {@link #load(CreativeCustomGroups.CustomGroupDefinition)}),
     * so an icon referencing a custom item is resolved correctly once every custom item/block is loaded.
     * PNX calls this automatically after plugin loading; plugins should not call it manually.
     */
    public static void register() {
        if (PENDING_DEFINITIONS.isEmpty()) return;

        List<CreativeGroupInfoPayload> injected = buildStagedGroups();

        List<CreativeGroupInfoPayload> allOriginalGroups = new ArrayList<>(Registries.CREATIVE.getGroupList());
        Map<CreativeGroupInfoPayload, Integer> originalGroupIndices = extractOriginalGroupIndices(allOriginalGroups);

        Map<CreativeCategory, List<CreativeGroupInfoPayload>> groupedVanilla = groupByCategory(allOriginalGroups);
        Map<CreativeCategory, List<CreativeGroupInfoPayload>> groupedCustom = groupByCategory(injected);

        Map<Integer, Integer> groupIndexMap = new HashMap<>();
        List<CreativeGroupInfoPayload> rebuilt = rebuildGroupsAndRemap(groupedVanilla, groupedCustom, originalGroupIndices, groupIndexMap);

        Registries.CREATIVE.getGroupList().clear();
        Registries.CREATIVE.getGroupList().addAll(rebuilt);

        remapCreativeItemGroups(groupIndexMap);
        PENDING_DEFINITIONS.clear();
    }

    /**
     * Builds the {@link CreativeGroupInfoPayload} for every staged definition, resolving each icon now
     * that all custom items/blocks are loaded.
     */
    private static List<CreativeGroupInfoPayload> buildStagedGroups() {
        List<CreativeGroupInfoPayload> injected = new ArrayList<>();
        for (CreativeCustomGroups.CustomGroupDefinition def : PENDING_DEFINITIONS) {
            Item icon = resolveIcon(def);
            CreativeGroupInfoPayload group = new CreativeGroupInfoPayload();
            group.setCreativeCategory(def.getCategory());
            group.setName(def.getName());
            group.setGroupIconItem(icon.toNetwork());
            injected.add(group);
        }
        return injected;
    }

    private static Map<CreativeGroupInfoPayload, Integer> extractOriginalGroupIndices(List<CreativeGroupInfoPayload> allGroups) {
        Map<CreativeGroupInfoPayload, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < allGroups.size(); i++) {
            indexMap.put(allGroups.get(i), i);
        }
        return indexMap;
    }

    private static Map<CreativeCategory, List<CreativeGroupInfoPayload>> groupByCategory(List<CreativeGroupInfoPayload> groups) {
        Map<CreativeCategory, List<CreativeGroupInfoPayload>> grouped = new EnumMap<>(CreativeCategory.class);
        for (CreativeGroupInfoPayload group : groups) {
            CreativeCategory category = CreativeCategory.valueOf(group.getCreativeCategory().name());
            grouped.computeIfAbsent(category, k -> new ArrayList<>()).add(group);
        }
        return grouped;
    }

    private static List<CreativeGroupInfoPayload> rebuildGroupsAndRemap(
        Map<CreativeCategory, List<CreativeGroupInfoPayload>> groupedVanilla,
        Map<CreativeCategory, List<CreativeGroupInfoPayload>> groupedCustom,
        Map<CreativeGroupInfoPayload, Integer> originalGroupIndices,
        Map<Integer, Integer> groupIndexMap
    ) {
        List<CreativeGroupInfoPayload> rebuilt = new ArrayList<>();
        int newIndex = 0;

        for (CreativeCategory category : CreativeCategory.values()) {
            List<CreativeGroupInfoPayload> vanilla = groupedVanilla.getOrDefault(category, Collections.emptyList());
            List<CreativeGroupInfoPayload> custom = groupedCustom.getOrDefault(category, Collections.emptyList());

            if (!vanilla.isEmpty()) {
                List<CreativeGroupInfoPayload> vanillaMain = vanilla.subList(0, vanilla.size() - 1);
                CreativeGroupInfoPayload wildcardGroup = vanilla.get(vanilla.size() - 1);

                for (CreativeGroupInfoPayload group : vanillaMain) {
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

                for (CreativeGroupInfoPayload group : custom) {
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
        ObjectLinkedOpenHashSet<CreativeItemEntryPayload> current = CreativeItemRegistry.ITEM_DATA;
        ObjectLinkedOpenHashSet<CreativeItemEntryPayload> rebuilt = new ObjectLinkedOpenHashSet<>();

        for (CreativeItemEntryPayload data : current) {
            Item item = Item.fromNetwork(data.getItemInstance());
            int originalGroupId = data.getGroupIndex();
            int newGroupId;

            String originalId = data.getItemInstance().getDefinition().getIdentifier();

            if (!CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.contains(originalId)){
                Integer mapped = groupIndexMap.get(originalGroupId);
                newGroupId = (mapped != null) ? mapped : originalGroupId;
            } else {
                // Custom item: use mapped group names from ITEM_GROUP_MAP saved on item/block registry
                String key = originalId;
                String groupName = CreativeItemRegistry.ITEM_GROUP_MAP.get(key);
                CreativeCategory fallbackCategory = getCategoryFromFallbackIndex(originalGroupId);
                boolean noGroup = groupName == null || groupName.isBlank() || "NONE".equalsIgnoreCase(groupName);

                if (!noGroup) {
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
                        originalId, fallbackIndex);
                    newGroupId = fallbackIndex;
                }
            }

            CreativeItemEntryPayload rebuiltData;

            if (originalId.endsWith("_spawn_egg")) {
                final CreativeItemEntryPayload payload = new CreativeItemEntryPayload();
                payload.setCreativeNetId(data.getCreativeNetId());
                payload.setItemInstance(item.toCreativeNetwork());
                payload.setGroupIndex(newGroupId);
                rebuiltData = payload;
            } else {
                final CreativeItemEntryPayload payload = new CreativeItemEntryPayload();
                payload.setCreativeNetId(data.getCreativeNetId());
                payload.setItemInstance(data.getItemInstance());
                payload.setGroupIndex(newGroupId);
                rebuiltData = payload;
            }

            rebuilt.add(rebuiltData);
        }

        current.clear();
        current.addAll(rebuilt);
        CreativeItemRegistry.ITEM_GROUP_MAP.clear();
    }

    public static CreativeCategory getCategoryFromFallbackIndex(int groupId) {
        if (groupId == CreativeItemRegistry.LAST_CONSTRUCTION_INDEX) return CreativeCategory.CONSTRUCTION;
        if (groupId == CreativeItemRegistry.LAST_EQUIPMENTS_INDEX) return CreativeCategory.EQUIPMENT;
        if (groupId == CreativeItemRegistry.LAST_ITEMS_INDEX) return CreativeCategory.ITEMS;
        if (groupId == CreativeItemRegistry.LAST_NATURE_INDEX) return CreativeCategory.NATURE;
        return CreativeCategory.ITEMS;
    }
}
