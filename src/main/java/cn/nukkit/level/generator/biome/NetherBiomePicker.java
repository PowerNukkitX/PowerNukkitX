package cn.nukkit.level.generator.biome;

import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.generator.biome.result.NetherBiomeResult;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.Map;

import static cn.nukkit.level.generator.biome.OverworldBiomePicker.XZSCALE;

public class NetherBiomePicker extends BiomePicker<NetherBiomeResult> {

    private final SimplexNoise temperatureNoise;
    private final SimplexNoise humidityNoise;

    private final static Vector3 BASALT_DELTAS = new Vector3(-0.5f, 0, 0.175f);
    private final static Vector3 CRIMSON_FOREST = new Vector3(0.4f, 0, 0);
    private final static Vector3 HELL = new Vector3(0, 0, 0);
    private final static Vector3 SOULSAND_VALLEY = new Vector3(0f, -0.5f, 0);
    private final static Vector3 WARRPED_FOREST = new Vector3(0f, 0.5f, 0.375f);

    private final static Map<Integer, Vector3> POINTS = Map.of(
            BiomeID.BASALT_DELTAS, BASALT_DELTAS,
            BiomeID.CRIMSON_FOREST, CRIMSON_FOREST,
            BiomeID.HELL, HELL,
            BiomeID.SOULSAND_VALLEY, SOULSAND_VALLEY,
            BiomeID.WARPED_FOREST, WARRPED_FOREST
    );

    public NetherBiomePicker(NukkitRandom random) {
        super(random);
        temperatureNoise = new SimplexNoise(random.fork(), -10 , new float[]{ 1.5f, 0, 1, 0, 0, 0 });
        humidityNoise = new SimplexNoise(random.fork(), -8 , new float[]{ 1, 1, 0, 0, 0, 0 });
    }

    @Override
    public NetherBiomeResult pick(int x, int y, int z) {
        float scaledX = x * XZSCALE;
        float scaledZ = z * XZSCALE;
        float temperature = temperatureNoise.getValue(scaledX, y, scaledZ);
        float humidity = humidityNoise.getValue(scaledX, y, scaledZ);
        Vector3 point = new Vector3(temperature, humidity, 0);
        double distance = Float.MAX_VALUE;
        int biomeId = BiomeID.HELL;
        for(var entry : POINTS.entrySet()) {
            double delta = point.distanceSquared(entry.getValue());
            if(delta < distance) {
                distance = delta;
                biomeId = entry.getKey();
            }
        }
        return new NetherBiomeResult(biomeId, temperature, humidity);
    }
}
