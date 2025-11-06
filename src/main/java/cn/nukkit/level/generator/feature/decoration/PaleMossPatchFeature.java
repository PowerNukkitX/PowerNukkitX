package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockGrassBlock;
import cn.nukkit.block.BlockPaleMossBlock;
import cn.nukkit.block.BlockPaleMossCarpet;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class PaleMossPatchFeature extends GenerateFeature {

    public static final String NAME = "minecraft:pale_moss_patch_feature";

    protected static final BlockState GRASS_BLOCK = BlockGrassBlock.PROPERTIES.getDefaultState();
    protected static final BlockState PALE_MOSS_BLOCK = BlockPaleMossBlock.PROPERTIES.getDefaultState();
    protected static final BlockState PALE_MOSS_CARPET = BlockPaleMossCarpet.PROPERTIES.getDefaultState();


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                if(Registries.BIOME.get(chunk.getBiomeId(x, SEA_LEVEL, z)).getTags().contains(BiomeTags.PALE_GARDEN)) {
                    if(random.nextInt(9) < 7) {
                        int y = chunk.getHeightMap(x, z);
                        while(chunk.getBlockState(x, y, z) != GRASS_BLOCK && y > SEA_LEVEL) {
                            y--;
                        }
                        if(y > SEA_LEVEL) {
                            chunk.setBlockState(x, y, z, PALE_MOSS_BLOCK);
                            if(random.nextInt(9) < 2) {
                                chunk.setBlockState(x, y+1, z, PALE_MOSS_CARPET);
                            }
                        }
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
