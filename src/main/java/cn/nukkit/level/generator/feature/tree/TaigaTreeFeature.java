package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.BlockSnowLayer;
import cn.nukkit.block.BlockSpruceLeaves;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.GriddedFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectSmallSpruceTree;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class TaigaTreeFeature extends GriddedFeature {

    protected final static BlockState SNOW_LAYER = BlockSnowLayer.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:taiga_surface_trees_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectSmallSpruceTree();
    }

    @Override
    public boolean canSpawnHere(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.TAIGA);
    }

    @Override
    public int getSplit() {
        return 2;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        super.apply(context);
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        BlockManager object = new BlockManager(level);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = chunk.getHeightMap(x, z);
                BlockState support = chunk.getBlockState(x, y, z);
                if(support.toBlock().isFullBlock()) {
                    int cx = x + (chunk.getX() << 4);
                    int cz = z + (chunk.getZ() << 4);
                    if(chunk.getBiomeId(x, y, z) == BiomeID.COLD_TAIGA) {
                        if(object.getBlockAt(cx, y+1, cz).isAir()) {
                            object.setBlockStateAt(cx, y + 1, cz, SNOW_LAYER);
                        }
                    }
                }
            }
        }
        queueObject(chunk, object);
    }
}
