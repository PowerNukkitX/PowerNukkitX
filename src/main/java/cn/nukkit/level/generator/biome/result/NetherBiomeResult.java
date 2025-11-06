package cn.nukkit.level.generator.biome.result;

import lombok.Getter;

@Getter
public class NetherBiomeResult extends BiomeResult {

    final float temperature;
    final float humidity;

    public NetherBiomeResult(int biomeId, float temperature, float humidity) {
        super(biomeId);
        this.temperature = temperature;
        this.humidity = humidity;
    }

}
