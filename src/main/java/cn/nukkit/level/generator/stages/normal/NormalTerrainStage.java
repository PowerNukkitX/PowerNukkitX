package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockStone;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.level.generator.noise.f.vanilla.NormalNoise;
import cn.nukkit.level.generator.noise.spline.Spline;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.NukkitRandom;

public class NormalTerrainStage extends GenerateStage {

    public static final String NAME = "normal_terrain";

    public static final int SEA_LEVEL = 62;

    private OverworldBiomePicker picker;
    private NormalNoise surfaceNoise;


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        Level level = chunk.getLevel();
        NukkitRandom random = new NukkitRandom(level.getSeed());
        if(picker == null) picker = (OverworldBiomePicker) level.getBiomePicker();
        if(surfaceNoise == null) surfaceNoise = new NormalNoise(random.identical(), -6, new float[]{1f, 1f, 1f});
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                OverworldBiomeResult result = picker.pick(x + baseX, SEA_LEVEL, z+ baseZ);
                float continental = result.getContinental();
                float erosion = result.getErosion();
                float pv = result.getPv();
                BiomeDefinition biome = Registries.BIOME.get(result.getBiomeId());

                float baseHeightSum = 0.0F;
                float biomeWeightSum = 0.0F;
                int smoothFactor = 5;
                for (int xSmooth = -smoothFactor; xSmooth <= smoothFactor; ++xSmooth) {
                    for (int zSmooth = -smoothFactor; zSmooth <= smoothFactor; ++zSmooth) {
                        int cx = x + baseX + xSmooth;
                        int cz = z + baseZ + zSmooth;
                        OverworldBiomeResult result1 = picker.pick(cx, SEA_LEVEL, cz);
                        BiomeDefinition definition = Registries.BIOME.get(result1.getBiomeId());
                        float weight = 1 - definition.data.scale;
                        float d0 = this.surfaceNoise.getValue(cx, SEA_LEVEL, cz);
                        float depth = (Spline.getDepth(result1.getContinental(), result1.getErosion(), result1.getPv()) + (d0 * Math.clamp(result1.getErosion(), 0.1f, 0.3f)));
                        baseHeightSum += depth * weight;
                        biomeWeightSum += weight;
                    }
                }

                baseHeightSum = baseHeightSum / Math.max(biomeWeightSum, 1);
                float originalHeight = NukkitMath.remap(baseHeightSum, -1.5f, 1.5f, level.getMinHeight(), level.getMaxHeight());

                float height = NukkitMath.clamp(originalHeight, level.getMinHeight(), level.getMaxHeight());
                for(int y = 0; y < height; y++) {
                    chunk.setBlockState(x, y, z, BlockStone.PROPERTIES.getDefaultState());
                }
                for(int y = SEA_LEVEL; y > height; y--) {
                    chunk.setBlockState(x, y, z, BlockWater.PROPERTIES.getDefaultState());
                }
            }
        }
        NukkitRandom bedrockRandom = random.identical();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for(int y = level.getMinHeight(); y < 0; y++) {
                    chunk.setBlockState(x, y, z, BlockDeepslate.PROPERTIES.getDefaultState());
                }
                for (int y = 0; y < 8; y++) {
                    if (random.nextBoundedInt(y) == 0) {
                        chunk.setBlockState(x, y, z, BlockDeepslate.PROPERTIES.getDefaultState());
                    }
                }
                chunk.setBlockState(x, level.getMinHeight(), z, BlockBedrock.PROPERTIES.getDefaultState());
                for (int i = 1; i < 5; i++) {
                    if (bedrockRandom.nextBoundedInt(i) == 0) {
                        chunk.setBlockState(x, level.getMinHeight() +i, z, BlockBedrock.PROPERTIES.getDefaultState());
                    }
                }
            }
        }
        chunk.recalculateHeightMap();
    }


    @Override
    public String name() {
        return NAME;
    }
}
