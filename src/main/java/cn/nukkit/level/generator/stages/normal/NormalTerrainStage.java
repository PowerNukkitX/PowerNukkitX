package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.level.generator.noise.spline.JaggednessSpline;
import cn.nukkit.level.generator.noise.spline.OffsetSpline;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.HashMap;
import java.util.Map;

public class NormalTerrainStage extends GenerateStage {

    private final static BlockState STONE = BlockStone.PROPERTIES.getDefaultState();
    private final static BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    private final static BlockState DEEPSLATE = BlockDeepslate.PROPERTIES.getDefaultState();
    private final static BlockState BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();

    public static final String NAME = "normal_terrain";

    public static final int SEA_LEVEL = 63;

    private OverworldBiomePicker picker;
    private SimplexNoise surfaceNoise;
    private SimplexNoise jagged;

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
        if(surfaceNoise == null) surfaceNoise = new SimplexNoise(random.identical(), -6, new float[]{1f, 1f, 1f});
        if(jagged == null) jagged = new SimplexNoise(random.identical(), -16, new float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f});
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                OverworldBiomeResult result = picker.pick(baseX + x, SEA_LEVEL, baseZ + z);
                int biomeId = result.getBiomeId();
                float baseHeightSum = 0.0F;
                float biomeWeightSum = 0.0F;
                int smoothFactor = 0;
                if(biomeId == BiomeID.RIVER) smoothFactor = 3;
                for (int xSmooth = -smoothFactor; xSmooth <= smoothFactor; ++xSmooth) {
                    for (int zSmooth = -smoothFactor; zSmooth <= smoothFactor; ++zSmooth) {
                        if(!(Math.abs(xSmooth) == smoothFactor || Math.abs(zSmooth) == smoothFactor)) continue;
                        int cx = x + baseX + xSmooth;
                        int cz = z + baseZ + zSmooth;
                        OverworldBiomeResult result1 = picker.pick(cx, SEA_LEVEL, cz);
                        float weight = 1;
                        Map<String, Double> depthSplineMap = this.depthSplineMap.get();
                        depthSplineMap.put("minecraft:overworld/continents", (double) result1.getContinental());
                        depthSplineMap.put("minecraft:overworld/erosion", (double) result1.getErosion());
                        depthSplineMap.put("minecraft:overworld/ridges_folded", (double) result1.getPv());
                        depthSplineMap.put("minecraft:overworld/ridges", (double) result1.getWeirdness());
                        float jaggedValue = jagged.getValue(cx * 1500, SEA_LEVEL, cz * 1500);

                        if (jaggedValue < 0) jaggedValue /= 2;
                        float jaggedness = (float) (JaggednessSpline.CACHED_SPLINE.evaluate(depthSplineMap) * jaggedValue * 4);
                        float depth = (float) (OffsetSpline.CACHED_SPLINE.evaluate(depthSplineMap) - 0.5037500262260437f);

                        float finalDensity = jaggedness + depth;
                        float finalDensityRoughed = finalDensity;
                        baseHeightSum += finalDensityRoughed * weight;
                        biomeWeightSum += weight;
                    }
                }

                baseHeightSum = baseHeightSum / Math.max(biomeWeightSum, 1);
                for(int y = level.getMinHeight(); y < level.getMaxHeight(); y++) {
                    float density = surfaceNoise.getValue((x + baseX), y,z + baseZ);
                    float densityMod = ((baseHeightSum + 0.18f) - NukkitMath.remapNormalized(y, level.getMinHeight(), level.getMaxHeight())) * 24;
                    if(density + densityMod > 0) {
                        chunk.getSection(y >> 4).setBlockState(x, y & 0x0f, z, STONE, 0);
                    } else {
                        if(y <= SEA_LEVEL) chunk.getSection(y >> 4).setBlockState(x, y & 0x0f, z, WATER, 0);
                    }
                }
            }
        }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for(int y = level.getMinHeight(); y < 0; y++) {
                    chunk.getSection(y >> 4).setBlockState(x, y & 0x0f, z, DEEPSLATE, 0);
                }
                for (int y = 0; y < 8; y++) {
                    if (random.nextBoundedInt(y) == 0) {
                        chunk.getSection(0).setBlockState(x, y & 0x0f, z, DEEPSLATE, 0);
                    }
                }
                chunk.getSection(level.getMinHeight() >> 4).setBlockState(x, level.getMinHeight() & 0x0f, z, BEDROCK, 0);
                for (int i = 1; i < 5; i++) {
                    if (random.nextBoundedInt(i) == 0) {
                        chunk.getSection(level.getMinHeight() >> 4).setBlockState(x, (level.getMinHeight() + i) & 0x0f, z, STONE, 0);
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
