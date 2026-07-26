package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.BlockSnowLayer;
import org.powernukkitx.block.BlockSpruceLeaves;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.feature.GriddedFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectSmallSpruceTree;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

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
    public boolean canSpawnHere(BiomeDefinitionData definition) {
        return Registries.BIOME.containsTag(BiomeTags.GROVE, definition);
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
                if (support.toBlock() instanceof BlockSpruceLeaves) {
                    object.setBlockStateAt(x + (chunk.getX() << 4), y + 1, z + (chunk.getZ() << 4), SNOW_LAYER);
                }
            }
        }
        queueObject(chunk, object);
    }
}
