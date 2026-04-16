package cn.nukkit.level.generator.holder;

import cn.nukkit.level.generator.densityfunction.DensityFunction;
import cn.nukkit.level.generator.densityfunction.DensityBase3dNoise;
import cn.nukkit.level.generator.densityfunction.DensityNether;
import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.Getter;

@Getter
public class NetherObjectHolder extends RandomizedObjectHolder {

    private TerrainHolder terrainHolder;
    private BasaltDeltaHolder basaltDeltasHolder;

    public NetherObjectHolder(RandomSourceProvider randomSourceProvider) {
        super(randomSourceProvider);
        this.terrainHolder = new TerrainHolder(randomSourceProvider);
        this.basaltDeltasHolder = new BasaltDeltaHolder(randomSourceProvider);
    }

    @Getter
    public static class TerrainHolder extends RandomizedObjectHolder {

        private NormalNoise surfaceNoise;
        private NormalNoise patchNoise;
        private NormalNoise soulsandNoise;
        private NormalNoise netherStateNoise;
        private NormalNoise netherwartNoise;
        private DensityFunction base3dNoise;
        private DensityFunction densityFunction;

        public TerrainHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new NormalNoise(getRandomSourceProvider().identical(), -6, new float[]{1f, 1f, 1f});
            this.patchNoise = new NormalNoise(getRandomSourceProvider().identical(), -5, new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.013333333333333334f});
            this.soulsandNoise = new NormalNoise(getRandomSourceProvider().identical(), -8, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.013333333333333334f});
            this.netherStateNoise = new NormalNoise(getRandomSourceProvider().identical(), -4, new float[]{1.0f});
            this.netherwartNoise = new NormalNoise(getRandomSourceProvider().identical(), -3, new float[]{1.0f, 0.0f, 0.0f, 0.9f});
            this.base3dNoise = DensityBase3dNoise.nether(getRandomSourceProvider().identical());
            this.densityFunction = DensityNether.finalDensity(this.base3dNoise);
        }
    }

    @Getter
    public static class BasaltDeltaHolder extends RandomizedObjectHolder {

        private NormalNoise surfaceNoise;
        private NormalNoise surfaceSecNoise;

        public BasaltDeltaHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.surfaceSecNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 0, 1f, 1f});
        }
    }
}
