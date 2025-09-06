package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.noise.f.vanilla.NormalNoise;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.Objects;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;


/**
 * @implNote <a href="https://minecraft.wiki/w/World_generation#Overworld_2">...</a>
 */
public class NormalSurfaceOverwriteStage extends GenerateStage {

    public static final String NAME = "normal_surface_overwrite";

    private NukkitRandom random;
    private NormalNoise surfaceNoise;
    private NormalNoise swampNoise;

    protected static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    protected static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if(random == null) random = new NukkitRandom(chunk.getLevel().getSeed());
        if(surfaceNoise == null) surfaceNoise = new NormalNoise(random.identical(), -6, new float[]{1f, 1f, 1f});
        if(swampNoise == null) swampNoise = new NormalNoise(random.identical(), -2, new float[]{1f});
        for(int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int lx = x + chunk.getX() << 4;
                int lz = z + chunk.getZ() << 4;
                int y = chunk.getHeightMap(x, z);
                int biomeId = chunk.getBiomeId(x, y, z);
                switch (biomeId) {
                    case BiomeID.SWAMPLAND -> {
                        if(swampNoise.getValue(lx, y, lz) > 0)
                            if(y == SEA_LEVEL) chunk.setBlockState(x, y, z, WATER);
                    }
                    case BiomeID.MANGROVE_SWAMP -> {
                        if(swampNoise.getValue(lx, y, lz) > 0)
                            if(y >= SEA_LEVEL - 1 && y <= SEA_LEVEL + 1) chunk.setBlockState(x, y, z, WATER);
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
