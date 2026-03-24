package cn.nukkit.level.generator.object.structures.jigsaw.village;

import cn.nukkit.block.BlockSandstone;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;

public class DesertVillageStructure extends VillageStructure {

    @Override
    protected RandomizableContainer resolveVillageChestLoot(String structureName) {
        RandomizableContainer loot = super.resolveVillageChestLoot(structureName);
        if (loot != null) {
            return loot;
        }
        return isGenericVillageHouse(structureName, "desert") ? VillageChestLoot.DESERT_HOUSE : null;
    }

    @Override
    protected boolean shouldShiftHousesToTerrain() {
        return true;
    }

    @Override
    protected BlockState getHouseSupportState() {
        return BlockSandstone.PROPERTIES.getDefaultState();
    }

    @Override
    protected int getLampHeightOffset(String structureName) {
        return structureName.contains("desert_lamp") ? 1 : 0;
    }

    private static final StructurePoolCollection COLLECTION;

    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    public String getEntryPool() {
        return "village/desert/town_centers";
    }

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put("village/desert/town_centers", pool(
                "village/desert/town_centers",
                entry("village/desert/town_centers/desert_meeting_point_1", 98),
                entry("village/desert/town_centers/desert_meeting_point_2", 98),
                entry("village/desert/town_centers/desert_meeting_point_3", 49),
                entry("village/desert/zombie/town_centers/desert_meeting_point_1", 2),
                entry("village/desert/zombie/town_centers/desert_meeting_point_2", 2),
                entry("village/desert/zombie/town_centers/desert_meeting_point_3", 1)
        ));

        COLLECTION.put("village/desert/streets", pool(
                "village/desert/streets",
                entry("village/desert/streets/corner_01", 3),
                entry("village/desert/streets/corner_02", 3),
                entry("village/desert/streets/straight_01", 4),
                entry("village/desert/streets/straight_02", 4),
                entry("village/desert/streets/straight_03", 3),
                entry("village/desert/streets/crossroad_01", 3),
                entry("village/desert/streets/crossroad_02", 3),
                entry("village/desert/streets/crossroad_03", 3),
                entry("village/desert/streets/square_01", 3),
                entry("village/desert/streets/square_02", 3),
                entry("village/desert/streets/turn_01", 3)
        ));

        COLLECTION.put("village/desert/zombie/streets", pool(
                "village/desert/zombie/streets",
                entry("village/desert/zombie/streets/corner_01", 3),
                entry("village/desert/zombie/streets/corner_02", 3),
                entry("village/desert/zombie/streets/straight_01", 4),
                entry("village/desert/zombie/streets/straight_02", 4),
                entry("village/desert/zombie/streets/straight_03", 3),
                entry("village/desert/zombie/streets/crossroad_01", 3),
                entry("village/desert/zombie/streets/crossroad_02", 3),
                entry("village/desert/zombie/streets/crossroad_03", 3),
                entry("village/desert/zombie/streets/square_01", 3),
                entry("village/desert/zombie/streets/square_02", 3),
                entry("village/desert/zombie/streets/turn_01", 3)
        ));

        COLLECTION.put("village/desert/houses", pool(
                "village/desert/houses",
                entry("village/desert/houses/desert_small_house_1", 2),
                entry("village/desert/houses/desert_small_house_2", 2),
                entry("village/desert/houses/desert_small_house_3", 2),
                entry("village/desert/houses/desert_small_house_4", 2),
                entry("village/desert/houses/desert_small_house_5", 2),
                entry("village/desert/houses/desert_small_house_6", 1),
                entry("village/desert/houses/desert_small_house_7", 2),
                entry("village/desert/houses/desert_small_house_8", 2),
                entry("village/desert/houses/desert_medium_house_1", 2),
                entry("village/desert/houses/desert_medium_house_2", 2),
                entry("village/desert/houses/desert_butcher_shop_1", 2),
                entry("village/desert/houses/desert_tool_smith_1", 2),
                entry("village/desert/houses/desert_fletcher_house_1", 2),
                entry("village/desert/houses/desert_shepherd_house_1", 2),
                entry("village/desert/houses/desert_armorer_1", 1),
                entry("village/desert/houses/desert_fisher_1", 2),
                entry("village/desert/houses/desert_tannery_1", 2),
                entry("village/desert/houses/desert_cartographer_house_1", 2),
                entry("village/desert/houses/desert_library_1", 2),
                entry("village/desert/houses/desert_mason_1", 2),
                entry("village/desert/houses/desert_weaponsmith_1", 2),
                entry("village/desert/houses/desert_temple_1", 2),
                entry("village/desert/houses/desert_temple_2", 2),
                entry("village/desert/houses/desert_large_farm_1", 11),
                entry("village/desert/houses/desert_farm_1", 4),
                entry("village/desert/houses/desert_farm_2", 4),
                entry("village/desert/houses/desert_animal_pen_1", 2),
                entry("village/desert/houses/desert_animal_pen_2", 2)
        ));

