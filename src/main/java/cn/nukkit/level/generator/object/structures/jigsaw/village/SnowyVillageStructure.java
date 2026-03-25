package cn.nukkit.level.generator.object.structures.jigsaw.village;

import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
public class SnowyVillageStructure extends VillageStructure {

    @Override
    protected RandomizableContainer resolveVillageChestLoot(String structureName) {
        RandomizableContainer loot = super.resolveVillageChestLoot(structureName);
        if (loot != null) {
            return loot;
        }
        return isGenericVillageHouse(structureName, "snowy") ? VillageChestLoot.SNOWY_HOUSE : null;
    }

    private static final StructurePoolCollection COLLECTION;

    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    public String getEntryPool() {
        return "village/snowy/town_centers";
    }

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put("village/snowy/town_centers", pool(
                "village/snowy/town_centers",
                entry("village/snowy/town_centers/snowy_meeting_point_1", 100),
                entry("village/snowy/town_centers/snowy_meeting_point_2", 50),
                entry("village/snowy/town_centers/snowy_meeting_point_3", 150),
                entry("village/snowy/zombie/town_centers/snowy_meeting_point_1", 2),
                entry("village/snowy/zombie/town_centers/snowy_meeting_point_2", 1),
                entry("village/snowy/zombie/town_centers/snowy_meeting_point_3", 3)
        ));

        COLLECTION.put("village/snowy/streets", pool(
                "village/snowy/streets",
                entry("village/snowy/streets/corner_01", 2),
                entry("village/snowy/streets/corner_02", 2),
                entry("village/snowy/streets/corner_03", 2),
                entry("village/snowy/streets/square_01", 2),
                entry("village/snowy/streets/straight_01", 4),
                entry("village/snowy/streets/straight_02", 4),
                entry("village/snowy/streets/straight_03", 4),
                entry("village/snowy/streets/straight_04", 7),
                entry("village/snowy/streets/straight_06", 4),
                entry("village/snowy/streets/straight_08", 4),
                entry("village/snowy/streets/crossroad_02", 1),
                entry("village/snowy/streets/crossroad_03", 2),
                entry("village/snowy/streets/crossroad_04", 2),
                entry("village/snowy/streets/crossroad_05", 2),
                entry("village/snowy/streets/crossroad_06", 2),
                entry("village/snowy/streets/turn_01", 3)
        ));

        COLLECTION.put("village/snowy/zombie/streets", pool(
                "village/snowy/zombie/streets",
                entry("village/snowy/zombie/streets/corner_01", 2),
                entry("village/snowy/zombie/streets/corner_02", 2),
                entry("village/snowy/zombie/streets/corner_03", 2),
                entry("village/snowy/zombie/streets/square_01", 2),
                entry("village/snowy/zombie/streets/straight_01", 4),
                entry("village/snowy/zombie/streets/straight_02", 4),
                entry("village/snowy/zombie/streets/straight_03", 4),
                entry("village/snowy/zombie/streets/straight_04", 7),
                entry("village/snowy/zombie/streets/straight_06", 4),
                entry("village/snowy/zombie/streets/straight_08", 4),
                entry("village/snowy/zombie/streets/crossroad_02", 1),
                entry("village/snowy/zombie/streets/crossroad_03", 2),
                entry("village/snowy/zombie/streets/crossroad_04", 2),
                entry("village/snowy/zombie/streets/crossroad_05", 2),
                entry("village/snowy/zombie/streets/crossroad_06", 2),
                entry("village/snowy/zombie/streets/turn_01", 3)
        ));

        COLLECTION.put("village/snowy/houses", pool(
                "village/snowy/houses",
                entry("village/snowy/houses/snowy_small_house_1", 2),
                entry("village/snowy/houses/snowy_small_house_2", 2),
                entry("village/snowy/houses/snowy_small_house_3", 2),
                entry("village/snowy/houses/snowy_small_house_4", 3),
                entry("village/snowy/houses/snowy_small_house_5", 2),
                entry("village/snowy/houses/snowy_small_house_6", 2),
                entry("village/snowy/houses/snowy_small_house_7", 2),
                entry("village/snowy/houses/snowy_small_house_8", 2),
                entry("village/snowy/houses/snowy_medium_house_1", 2),
                entry("village/snowy/houses/snowy_medium_house_2", 2),
                entry("village/snowy/houses/snowy_medium_house_3", 2),
                entry("village/snowy/houses/snowy_butchers_shop_1", 2),
                entry("village/snowy/houses/snowy_butchers_shop_2", 2),
                entry("village/snowy/houses/snowy_tool_smith_1", 2),
                entry("village/snowy/houses/snowy_fletcher_house_1", 2),
                entry("village/snowy/houses/snowy_shepherds_house_1", 3),
                entry("village/snowy/houses/snowy_armorer_house_1", 1),
                entry("village/snowy/houses/snowy_armorer_house_2", 1),
                entry("village/snowy/houses/snowy_fisher_cottage", 2),
                entry("village/snowy/houses/snowy_tannery_1", 2),
                entry("village/snowy/houses/snowy_cartographer_house_1", 2),
                entry("village/snowy/houses/snowy_library_1", 2),
                entry("village/snowy/houses/snowy_masons_house_1", 2),
                entry("village/snowy/houses/snowy_masons_house_2", 2),
                entry("village/snowy/houses/snowy_weapon_smith_1", 2),
                entry("village/snowy/houses/snowy_temple_1", 2),
                entry("village/snowy/houses/snowy_farm_1", 3),
                entry("village/snowy/houses/snowy_farm_2", 3),
                entry("village/snowy/houses/snowy_animal_pen_1", 2),
                entry("village/snowy/houses/snowy_animal_pen_2", 2)
        ));

