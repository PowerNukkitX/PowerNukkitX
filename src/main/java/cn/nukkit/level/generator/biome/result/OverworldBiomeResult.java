package cn.nukkit.level.generator.biome.result;

import lombok.Getter;

@Getter
public class OverworldBiomeResult extends BiomeResult {

    final float continental;
    final float temperature;
    final float humidity;
    final float erosion;
    final float weirdness;
    final float pv;

    public OverworldBiomeResult(int biomeId, float continental, float temperature, float humidity, float erosion, float weirdness, float pv) {
        super(biomeId);
        this.continental = continental;
        this.temperature = temperature;
        this.humidity = humidity;
        this.erosion = erosion;
        this.weirdness = weirdness;
        this.pv = pv;
    }

}
