package org.powernukkitx.level.generator.holder;

import org.powernukkitx.level.generator.noise.d.NoiseGeneratorOctavesD;
import org.powernukkitx.level.generator.noise.d.NoiseGeneratorSimplexD;
import org.powernukkitx.utils.random.RandomSourceProvider;
import lombok.Getter;

@Getter
public class TheEndObjectHolder extends RandomizedObjectHolder {

    private TerrainHolder terrainHolder;
    public TheEndObjectHolder(RandomSourceProvider randomSourceProvider) {
        super(randomSourceProvider);
        this.terrainHolder = new TerrainHolder(randomSourceProvider);
    }

    @Getter
    public static class TerrainHolder extends RandomizedObjectHolder {

        private NoiseGeneratorOctavesD roughnessNoiseOctaves;
        private NoiseGeneratorOctavesD roughnessNoiseOctaves2;
        private NoiseGeneratorOctavesD detailNoiseOctaves;
        private NoiseGeneratorSimplexD islandNoise;

        public TerrainHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.roughnessNoiseOctaves = new NoiseGeneratorOctavesD(randomSourceProvider.identical(), 16);
            this.roughnessNoiseOctaves2 = new NoiseGeneratorOctavesD(randomSourceProvider.identical(), 16);
            this.detailNoiseOctaves = new NoiseGeneratorOctavesD(randomSourceProvider.identical(), 8);
            this.islandNoise = new NoiseGeneratorSimplexD(randomSourceProvider.identical());
        }
    }

}
