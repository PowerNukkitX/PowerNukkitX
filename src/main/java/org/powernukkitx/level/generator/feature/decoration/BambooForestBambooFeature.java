package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.BlockBamboo;
import org.powernukkitx.block.BlockPodzol;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.BambooLeafSize;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.tags.BlockTags;

public class BambooForestBambooFeature extends SurfaceGenerateFeature {

    private static final int BAMBOO_SHOOTS_PER_FEATURE = 4;
    private static final BlockState NO_LEAVE = BlockBamboo.PROPERTIES.getBlockState(CommonBlockProperties.BAMBOO_LEAF_SIZE.createValue(BambooLeafSize.NO_LEAVES));
    private static final BlockState SMALL_LEAVE = BlockBamboo.PROPERTIES.getBlockState(CommonBlockProperties.BAMBOO_LEAF_SIZE.createValue(BambooLeafSize.SMALL_LEAVES));
    private static final BlockState LARGE_LEAVE = BlockBamboo.PROPERTIES.getBlockState(CommonBlockProperties.BAMBOO_LEAF_SIZE.createValue(BambooLeafSize.LARGE_LEAVES));
    private static final BlockState PODZOL = BlockPodzol.PROPERTIES.getDefaultState();


    public static final String NAME = "minecraft:bamboo_then_podzol_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {

        if(!Registries.BIOME.containsTag(BiomeTags.BAMBOO, manager.getLevel().getBiomeId(x, y, z))) return;
        this.random.setSeed(x + y + z);

        for (int i = 0; i < BAMBOO_SHOOTS_PER_FEATURE; i++) {
            int px = x;
            int pz = z;
            int py = y;
            if (i > 0) {
                px += random.nextInt(-3, 3);
                pz += random.nextInt(-3, 3);
                py = manager.getLevel().getHeightMap(px, pz) + 1;
            }

            this.placeBambooAt(manager, px, py, pz);
        }
    }

    private void placeBambooAt(BlockManager manager, int x, int y, int z) {
        if (!manager.getBlockIfCachedOrLoaded(x, y, z).isAir()
                || !manager.getBlockIfCachedOrLoaded(x, y - 1, z).hasTag(BlockTags.DIRT)) {
            return;
        }

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
        return 240;
    }

    @Override
    public int getRandom() {
        return 80;
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
