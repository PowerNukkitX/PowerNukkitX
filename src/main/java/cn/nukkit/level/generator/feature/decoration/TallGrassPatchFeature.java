package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockShortGrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;

public class TallGrassPatchFeature extends SurfaceGenerateFeature {

    private static final BlockState STATE = BlockShortGrass.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_tall_grass_feature";

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        int biomeId = context.getChunk().getBiomeId(7, context.getLevel().getHeightMap((context.getChunk().getX() << 4) + 7, (context.getChunk().getZ() << 4) + 7), 7);
        BiomeDefinition biome = Registries.BIOME.get(biomeId);
        if (biome.getTags().contains(BiomeTags.MOOSHROOM_ISLAND)) {
            return;
        }
        super.populate(context, random);
    }

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, STATE);
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
}
