package cn.nukkit.level.generator.object.structures.jigsaw.village;

import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;

public class TaigaVillageStructure extends VillageStructure {

    @Override
    protected RandomizableContainer resolveVillageChestLoot(String structureName) {
        RandomizableContainer loot = super.resolveVillageChestLoot(structureName);
        if (loot != null) {
            return loot;
        }
        return isGenericVillageHouse(structureName, "taiga") ? VillageChestLoot.TAIGA_HOUSE : null;
    }

    @Override
    protected int getLampHeightOffset(String structureName) {
        return structureName.contains("taiga_lamp_post") ? 1 : 0;
    }

    private static final StructurePoolCollection COLLECTION;

    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    public String getEntryPool() {
        return "village/taiga/town_centers";
    }

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put("village/taiga/town_centers", pool(
                "village/taiga/town_centers",
                entry("village/taiga/town_centers/taiga_meeting_point_1", 49),
                entry("village/taiga/town_centers/taiga_meeting_point_2", 49),
                entry("village/taiga/zombie/town_centers/taiga_meeting_point_1", 1),
                entry("village/taiga/zombie/town_centers/taiga_meeting_point_2", 1)
        ));

        COLLECTION.put("village/taiga/streets", pool(
                "village/taiga/streets",
                entry("village/taiga/streets/corner_01", 2),
                entry("village/taiga/streets/corner_02", 2),
                entry("village/taiga/streets/corner_03", 2),
                entry("village/taiga/streets/straight_01", 4),
                entry("village/taiga/streets/straight_02", 4),
                entry("village/taiga/streets/straight_03", 4),
                entry("village/taiga/streets/straight_04", 7),
                entry("village/taiga/streets/straight_05", 7),
                entry("village/taiga/streets/straight_06", 4),
                entry("village/taiga/streets/crossroad_01", 1),
                entry("village/taiga/streets/crossroad_02", 1),
                entry("village/taiga/streets/crossroad_03", 2),
                entry("village/taiga/streets/crossroad_04", 2),
                entry("village/taiga/streets/crossroad_05", 2),
                entry("village/taiga/streets/crossroad_06", 2),
                entry("village/taiga/streets/turn_01", 3)
        ));

        COLLECTION.put("village/taiga/zombie/streets", pool(
                "village/taiga/zombie/streets",
                entry("village/taiga/zombie/streets/corner_01", 2),
                entry("village/taiga/zombie/streets/corner_02", 2),
                entry("village/taiga/zombie/streets/corner_03", 2),
                entry("village/taiga/zombie/streets/straight_01", 4),
                entry("village/taiga/zombie/streets/straight_02", 4),
                entry("village/taiga/zombie/streets/straight_03", 4),
                entry("village/taiga/zombie/streets/straight_04", 7),
                entry("village/taiga/zombie/streets/straight_05", 7),
                entry("village/taiga/zombie/streets/straight_06", 4),
                entry("village/taiga/zombie/streets/crossroad_01", 1),
                entry("village/taiga/zombie/streets/crossroad_02", 1),
                entry("village/taiga/zombie/streets/crossroad_03", 2),
                entry("village/taiga/zombie/streets/crossroad_04", 2),
                entry("village/taiga/zombie/streets/crossroad_05", 2),
                entry("village/taiga/zombie/streets/crossroad_06", 2),
                entry("village/taiga/zombie/streets/turn_01", 3)
        ));

        COLLECTION.put("village/taiga/houses", pool(
                "village/taiga/houses",
                entry("village/taiga/houses/taiga_small_house_1", 4),
                entry("village/taiga/houses/taiga_small_house_2", 4),
                entry("village/taiga/houses/taiga_small_house_3", 4),
                entry("village/taiga/houses/taiga_small_house_4", 4),
                entry("village/taiga/houses/taiga_small_house_5", 4),
                entry("village/taiga/houses/taiga_medium_house_1", 2),
                entry("village/taiga/houses/taiga_medium_house_2", 2),
                entry("village/taiga/houses/taiga_medium_house_3", 2),
                entry("village/taiga/houses/taiga_medium_house_4", 2),
                entry("village/taiga/houses/taiga_butcher_shop_1", 2),
                entry("village/taiga/houses/taiga_tool_smith_1", 2),
                entry("village/taiga/houses/taiga_fletcher_house_1", 2),
                entry("village/taiga/houses/taiga_shepherds_house_1", 2),
                entry("village/taiga/houses/taiga_armorer_house_1", 1),
                entry("village/taiga/houses/taiga_armorer_2", 1),
                entry("village/taiga/houses/taiga_fisher_cottage_1", 3),
                entry("village/taiga/houses/taiga_tannery_1", 2),
                entry("village/taiga/houses/taiga_cartographer_house_1", 2),
                entry("village/taiga/houses/taiga_library_1", 2),
                entry("village/taiga/houses/taiga_masons_house_1", 2),
                entry("village/taiga/houses/taiga_weaponsmith_1", 2),
                entry("village/taiga/houses/taiga_weaponsmith_2", 2),
                entry("village/taiga/houses/taiga_temple_1", 2),
                entry("village/taiga/houses/taiga_large_farm_1", 6),
                entry("village/taiga/houses/taiga_large_farm_2", 6),
                entry("village/taiga/houses/taiga_small_farm_1", 1),
                entry("village/taiga/houses/taiga_animal_pen_1", 2)
        ));