        COLLECTION.put("village/snowy/zombie/houses", pool(
                "village/snowy/zombie/houses",
                entry("village/snowy/zombie/houses/snowy_small_house_1", 2),
                entry("village/snowy/zombie/houses/snowy_small_house_2", 2),
                entry("village/snowy/zombie/houses/snowy_small_house_3", 2),
                entry("village/snowy/zombie/houses/snowy_small_house_4", 2),
                entry("village/snowy/zombie/houses/snowy_small_house_5", 2),
                entry("village/snowy/zombie/houses/snowy_small_house_6", 2),
                entry("village/snowy/zombie/houses/snowy_small_house_7", 2),
                entry("village/snowy/zombie/houses/snowy_small_house_8", 2),
                entry("village/snowy/zombie/houses/snowy_medium_house_1", 2),
                entry("village/snowy/zombie/houses/snowy_medium_house_2", 2),
                entry("village/snowy/zombie/houses/snowy_medium_house_3", 1),
                entry("village/snowy/houses/snowy_butchers_shop_1", 2),
                entry("village/snowy/houses/snowy_butchers_shop_2", 2),
                entry("village/snowy/houses/snowy_tool_smith_1", 2),
                entry("village/snowy/houses/snowy_fletcher_house_1", 2),
                entry("village/snowy/houses/snowy_shepherds_house_1", 2),
                entry("village/snowy/houses/snowy_armorer_house_1", 1),
                entry("village/snowy/houses/snowy_armorer_house_2", 1),
                entry("village/snowy/houses/snowy_fisher_cottage", 2),
                entry("village/snowy/houses/snowy_tannery_1", 2),
                entry("village/snowy/houses/snowy_cartographer_house_1", 2),
                entry("village/snowy/houses/snowy_library_1", 2),
                entry("village/snowy/houses/snowy_masons_house_1", 2),
                entry("village/snowy/houses/snowy_masons_house_2", 2),
                entry("village/snowy/houses/snowy_weapon_smith_1", 2),
                entry("village/snowy/houses/snowy_temple_1", 2),
                entry("village/snowy/houses/snowy_farm_1", 3),
                entry("village/snowy/houses/snowy_farm_2", 3),
                entry("village/snowy/houses/snowy_animal_pen_1", 2),
                entry("village/snowy/houses/snowy_animal_pen_2", 2)
        ));

        COLLECTION.put("village/snowy/terminators", pool(
                "village/snowy/terminators",
                entry("village/plains/terminators/terminator_01", 1),
                entry("village/plains/terminators/terminator_02", 1),
                entry("village/plains/terminators/terminator_03", 1),
                entry("village/plains/terminators/terminator_04", 1)
        ));

        COLLECTION.put("village/snowy/decor", pool(
                "village/snowy/decor",
                entry("village/snowy/snowy_lamp_post_01", 4),
                entry("village/snowy/snowy_lamp_post_02", 4),
                entry("village/snowy/snowy_lamp_post_03", 1)
        ));

        COLLECTION.put("village/snowy/zombie/decor", pool(
                "village/snowy/zombie/decor",
                entry("village/snowy/snowy_lamp_post_01", 1),
                entry("village/snowy/snowy_lamp_post_02", 1),
                entry("village/snowy/snowy_lamp_post_03", 1)
        ));

        COLLECTION.put("village/snowy/villagers", pool(
                "village/snowy/villagers",
                entry("village/snowy/villagers/nitwit", 1),
                entry("village/snowy/villagers/baby", 1),
                entry("village/snowy/villagers/unemployed", 10)
        ));

        COLLECTION.put("village/snowy/zombie/villagers", pool(
                "village/snowy/zombie/villagers",
                entry("village/snowy/zombie/villagers/nitwit", 1),
                entry("village/snowy/zombie/villagers/unemployed", 10)
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
