package cn.nukkit.level.generator.object.structures.jigsaw.village;

import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
public class PlainsVillageStructure extends VillageStructure {

    @Override
    protected RandomizableContainer resolveVillageChestLoot(String structureName) {
        RandomizableContainer loot = super.resolveVillageChestLoot(structureName);
        if (loot != null) {
            return loot;
        }
        return isGenericVillageHouse(structureName, "plains") ? VillageChestLoot.PLAINS_HOUSE : null;
    }

    private final static StructurePoolCollection COLLECTION;

    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    public String getEntryPool() {
        return "village/plains/town_centers";
    }

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put("village/plains/town_centers", pool(
                "village/plains/town_centers",
                entry("village/plains/town_centers/plains_fountain_01", 50),
                entry("village/plains/town_centers/plains_meeting_point_1", 50),
                entry("village/plains/town_centers/plains_meeting_point_2", 50),
                entry("village/plains/town_centers/plains_meeting_point_3", 50),
                entry("village/plains/zombie/town_centers/plains_fountain_01", 1),
                entry("village/plains/zombie/town_centers/plains_meeting_point_1", 1),
                entry("village/plains/zombie/town_centers/plains_meeting_point_2", 1),
                entry("village/plains/zombie/town_centers/plains_meeting_point_3", 1)
        ));

        COLLECTION.put("village/plains/streets", pool(
                "village/plains/streets",
                entry("village/plains/streets/corner_01", 2),
                entry("village/plains/streets/corner_02", 2),
                entry("village/plains/streets/corner_03", 2),
                entry("village/plains/streets/straight_01", 4),
                entry("village/plains/streets/straight_02", 4),
                entry("village/plains/streets/straight_03", 7),
                entry("village/plains/streets/straight_04", 7),
                entry("village/plains/streets/straight_05", 3),
                entry("village/plains/streets/straight_06", 4),
                entry("village/plains/streets/crossroad_01", 2),
                entry("village/plains/streets/crossroad_02", 1),
                entry("village/plains/streets/crossroad_03", 2),
                entry("village/plains/streets/crossroad_04", 2),
                entry("village/plains/streets/crossroad_05", 2),
                entry("village/plains/streets/crossroad_06", 2),
                entry("village/plains/streets/turn_01", 3)
        ));

        COLLECTION.put("village/plains/zombie/streets", pool(
                "village/plains/zombie/streets",
                entry("village/plains/zombie/streets/corner_01", 2),
                entry("village/plains/zombie/streets/corner_02", 2),
                entry("village/plains/zombie/streets/corner_03", 2),
                entry("village/plains/zombie/streets/straight_01", 4),
                entry("village/plains/zombie/streets/straight_02", 4),
                entry("village/plains/zombie/streets/straight_03", 7),
                entry("village/plains/zombie/streets/straight_04", 7),
                entry("village/plains/zombie/streets/straight_05", 3),
                entry("village/plains/zombie/streets/straight_06", 4),
                entry("village/plains/zombie/streets/crossroad_01", 2),
                entry("village/plains/zombie/streets/crossroad_02", 1),
                entry("village/plains/zombie/streets/crossroad_03", 2),
                entry("village/plains/zombie/streets/crossroad_04", 2),
                entry("village/plains/zombie/streets/crossroad_05", 2),
                entry("village/plains/zombie/streets/crossroad_06", 2),
                entry("village/plains/zombie/streets/turn_01", 3)
        ));

