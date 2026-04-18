package cn.nukkit.level.generator.object.structures.jigsaw.village;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockJigsaw;
import cn.nukkit.block.BlockStandingBanner;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import cn.nukkit.utils.DyeColor;
import org.cloudburstmc.nbt.NbtMap;

import java.util.ArrayList;
import java.util.List;

public class SavannaVillageStructure extends VillageStructure {

    @Override
    protected RandomizableContainer resolveVillageChestLoot(String structureName) {
        RandomizableContainer loot = super.resolveVillageChestLoot(structureName);
        if (loot != null) {
            return loot;
        }
        return isGenericVillageHouse(structureName, "savanna") ? VillageChestLoot.SAVANNA_HOUSE : null;
    }

    @Override
    protected int getLampHeightOffset(String structureName) {
        return structureName.contains("savanna_lamp_post") ? 1 : 0;
    }

    @Override
    protected void postProcessStructure(StructureHelper helper) {
        List<Block> placedBlocks = new ArrayList<>(helper.getBlocks());
        helper.applySubChunkUpdate();

        Level level = helper.getLevel();
        placedBlocks.stream()
                .filter(BlockUnknown.class::isInstance)
                .forEach(block -> level.setBlock(block, BlockAir.STATE.toBlock(block), true, true));

        recolorSavannaBanners(level, placedBlocks);

        for (Block block : placedBlocks) {
            if (!(level.getBlock(block) instanceof BlockJigsaw)) {
                continue;
            }
            level.setBlock(block, BlockAir.STATE.toBlock(block), true, true);
            Entity villager = Entity.createEntity(
                    Entity.VILLAGER_V2,
                    new Position(block.getFloorX() + 0.5, block.getFloorY(), block.getFloorZ() + 0.5, level)
            );
            if (villager != null) {
                villager.spawnToAll();
            }
        }
    }

    private void recolorSavannaBanners(Level level, List<Block> placedBlocks) {
        for (Block block : placedBlocks) {
            Block levelBlock = level.getBlock(block);
            if (!(levelBlock instanceof BlockStandingBanner bannerBlock)) {
                continue;
            }
            BlockEntityBanner banner = bannerBlock.getBlockEntity();
            if (banner == null) {
                banner = bannerBlock.createBlockEntity(NbtMap.builder().putInt("Base", DyeColor.BROWN.getDyeData() & 0x0f).build());
            }
            banner.setBaseColor(DyeColor.BROWN);
            banner.spawnToAll();
        }
    }

    private static final StructurePoolCollection COLLECTION;

    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    public String getEntryPool() {
        return "village/savanna/town_centers";
    }

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put("village/savanna/town_centers", pool(
                "village/savanna/town_centers",
                entry("village/savanna/town_centers/savanna_meeting_point_1", 100),
                entry("village/savanna/town_centers/savanna_meeting_point_2", 50),
                entry("village/savanna/town_centers/savanna_meeting_point_3", 150),
                entry("village/savanna/town_centers/savanna_meeting_point_4", 150),
                entry("village/savanna/zombie/town_centers/savanna_meeting_point_1", 2),
                entry("village/savanna/zombie/town_centers/savanna_meeting_point_2", 1),
                entry("village/savanna/zombie/town_centers/savanna_meeting_point_3", 3),
                entry("village/savanna/zombie/town_centers/savanna_meeting_point_4", 3)
        ));

        COLLECTION.put("village/savanna/streets", pool(
                "village/savanna/streets",
                entry("village/savanna/streets/corner_01", 2),
                entry("village/savanna/streets/corner_03", 2),
                entry("village/savanna/streets/straight_02", 4),
                entry("village/savanna/streets/straight_04", 7),
                entry("village/savanna/streets/straight_05", 3),
                entry("village/savanna/streets/straight_06", 4),
                entry("village/savanna/streets/straight_08", 4),
                entry("village/savanna/streets/straight_09", 4),
                entry("village/savanna/streets/straight_10", 4),
                entry("village/savanna/streets/straight_11", 4),
                entry("village/savanna/streets/crossroad_02", 1),
                entry("village/savanna/streets/crossroad_03", 2),
                entry("village/savanna/streets/crossroad_04", 2),
                entry("village/savanna/streets/crossroad_05", 2),
                entry("village/savanna/streets/crossroad_06", 2),
                entry("village/savanna/streets/crossroad_07", 2),
                entry("village/savanna/streets/split_01", 2),
                entry("village/savanna/streets/split_02", 2),
                entry("village/savanna/streets/turn_01", 3)
        ));

