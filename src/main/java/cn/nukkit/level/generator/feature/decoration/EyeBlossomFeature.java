package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockClosedEyeblossom;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;

public class EyeBlossomFeature extends SurfaceGenerateFeature {

    private static final BlockState CLOSED_EYEBLOSSOM = BlockClosedEyeblossom.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_eyeblossom_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, CLOSED_EYEBLOSSOM);
    }

    @Override
    public int getBase() {
        return 10;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean isSupportValid(Block support) {
        return support.hasTag(BlockTags.DIRT) && Registries.BIOME.containsTag(
                BiomeTags.PALE_GARDEN,
                support.getLevel().getBiomeId(support.getFloorX(), support.getFloorY(), support.getFloorZ())
        );
    }
}
