package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockReeds;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockSweetBerryBush;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.utils.random.NukkitRandom;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_16;
import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class ReedsFeature extends GenerateFeature {

    private final static BlockState STATE = BlockReeds.PROPERTIES.getBlockState(AGE_16.createValue(AGE_16.getMax()));

    public static final String NAME = "minecraft:reeds_feature";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if(random.nextInt(20) != 0) return;
        int maxReed = random.nextInt(3);
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                if(chunk.getHeightMap(x, z) == SEA_LEVEL) {
                    if(BlockReeds.isSupportValid(level.getBlock(x + (chunkX << 4), SEA_LEVEL, z + (chunkZ << 4)))) {
                        for(int i = 1; i < 4; i++) {
                            chunk.setBlockState(x, SEA_LEVEL + i, z, STATE);
                        }
                        if(--maxReed <= 0) return;
                    }
                }
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