        COLLECTION.put("village/savanna/zombie/streets", pool(
                "village/savanna/zombie/streets",
                entry("village/savanna/zombie/streets/corner_01", 2),
                entry("village/savanna/zombie/streets/corner_03", 2),
                entry("village/savanna/zombie/streets/straight_02", 4),
                entry("village/savanna/zombie/streets/straight_04", 7),
                entry("village/savanna/zombie/streets/straight_05", 3),
                entry("village/savanna/zombie/streets/straight_06", 4),
                entry("village/savanna/zombie/streets/straight_08", 4),
                entry("village/savanna/zombie/streets/straight_09", 4),
                entry("village/savanna/zombie/streets/straight_10", 4),
                entry("village/savanna/zombie/streets/straight_11", 4),
                entry("village/savanna/zombie/streets/crossroad_02", 1),
                entry("village/savanna/zombie/streets/crossroad_03", 2),
                entry("village/savanna/zombie/streets/crossroad_04", 2),
                entry("village/savanna/zombie/streets/crossroad_05", 2),
                entry("village/savanna/zombie/streets/crossroad_06", 2),
                entry("village/savanna/zombie/streets/crossroad_07", 2),
                entry("village/savanna/zombie/streets/split_01", 2),
                entry("village/savanna/zombie/streets/split_02", 2),
                entry("village/savanna/zombie/streets/turn_01", 3)
        ));

        COLLECTION.put("village/savanna/houses", pool(
                "village/savanna/houses",
                entry("village/savanna/houses/savanna_small_house_1", 2),
                entry("village/savanna/houses/savanna_small_house_2", 2),
                entry("village/savanna/houses/savanna_small_house_3", 2),
                entry("village/savanna/houses/savanna_small_house_4", 2),
                entry("village/savanna/houses/savanna_small_house_5", 2),
                entry("village/savanna/houses/savanna_small_house_6", 2),
                entry("village/savanna/houses/savanna_small_house_7", 2),
                entry("village/savanna/houses/savanna_small_house_8", 2),
                entry("village/savanna/houses/savanna_medium_house_1", 2),
                entry("village/savanna/houses/savanna_medium_house_2", 2),
                entry("village/savanna/houses/savanna_butchers_shop_1", 2),
                entry("village/savanna/houses/savanna_butchers_shop_2", 2),
                entry("village/savanna/houses/savanna_tool_smith_1", 2),
                entry("village/savanna/houses/savanna_fletcher_house_1", 2),
                entry("village/savanna/houses/savanna_shepherd_1", 7),
                entry("village/savanna/houses/savanna_armorer_1", 1),
                entry("village/savanna/houses/savanna_fisher_cottage_1", 3),
                entry("village/savanna/houses/savanna_tannery_1", 2),
                entry("village/savanna/houses/savanna_cartographer_1", 2),
                entry("village/savanna/houses/savanna_library_1", 2),
                entry("village/savanna/houses/savanna_mason_1", 2),
                entry("village/savanna/houses/savanna_weaponsmith_1", 2),
                entry("village/savanna/houses/savanna_weaponsmith_2", 2),
                entry("village/savanna/houses/savanna_temple_1", 2),
                entry("village/savanna/houses/savanna_temple_2", 3),
                entry("village/savanna/houses/savanna_large_farm_1", 4),
                entry("village/savanna/houses/savanna_large_farm_2", 6),
                entry("village/savanna/houses/savanna_small_farm", 4),
                entry("village/savanna/houses/savanna_animal_pen_1", 2),
                entry("village/savanna/houses/savanna_animal_pen_2", 2),
                entry("village/savanna/houses/savanna_animal_pen_3", 2)
        ));

