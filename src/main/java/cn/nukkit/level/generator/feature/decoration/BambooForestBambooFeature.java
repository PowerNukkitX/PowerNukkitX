package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockBamboo;
import cn.nukkit.block.BlockPodzol;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.BambooLeafSize;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.NukkitRandom;

public class BambooForestBambooFeature extends SurfaceGenerateFeature {

    private static final BlockState NO_LEAVE = BlockBamboo.PROPERTIES.getBlockState(CommonBlockProperties.BAMBOO_LEAF_SIZE.createValue(BambooLeafSize.NO_LEAVES));
    private static final BlockState SMALL_LEAVE = BlockBamboo.PROPERTIES.getBlockState(CommonBlockProperties.BAMBOO_LEAF_SIZE.createValue(BambooLeafSize.SMALL_LEAVES));
    private static final BlockState LARGE_LEAVE = BlockBamboo.PROPERTIES.getBlockState(CommonBlockProperties.BAMBOO_LEAF_SIZE.createValue(BambooLeafSize.LARGE_LEAVES));
    private static final BlockState PODZOL = BlockPodzol.PROPERTIES.getDefaultState();


    public static final String NAME = "minecraft:bamboo_then_podzol_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {

        if(!Registries.BIOME.get(manager.getLevel().getBiomeId(x, y, z)).getTags().contains(BiomeTags.BAMBOO)) return;
        this.random.setSeed(x + y + z);
        int midX = x + 1;
        int midZ = z + 1;

        int rad = random.nextInt(0, 2);
        for(int _x = -rad - 1; _x <= rad; _x++) {
            for (int _z = -rad - 1; _z <= rad; _z++) {
                float calcX = _x + 0.5f;
                float calcZ = _z + 0.5f;
                float calcRad = rad + 0.8f;
                int px = midX + _x;
                int pz = midZ + _z;
                if((calcX * calcX) + (calcZ * calcZ) < (calcRad * calcRad))
                    placePodzolAt(manager, px, manager.getLevel().getHeightMap(px, pz), pz);
            }
        }
        int height = 5 + random.nextInt(11);
        for(int i = 0; i <= height; i++) {
            int diff = height - i;
            BlockState place = diff < 3 ? LARGE_LEAVE : diff < 4 ? SMALL_LEAVE : NO_LEAVE;
            manager.setBlockStateAt(x, y+i, z, place);
        }
    }

    @Override
    public int getBase() {
        return 40;
    }

    @Override
    public int getRandom() {
        return 20;
    }

    @Override
    public String name() {
        return NAME;
    }

    private void placePodzolAt(BlockManager world, int x, int y, int z) {
        if (world.getBlockIfCachedOrLoaded(x, y, z).hasTag(BlockTags.DIRT)) {
            world.setBlockStateAt(new Vector3(x, y, z), PODZOL);
        }
    }
}