        COLLECTION.put("village/plains/houses", pool(
                "village/plains/houses",
                entry("village/plains/houses/plains_small_house_1", 2),
                entry("village/plains/houses/plains_small_house_2", 2),
                entry("village/plains/houses/plains_small_house_3", 2),
                entry("village/plains/houses/plains_small_house_4", 2),
                entry("village/plains/houses/plains_small_house_5", 2),
                entry("village/plains/houses/plains_small_house_6", 1),
                entry("village/plains/houses/plains_small_house_7", 2),
                entry("village/plains/houses/plains_small_house_8", 3),
                entry("village/plains/houses/plains_medium_house_1", 2),
                entry("village/plains/houses/plains_medium_house_2", 2),
                entry("village/plains/houses/plains_big_house_1", 2),
                entry("village/plains/houses/plains_butcher_shop_1", 2),
                entry("village/plains/houses/plains_butcher_shop_2", 2),
                entry("village/plains/houses/plains_tool_smith_1", 2),
                entry("village/plains/houses/plains_fletcher_house_1", 2),
                entry("village/plains/houses/plains_shepherds_house_1", 2),
                entry("village/plains/houses/plains_armorer_house_1", 2),
                entry("village/plains/houses/plains_fisher_cottage_1", 2),
                entry("village/plains/houses/plains_tannery_1", 2),
                entry("village/plains/houses/plains_cartographer_1", 1),
                entry("village/plains/houses/plains_library_1", 5),
                entry("village/plains/houses/plains_library_2", 1),
                entry("village/plains/houses/plains_masons_house_1", 2),
                entry("village/plains/houses/plains_weaponsmith_1", 2),
                entry("village/plains/houses/plains_temple_3", 2),
                entry("village/plains/houses/plains_temple_4", 2),
                entry("village/plains/houses/plains_stable_1", 2),
                entry("village/plains/houses/plains_stable_2", 2),
                entry("village/plains/houses/plains_large_farm_1", 4),
                entry("village/plains/houses/plains_small_farm_1", 4),
                entry("village/plains/houses/plains_animal_pen_1", 1),
                entry("village/plains/houses/plains_animal_pen_2", 1),
                entry("village/plains/houses/plains_animal_pen_3", 5),
                entry("village/plains/houses/plains_accessory_1", 1),
                entry("village/plains/houses/plains_meeting_point_4", 3),
                entry("village/plains/houses/plains_meeting_point_5", 1)
        ));

        COLLECTION.put("village/plains/zombie/houses", pool(
                "village/plains/zombie/houses",
                entry("village/plains/zombie/houses/plains_small_house_1", 2),
                entry("village/plains/zombie/houses/plains_small_house_2", 2),
                entry("village/plains/zombie/houses/plains_small_house_3", 2),
                entry("village/plains/zombie/houses/plains_small_house_4", 2),
                entry("village/plains/zombie/houses/plains_small_house_5", 2),
                entry("village/plains/zombie/houses/plains_small_house_6", 1),
                entry("village/plains/zombie/houses/plains_small_house_7", 2),
                entry("village/plains/zombie/houses/plains_small_house_8", 2),
                entry("village/plains/zombie/houses/plains_medium_house_1", 2),
                entry("village/plains/zombie/houses/plains_medium_house_2", 2),
                entry("village/plains/zombie/houses/plains_big_house_1", 2),
                entry("village/plains/houses/plains_butcher_shop_1", 2),
                entry("village/plains/zombie/houses/plains_butcher_shop_2", 2),
                entry("village/plains/houses/plains_tool_smith_1", 2),
                entry("village/plains/zombie/houses/plains_fletcher_house_1", 2),
                entry("village/plains/zombie/houses/plains_shepherds_house_1", 2),
                entry("village/plains/houses/plains_armorer_house_1", 2),
                entry("village/plains/houses/plains_fisher_cottage_1", 2),
                entry("village/plains/houses/plains_tannery_1", 2),
                entry("village/plains/houses/plains_cartographer_1", 1),
                entry("village/plains/houses/plains_library_1", 3),
                entry("village/plains/houses/plains_library_2", 1),
                entry("village/plains/houses/plains_masons_house_1", 2),
                entry("village/plains/houses/plains_weaponsmith_1", 2),
                entry("village/plains/houses/plains_temple_3", 2),
                entry("village/plains/houses/plains_temple_4", 2),
                entry("village/plains/zombie/houses/plains_stable_1", 2),
                entry("village/plains/houses/plains_stable_2", 2),
                entry("village/plains/houses/plains_large_farm_1", 4),
                entry("village/plains/houses/plains_small_farm_1", 4),
                entry("village/plains/houses/plains_animal_pen_1", 1),
                entry("village/plains/houses/plains_animal_pen_2", 1),
                entry("village/plains/zombie/houses/plains_animal_pen_3", 5),
                entry("village/plains/zombie/houses/plains_meeting_point_4", 3),
                entry("village/plains/zombie/houses/plains_meeting_point_5", 1)
        ));