        COLLECTION.put("village/savanna/zombie/houses", pool(
                "village/savanna/zombie/houses",
                entry("village/savanna/zombie/houses/savanna_small_house_1", 2),
                entry("village/savanna/zombie/houses/savanna_small_house_2", 2),
                entry("village/savanna/zombie/houses/savanna_small_house_3", 2),
                entry("village/savanna/zombie/houses/savanna_small_house_4", 2),
                entry("village/savanna/zombie/houses/savanna_small_house_5", 2),
                entry("village/savanna/zombie/houses/savanna_small_house_6", 2),
                entry("village/savanna/zombie/houses/savanna_small_house_7", 2),
                entry("village/savanna/zombie/houses/savanna_small_house_8", 2),
                entry("village/savanna/zombie/houses/savanna_medium_house_1", 2),
                entry("village/savanna/zombie/houses/savanna_medium_house_2", 2),
                entry("village/savanna/houses/savanna_butchers_shop_1", 2),
                entry("village/savanna/houses/savanna_butchers_shop_2", 2),
                entry("village/savanna/houses/savanna_tool_smith_1", 2),
                entry("village/savanna/houses/savanna_fletcher_house_1", 2),
                entry("village/savanna/houses/savanna_shepherd_1", 2),
                entry("village/savanna/houses/savanna_armorer_1", 1),
                entry("village/savanna/houses/savanna_fisher_cottage_1", 2),
                entry("village/savanna/houses/savanna_tannery_1", 2),
                entry("village/savanna/houses/savanna_cartographer_1", 2),
                entry("village/savanna/houses/savanna_library_1", 2),
                entry("village/savanna/houses/savanna_mason_1", 2),
                entry("village/savanna/houses/savanna_weaponsmith_1", 2),
                entry("village/savanna/houses/savanna_weaponsmith_2", 2),
                entry("village/savanna/houses/savanna_temple_1", 1),
                entry("village/savanna/houses/savanna_temple_2", 3),
                entry("village/savanna/houses/savanna_large_farm_1", 4),
                entry("village/savanna/zombie/houses/savanna_large_farm_2", 4),
                entry("village/savanna/houses/savanna_small_farm", 4),
                entry("village/savanna/houses/savanna_animal_pen_1", 2),
                entry("village/savanna/zombie/houses/savanna_animal_pen_2", 2),
                entry("village/savanna/zombie/houses/savanna_animal_pen_3", 2)
        ));

        COLLECTION.put("village/savanna/terminators", pool(
                "village/savanna/terminators",
                entry("village/plains/terminators/terminator_01", 1),
                entry("village/plains/terminators/terminator_02", 1),
                entry("village/plains/terminators/terminator_03", 1),
                entry("village/plains/terminators/terminator_04", 1),
                entry("village/savanna/terminators/terminator_05", 1)
        ));

        COLLECTION.put("village/savanna/zombie/terminators", pool(
                "village/savanna/zombie/terminators",
                entry("village/plains/terminators/terminator_01", 1),
                entry("village/plains/terminators/terminator_02", 1),
                entry("village/plains/terminators/terminator_03", 1),
                entry("village/plains/terminators/terminator_04", 1),
                entry("village/savanna/zombie/terminators/terminator_05", 1)
        ));

        COLLECTION.put("village/savanna/decor", pool(
                "village/savanna/decor",
                entry("village/savanna/savanna_lamp_post_01", 4)
        ));

        COLLECTION.put("village/savanna/zombie/decor", pool(
                "village/savanna/zombie/decor",
                entry("village/savanna/savanna_lamp_post_01", 4)
        ));

        COLLECTION.put("village/savanna/villagers", pool(
                "village/savanna/villagers",
                entry("village/savanna/villagers/nitwit", 1),
                entry("village/savanna/villagers/baby", 1),
                entry("village/savanna/villagers/unemployed", 10)
        ));

        COLLECTION.put("village/savanna/zombie/villagers", pool(
                "village/savanna/zombie/villagers",
                entry("village/savanna/zombie/villagers/nitwit", 1),
                entry("village/savanna/zombie/villagers/unemployed", 10)
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
