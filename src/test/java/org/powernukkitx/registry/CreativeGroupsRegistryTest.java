package org.powernukkitx.registry;

import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.cloudburstmc.protocol.bedrock.data.payload.creative.CreativeGroupInfoPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.creative.CreativeItemCategory;
import org.cloudburstmc.protocol.bedrock.data.payload.creative.CreativeItemEntryPayload;
import org.cloudburstmc.protocol.bedrock.data.payload.creative.CreativeItemNetId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.network.protocol.types.inventory.creative.CreativeCustomGroups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreativeGroupsRegistryTest {

    private static final String GROUP_NAME = "itemGroup.name.test_backrooms_blocks";
    private static final int CUSTOM_BLOCK_COUNT = 35;

    private static List<CreativeGroupInfoPayload> groupsSnapshot;
    private static List<CreativeItemEntryPayload> itemDataSnapshot;
    private static Map<CreativeCategory, Map<String, Integer>> categoryIndexSnapshot;
    private static Map<String, String> itemGroupSnapshot;
    private static HashSet<String> customIdentifiersSnapshot;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.ITEM.init();
        Registries.CREATIVE.init();
    }

    @BeforeEach
    void snapshot() {
        groupsSnapshot = new ArrayList<>(CreativeItemRegistry.GROUPS);
        itemDataSnapshot = new ArrayList<>(CreativeItemRegistry.ITEM_DATA);
        categoryIndexSnapshot = new HashMap<>();
        for (var e : CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP.entrySet()) {
            categoryIndexSnapshot.put(e.getKey(), new HashMap<>(e.getValue()));
        }
        itemGroupSnapshot = new HashMap<>(CreativeItemRegistry.ITEM_GROUP_MAP);
        customIdentifiersSnapshot = new HashSet<>(CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS);
    }

    @AfterEach
    void restore() {
        CreativeItemRegistry.GROUPS.clear();
        CreativeItemRegistry.GROUPS.addAll(groupsSnapshot);

        CreativeItemRegistry.ITEM_DATA.clear();
        CreativeItemRegistry.ITEM_DATA.addAll(itemDataSnapshot);

        CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP.clear();
        CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP.putAll(categoryIndexSnapshot);

        CreativeItemRegistry.ITEM_GROUP_MAP.clear();
        CreativeItemRegistry.ITEM_GROUP_MAP.putAll(itemGroupSnapshot);

        CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.clear();
        CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.addAll(customIdentifiersSnapshot);
    }

    private static void addCustomBlock(String id) {
        CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.add(id);
        CreativeItemRegistry.ITEM_GROUP_MAP.put(id, GROUP_NAME);

        ItemDefinition def = new SimpleItemDefinition(id, 40000 + CreativeItemRegistry.ITEM_DATA.size(),
            ItemVersion.NONE, false, null);
        ItemData data = ItemData.builder().definition(def).damage(0).count(1).build();

        CreativeItemEntryPayload payload = new CreativeItemEntryPayload();
        payload.setCreativeNetId(new CreativeItemNetId(CreativeItemRegistry.ITEM_DATA.size()));
        payload.setItemInstance(data);
        payload.setGroupIndex(CreativeItemRegistry.LAST_CONSTRUCTION_INDEX);
        CreativeItemRegistry.ITEM_DATA.add(payload);
    }

    private static int countAtGroup(int groupIndex) {
        int n = 0;
        for (CreativeItemEntryPayload data : CreativeItemRegistry.ITEM_DATA) {
            if (data.getGroupIndex() == groupIndex) n++;
        }
        return n;
    }

    private static String iconId() {
        return CreativeItemRegistry.ITEM_DATA.iterator().next()
            .getItemInstance().getDefinition().getIdentifier();
    }

    @Test
    void customGroupBuiltAndPopulatedWithoutManualRegister() {
        int totalBefore = CreativeItemRegistry.ITEM_DATA.size();

        List<String> vanillaTailIds = new ArrayList<>();
        for (CreativeItemEntryPayload data : CreativeItemRegistry.ITEM_DATA) {
            String id = data.getItemInstance().getDefinition().getIdentifier();
            if (data.getGroupIndex() == CreativeItemRegistry.LAST_CONSTRUCTION_INDEX
                && !CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.contains(id)) {
                vanillaTailIds.add(id);
            }
        }

        CreativeCustomGroups.define(CreativeItemCategory.CONSTRUCTION, GROUP_NAME, iconId());
        List<String> customIds = new ArrayList<>();
        for (int i = 0; i < CUSTOM_BLOCK_COUNT; i++) {
            String id = "test:backrooms_" + i;
            customIds.add(id);
            addCustomBlock(id);
        }

        CreativeGroupsRegistry.register();

        Integer customIdx = CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP
            .getOrDefault(CreativeCategory.CONSTRUCTION, Map.of()).get(GROUP_NAME);
        assertNotNull(customIdx, "custom group should be registered in the construction index map");

        assertEquals(CUSTOM_BLOCK_COUNT, countAtGroup(customIdx),
            "custom group must contain exactly the plugin's blocks");

        Map<String, Integer> byId = new HashMap<>();
        for (CreativeItemEntryPayload data : CreativeItemRegistry.ITEM_DATA) {
            byId.put(data.getItemInstance().getDefinition().getIdentifier(), data.getGroupIndex());
        }
        for (String id : customIds) {
            assertEquals(customIdx, byId.get(id), "custom block " + id + " should be in the custom group");
        }

        for (String id : vanillaTailIds) {
            assertNotEquals(customIdx, byId.get(id),
                "vanilla tail item " + id + " must not land in the injected custom group");
        }

        assertEquals(totalBefore + CUSTOM_BLOCK_COUNT, CreativeItemRegistry.ITEM_DATA.size());
    }

    @Test
    void vanillaTailItemIgnoresStaleGroupName() {
        String victim = null;
        for (CreativeItemEntryPayload data : CreativeItemRegistry.ITEM_DATA) {
            String id = data.getItemInstance().getDefinition().getIdentifier();
            if (data.getGroupIndex() == CreativeItemRegistry.LAST_CONSTRUCTION_INDEX
                && !CreativeItemRegistry.CUSTOM_ITEM_IDENTIFIERS.contains(id)) {
                victim = id;
                break;
            }
        }
        assertNotNull(victim, "expected at least one vanilla construction-tail item in the dataset");

        CreativeCustomGroups.define(CreativeItemCategory.CONSTRUCTION, GROUP_NAME, iconId());
        addCustomBlock("test:backrooms_probe");
        CreativeItemRegistry.ITEM_GROUP_MAP.put(victim, GROUP_NAME);

        CreativeGroupsRegistry.register();

        Integer customIdx = CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP
            .getOrDefault(CreativeCategory.CONSTRUCTION, Map.of()).get(GROUP_NAME);
        assertNotNull(customIdx);

        Integer victimIdx = null;
        for (CreativeItemEntryPayload data : CreativeItemRegistry.ITEM_DATA) {
            if (victim.equals(data.getItemInstance().getDefinition().getIdentifier())) {
                victimIdx = data.getGroupIndex();
                break;
            }
        }
        assertNotEquals(customIdx, victimIdx,
            "vanilla tail item must not be pulled into the custom group by a stale group name");
    }

    @Test
    void registerIsIdempotent() {
        CreativeCustomGroups.define(CreativeItemCategory.CONSTRUCTION, GROUP_NAME, iconId());
        addCustomBlock("test:backrooms_idem");

        CreativeGroupsRegistry.register();
        int groupsAfterFirst = CreativeItemRegistry.GROUPS.size();
        int itemsAfterFirst = CreativeItemRegistry.ITEM_DATA.size();
        Integer idxAfterFirst = CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP
            .getOrDefault(CreativeCategory.CONSTRUCTION, Map.of()).get(GROUP_NAME);

        CreativeGroupsRegistry.register();

        assertEquals(groupsAfterFirst, CreativeItemRegistry.GROUPS.size(), "no duplicate group on re-register");
        assertEquals(itemsAfterFirst, CreativeItemRegistry.ITEM_DATA.size(), "item count stable on re-register");
        assertEquals(idxAfterFirst, CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP
            .getOrDefault(CreativeCategory.CONSTRUCTION, Map.of()).get(GROUP_NAME), "custom group index stable");
    }
}
