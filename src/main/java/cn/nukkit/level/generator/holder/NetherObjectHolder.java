package cn.nukkit.level.generator.holder;

import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
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

        private SimplexNoise surfaceNoise;
        private SimplexNoise patchNoise;
        private SimplexNoise soulsandNoise;
        private SimplexNoise netherStateNoise;
        private SimplexNoise netherwartNoise;

        public TerrainHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new SimplexNoise(getRandomSourceProvider().identical(), -6, new float[]{1f, 1f, 1f});
            this.patchNoise = new SimplexNoise(getRandomSourceProvider().identical(), -5, new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.013333333333333334f});
            this.soulsandNoise = new SimplexNoise(getRandomSourceProvider().identical(), -8, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.013333333333333334f});
            this.netherStateNoise = new SimplexNoise(getRandomSourceProvider().identical(), -4, new float[]{1.0f});
            this.netherwartNoise = new SimplexNoise(getRandomSourceProvider().identical(), -3, new float[]{1.0f, 0.0f, 0.0f, 0.9f});
        }
    }

    @Getter
    public static class BasaltDeltaHolder extends RandomizedObjectHolder {

        private SimplexNoise surfaceNoise;
        private SimplexNoise surfaceSecNoise;

        public BasaltDeltaHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new SimplexNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.surfaceSecNoise = new SimplexNoise(randomSourceProvider.identical(), -6, new float[]{1f, 0, 1f, 1f});
        }
    }
}
