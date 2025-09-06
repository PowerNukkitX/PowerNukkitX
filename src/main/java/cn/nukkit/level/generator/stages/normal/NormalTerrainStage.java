package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockStone;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.level.generator.noise.f.vanilla.NoiseGeneratorPerlinF;
import cn.nukkit.level.generator.noise.f.vanilla.NormalNoise;
import cn.nukkit.level.generator.noise.spline.JaggednessSpline;
import cn.nukkit.level.generator.noise.spline.OffsetSpline;
import cn.nukkit.level.generator.noise.spline.SplineGenerator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.CleanerHandle;
import cn.nukkit.utils.PNXLibInflater;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.HashMap;
import java.util.Map;

public class NormalTerrainStage extends GenerateStage {

    public static final String NAME = "normal_terrain";

    public static final int SEA_LEVEL = 62;

    private OverworldBiomePicker picker;
    private NormalNoise surfaceNoise;
    private NormalNoise jagged;


    private final ThreadLocal<Map<String, Double>> depthSplineMap = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<NukkitRandom> random = ThreadLocal.withInitial(NukkitRandom::new);

    @Override
    public void apply(ChunkGenerateContext context) {

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        Level level = chunk.getLevel();
        NukkitRandom random = this.random.get();
        random.setSeed(level.getSeed());
        if(picker == null) picker = (OverworldBiomePicker) level.getBiomePicker();
        if(surfaceNoise == null) surfaceNoise = new NormalNoise(random.identical(), -6, new float[]{1f, 1f, 1f});
        if(jagged == null) jagged = new NormalNoise(random.identical(), -16, new float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f});
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                OverworldBiomeResult result = picker.pick(x + baseX, SEA_LEVEL, z+ baseZ);
                float continental = result.getContinental();
                float erosion = result.getErosion();
                float pv = result.getPv();
                int biomeId = result.getBiomeId();
                BiomeDefinition biome = Registries.BIOME.get(biomeId);

                float baseHeightSum = 0.0F;
                float biomeWeightSum = 0.0F;
                int smoothFactor = 2;
                if(biomeId == BiomeID.RIVER) smoothFactor = 5;
                if(biomeId == BiomeID.OCEAN) smoothFactor = 5;
                for (int xSmooth = -smoothFactor; xSmooth <= smoothFactor; ++xSmooth) {
                    for (int zSmooth = -smoothFactor; zSmooth <= smoothFactor; ++zSmooth) {
                        int cx = x + baseX + xSmooth;
                        int cz = z + baseZ + zSmooth;
                        OverworldBiomeResult result1 = picker.pick(cx, SEA_LEVEL, cz);
                        BiomeDefinition definition = Registries.BIOME.get(result1.getBiomeId());
                        float weight = 1;
                        float d0 = this.surfaceNoise.getValue(cx, SEA_LEVEL, cz);
                        Map<String, Double> depthSplineMap = this.depthSplineMap.get();
                        depthSplineMap.put("minecraft:overworld/continents", (double) result1.getContinental());
                        depthSplineMap.put("minecraft:overworld/erosion", (double) result1.getErosion());
                        depthSplineMap.put("minecraft:overworld/ridges_folded", (double) result1.getPv());
                        depthSplineMap.put("minecraft:overworld/ridges", (double) result1.getWeirdness());
                        float jaggedValue = jagged.getValue(cx * 1500, SEA_LEVEL, cz * 1500 );
                        if(jaggedValue < 0) jaggedValue /= 2;
                        float jaggedness = (float) (JaggednessSpline.CACHED_SPLINE.evaluate(depthSplineMap) * jaggedValue * 4);
                        float depth = (float) (OffsetSpline.CACHED_SPLINE.evaluate(depthSplineMap) - 0.5037500262260437f);
                        float finalDensity = jaggedness + depth;
                        float roughness = (d0 * NukkitMath.remap(result1.getErosion() + result1.getPv(), -2, 2, -0.1f, 0.1f));
                        float finalDensityRoughed = finalDensity + roughness;
                        baseHeightSum += finalDensityRoughed * weight;
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