        COLLECTION.put("village/taiga/zombie/houses", pool(
                "village/taiga/zombie/houses",
                entry("village/taiga/zombie/houses/taiga_small_house_1", 4),
                entry("village/taiga/zombie/houses/taiga_small_house_2", 4),
                entry("village/taiga/zombie/houses/taiga_small_house_3", 4),
                entry("village/taiga/zombie/houses/taiga_small_house_4", 4),
                entry("village/taiga/zombie/houses/taiga_small_house_5", 4),
                entry("village/taiga/zombie/houses/taiga_medium_house_1", 2),
                entry("village/taiga/zombie/houses/taiga_medium_house_2", 2),
                entry("village/taiga/zombie/houses/taiga_medium_house_3", 2),
                entry("village/taiga/zombie/houses/taiga_medium_house_4", 2),
                entry("village/taiga/houses/taiga_butcher_shop_1", 2),
                entry("village/taiga/zombie/houses/taiga_tool_smith_1", 2),
                entry("village/taiga/houses/taiga_fletcher_house_1", 2),
                entry("village/taiga/zombie/houses/taiga_shepherds_house_1", 2),
                entry("village/taiga/houses/taiga_armorer_house_1", 1),
                entry("village/taiga/zombie/houses/taiga_fisher_cottage_1", 2),
                entry("village/taiga/houses/taiga_tannery_1", 2),
                entry("village/taiga/zombie/houses/taiga_cartographer_house_1", 2),
                entry("village/taiga/zombie/houses/taiga_library_1", 2),
                entry("village/taiga/houses/taiga_masons_house_1", 2),
                entry("village/taiga/houses/taiga_weaponsmith_1", 2),
                entry("village/taiga/zombie/houses/taiga_weaponsmith_2", 2),
                entry("village/taiga/zombie/houses/taiga_temple_1", 2),
                entry("village/taiga/houses/taiga_large_farm_1", 6),
                entry("village/taiga/zombie/houses/taiga_large_farm_2", 6),
                entry("village/taiga/houses/taiga_small_farm_1", 1),
                entry("village/taiga/houses/taiga_animal_pen_1", 2)
        ));

        COLLECTION.put("village/taiga/terminators", pool(
                "village/taiga/terminators",
                entry("village/plains/terminators/terminator_01", 1),
                entry("village/plains/terminators/terminator_02", 1),
                entry("village/plains/terminators/terminator_03", 1),
                entry("village/plains/terminators/terminator_04", 1)
        ));

        COLLECTION.put("village/taiga/decor", pool(
                "village/taiga/decor",
                entry("village/taiga/taiga_lamp_post_1", 10),
                entry("village/taiga/taiga_decoration_1", 4),
                entry("village/taiga/taiga_decoration_2", 1),
                entry("village/taiga/taiga_decoration_3", 1),
                entry("village/taiga/taiga_decoration_4", 1),
                entry("village/taiga/taiga_decoration_5", 2),
                entry("village/taiga/taiga_decoration_6", 1)
        ));

        COLLECTION.put("village/taiga/zombie/decor", pool(
                "village/taiga/zombie/decor",
                entry("village/taiga/taiga_decoration_1", 4),
                entry("village/taiga/taiga_decoration_2", 1),
                entry("village/taiga/taiga_decoration_3", 1),
                entry("village/taiga/taiga_decoration_4", 1)
        ));

        COLLECTION.put("village/taiga/villagers", pool(
                "village/taiga/villagers",
                entry("village/taiga/villagers/nitwit", 1),
                entry("village/taiga/villagers/baby", 1),
                entry("village/taiga/villagers/unemployed", 10)
        ));

        COLLECTION.put("village/taiga/zombie/villagers", pool(
                "village/taiga/zombie/villagers",
                entry("village/taiga/zombie/villagers/nitwit", 1),
                entry("village/taiga/zombie/villagers/unemployed", 10)
        ));
    }

    private static StructurePool pool(String name, StructurePool.Entry... entries) {
        return new StructurePool(name, entries);
    }

    private static StructurePool.Entry entry(String structureName, int weight) {
        return new StructurePool.Entry(structureName, weight);
    }
}