        COLLECTION.put("village/plains/terminators", pool(
                "village/plains/terminators",
                entry("village/plains/terminators/terminator_01", 1),
                entry("village/plains/terminators/terminator_02", 1),
                entry("village/plains/terminators/terminator_03", 1),
                entry("village/plains/terminators/terminator_04", 1)
        ));

        COLLECTION.put("village/plains/decor", pool(
                "village/plains/decor",
                entry("village/plains/plains_lamp_1", 2)
        ));

        COLLECTION.put("village/plains/zombie/decor", pool(
                "village/plains/zombie/decor",
                entry("village/plains/plains_lamp_1", 1)
        ));

        COLLECTION.put("village/plains/villagers", pool(
                "village/plains/villagers",
                entry("village/plains/villagers/nitwit", 1),
                entry("village/plains/villagers/baby", 1),
                entry("village/plains/villagers/unemployed", 10)
        ));

        COLLECTION.put("village/plains/zombie/villagers", pool(
                "village/plains/zombie/villagers",
                entry("village/plains/zombie/villagers/nitwit", 1),
                entry("village/plains/zombie/villagers/unemployed", 10)
        ));

        COLLECTION.put("village/common/animals", pool(
                "village/common/animals",
                entry("village/common/animals/cows_1", 7),
                entry("village/common/animals/pigs_1", 7),
                entry("village/common/animals/horses_1", 1),
                entry("village/common/animals/horses_2", 1),
                entry("village/common/animals/horses_3", 1),
                entry("village/common/animals/horses_4", 1),
                entry("village/common/animals/horses_5", 1),
                entry("village/common/animals/sheep_1", 1),
                entry("village/common/animals/sheep_2", 1)
        ));

        COLLECTION.put("village/common/sheep", pool(
                "village/common/sheep",
                entry("village/common/animals/sheep_1", 1),
                entry("village/common/animals/sheep_2", 1)
        ));

        COLLECTION.put("village/common/cats", pool(
                "village/common/cats",
                entry("village/common/animals/cat_black", 1),
                entry("village/common/animals/cat_british", 1),
                entry("village/common/animals/cat_calico", 1),
                entry("village/common/animals/cat_persian", 1),
                entry("village/common/animals/cat_ragdoll", 1),
                entry("village/common/animals/cat_red", 1),
                entry("village/common/animals/cat_siamese", 1),
                entry("village/common/animals/cat_tabby", 1),
                entry("village/common/animals/cat_white", 1),
                entry("village/common/animals/cat_jellie", 1)
        ));

        COLLECTION.put("village/common/butcher_animals", pool(
                "village/common/butcher_animals",
                entry("village/common/animals/cows_1", 3),
                entry("village/common/animals/pigs_1", 3),
                entry("village/common/animals/sheep_1", 1),
                entry("village/common/animals/sheep_2", 1)
        ));

        COLLECTION.put("village/common/iron_golem", pool(
                "village/common/iron_golem",
                entry("village/common/iron_golem", 1)
        ));

        COLLECTION.put("village/common/well_bottoms", pool(
                "village/common/well_bottoms",
                entry("village/common/well_bottom", 1)
        ));
    }

    private static StructurePool pool(String name, StructurePool.Entry... entries) {
        return new StructurePool(name, entries);
    }

    private static StructurePool.Entry entry(String structureName, int weight) {
        return new StructurePool.Entry(structureName, weight);
    }
}