        COLLECTION.put("village/desert/zombie/houses", pool(
                "village/desert/zombie/houses",
                entry("village/desert/zombie/houses/desert_small_house_1", 2),
                entry("village/desert/zombie/houses/desert_small_house_2", 2),
                entry("village/desert/zombie/houses/desert_small_house_3", 2),
                entry("village/desert/zombie/houses/desert_small_house_4", 2),
                entry("village/desert/zombie/houses/desert_small_house_5", 2),
                entry("village/desert/zombie/houses/desert_small_house_6", 1),
                entry("village/desert/zombie/houses/desert_small_house_7", 2),
                entry("village/desert/zombie/houses/desert_small_house_8", 2),
                entry("village/desert/zombie/houses/desert_medium_house_1", 2),
                entry("village/desert/zombie/houses/desert_medium_house_2", 2),
                entry("village/desert/houses/desert_butcher_shop_1", 2),
                entry("village/desert/houses/desert_tool_smith_1", 2),
                entry("village/desert/houses/desert_fletcher_house_1", 2),
                entry("village/desert/houses/desert_shepherd_house_1", 2),
                entry("village/desert/houses/desert_armorer_1", 1),
                entry("village/desert/houses/desert_fisher_1", 2),
                entry("village/desert/houses/desert_tannery_1", 2),
                entry("village/desert/houses/desert_cartographer_house_1", 2),
                entry("village/desert/houses/desert_library_1", 2),
                entry("village/desert/houses/desert_mason_1", 2),
                entry("village/desert/houses/desert_weaponsmith_1", 2),
                entry("village/desert/houses/desert_temple_1", 2),
                entry("village/desert/houses/desert_temple_2", 2),
                entry("village/desert/houses/desert_large_farm_1", 7),
                entry("village/desert/houses/desert_farm_1", 4),
                entry("village/desert/houses/desert_farm_2", 4),
                entry("village/desert/houses/desert_animal_pen_1", 2),
                entry("village/desert/houses/desert_animal_pen_2", 2)
        ));

        COLLECTION.put("village/desert/terminators", pool(
                "village/desert/terminators",
                entry("village/desert/terminators/terminator_01", 1),
                entry("village/desert/terminators/terminator_02", 1)
        ));

        COLLECTION.put("village/desert/zombie/terminators", pool(
                "village/desert/zombie/terminators",
                entry("village/desert/terminators/terminator_01", 1),
                entry("village/desert/zombie/terminators/terminator_02", 1)
        ));

        COLLECTION.put("village/desert/decor", pool(
                "village/desert/decor",
                entry("village/desert/desert_lamp_1", 10)
        ));

        COLLECTION.put("village/desert/zombie/decor", pool(
                "village/desert/zombie/decor",
                entry("village/desert/desert_lamp_1", 10)
        ));

        COLLECTION.put("village/desert/villagers", pool(
                "village/desert/villagers",
                entry("village/desert/villagers/nitwit", 1),
                entry("village/desert/villagers/baby", 1),
                entry("village/desert/villagers/unemployed", 10)
        ));

        COLLECTION.put("village/desert/camel", pool(
                "village/desert/camel",
                entry("village/desert/camel_spawn", 1)
        ));

        COLLECTION.put("village/desert/zombie/villagers", pool(
                "village/desert/zombie/villagers",
                entry("village/desert/zombie/villagers/nitwit", 1),
                entry("village/desert/zombie/villagers/unemployed", 10)
        ));
    }

    private static StructurePool pool(String name, StructurePool.Entry... entries) {
        return new StructurePool(name, entries);
    }

    private static StructurePool.Entry entry(String structureName, int weight) {
        return new StructurePool.Entry(structureName, weight);
    }
}
