package cn.nukkit.level.generator.biome.result;

import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

import static cn.nukkit.level.biome.BiomeID.DEEP_DARK;
import static cn.nukkit.level.biome.BiomeID.DRIPSTONE_CAVES;
import static cn.nukkit.level.biome.BiomeID.LUSH_CAVES;

@Getter
public class OverworldBiomeResult extends BiomeResult {

    final float continental;
    final float temperature;
    final float humidity;
    final float erosion;
    final float weirdness;
    final float pv;

    final int original;

    public OverworldBiomeResult(int biomeId, float continental, float temperature, float humidity, float erosion, float weirdness, float pv) {
        super(biomeId);
        this.original = biomeId;
        this.continental = continental;
        this.temperature = temperature;
        this.humidity = humidity;
        this.erosion = erosion;
        this.weirdness = weirdness;
        this.pv = pv;
    }

    /**
     * @param y The Y-Coordinate you want to get the corrected biome id from
     */
    @ApiStatus.Internal
    public OverworldBiomeResult correct(int y) {
        float depth = (-y / 128f);

        if (depth >= 0.2f) {
            if (depth < 0.99f) {
                if (continental > 0.8f && continental < 1f) {
                    biomeId = DRIPSTONE_CAVES;
                } else if (humidity > 0.3f) {
                    biomeId = LUSH_CAVES;
                }
            } else if(depth > 1.1){
                if (erosion > -1f && erosion < -0.375f) {
                    biomeId = DEEP_DARK;
                }
            }
        }
        return this;
    }

    public void reset() {
        biomeId = original;
    }

}
