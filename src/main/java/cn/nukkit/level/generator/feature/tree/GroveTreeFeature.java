package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSnowLayer;
import cn.nukkit.block.BlockSpruceLeaves;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
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

public class GroveTreeFeature extends GriddedFeature {

    protected final static BlockState SNOW_LAYER = BlockSnowLayer.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:grove_spruce_tree_feature";

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
        return definition.getTags().contains(BiomeTags.GROVE);
    }

    @Override
    public int getSplit() {
        return 4;
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
                if(support.toBlock() instanceof BlockSpruceLeaves) {
                    object.setBlockStateAt(x + (chunk.getX() << 4), y+1, z + (chunk.getZ() << 4), SNOW_LAYER);
                }
            }
        }
        queueObject(chunk, object);
    }
}
