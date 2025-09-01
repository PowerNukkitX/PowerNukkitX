package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockLeafLitter;
import cn.nukkit.block.BlockWildflowers;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.Set;

import static cn.nukkit.block.property.CommonBlockProperties.GROWTH;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

public class BirchForestWildflowersFeature extends GenerateFeature {

    public static final String NAME = "minecraft:scatter_birch_forest_wildflowers_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        NukkitRandom random = new NukkitRandom(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if(random.fork().nextInt(10) < 1) {
                    int y = chunk.getHeightMap(x, z) + 1;
                    Set<String> tags = Registries.BIOME.get(chunk.getBiomeId(x, y, z)).getTags();
                    if(tags.contains(BiomeTags.FOREST) && tags.contains(BiomeTags.BIRCH)) {
                        Block support = chunk.getBlockState(x, y - 1, z).toBlock();
                        if (support.isFullBlock() && !support.isTransparent()) {
                            if (chunk.getBlockState(x, y, z) == BlockAir.STATE) {
                                NukkitRandom rnd = random.fork();
                                chunk.setBlockState(x, y, z, BlockWildflowers.PROPERTIES.getBlockState(
                                        MINECRAFT_CARDINAL_DIRECTION.createValue(
                                                MinecraftCardinalDirection.values()[rnd.nextInt(MinecraftCardinalDirection.values().length - 1)]
                                        ),
                                        GROWTH.createValue(rnd.nextInt(3))
                                ));
                            }
                        }
                    }
                }
            }
        }
    }
}
