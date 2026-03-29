package cn.nukkit.level.generator.holder;

import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.level.generator.stages.normal.sampler.CarvingSampler;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.Getter;

@Getter
public class NormalObjectHolder extends RandomizedObjectHolder {

    private TerrainHolder terrainHolder;
    private SurfaceHolder surfaceHolder;
    private SurfaceOverwriteHolder surfaceOverwriteHolder;
    private FeatureHolder featureHolder;

    public NormalObjectHolder(RandomSourceProvider randomSourceProvider) {
        super(randomSourceProvider);
        this.terrainHolder = new TerrainHolder(randomSourceProvider);
        this.surfaceHolder = new SurfaceHolder(randomSourceProvider);
        this.surfaceOverwriteHolder = new SurfaceOverwriteHolder(randomSourceProvider);
        this.featureHolder = new FeatureHolder(randomSourceProvider);
    }

    @Getter
    public class TerrainHolder extends RandomizedObjectHolder {

        private SimplexNoise surfaceNoise;
        private SimplexNoise jagged;
        private volatile CarvingSampler carver;
        public TerrainHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new SimplexNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.jagged = new SimplexNoise(randomSourceProvider.identical(), -16, new float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f});
            this.carver = new CarvingSampler(randomSourceProvider.getSeed());
        }
    }

    @Getter
    public class SurfaceHolder extends RandomizedObjectHolder {

        private SimplexF simplexF;

        public SurfaceHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.simplexF = new SimplexF(randomSourceProvider.identical(), 1f, 2 / 4f, 1 / 10f);
        }
    }

    @Getter
    public class SurfaceOverwriteHolder extends RandomizedObjectHolder {

        private NormalNoise surfaceNoise;
        private NormalNoise swampNoise;
        public SurfaceOverwriteHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.surfaceNoise = new NormalNoise(randomSourceProvider.identical(), -6, new float[]{1f, 1f, 1f});
            this.swampNoise = new NormalNoise(randomSourceProvider.identical(), -2, new float[]{1f});
        }
    }

    @Getter
    public class FeatureHolder extends RandomizedObjectHolder {

        private SimplexF randomClayWithDripleavesSnapToFloor;
        private SimplexF dripstoneCluster;
        private SimplexF mossPatchSnapToFloor;
        private SimplexF mossSnapToCeiling;
        private SimplexF sculkPatch;
        private SimplexNoise kelp;
        public FeatureHolder(RandomSourceProvider randomSourceProvider) {
            super(randomSourceProvider);
            this.randomClayWithDripleavesSnapToFloor = new SimplexF(randomSourceProvider.identical(), 1f, 2 / 4f, 1 / 15f);
            this.dripstoneCluster = new SimplexF(randomSourceProvider.identical(), 30f, 1 / 99f, 1 / 15f);
            this.mossPatchSnapToFloor = new SimplexF(randomSourceProvider.identical(), 2f, 2 / 4f, 1 / 10f);
            this.mossSnapToCeiling = new SimplexF(randomSourceProvider.identical(), 2f, 2 / 4f, 1 / 30f);
            this.sculkPatch = new SimplexF(randomSourceProvider.identical(), 20f, 1 / 99f, 1 / 100f);
            this.kelp = new SimplexNoise(randomSourceProvider.identical(), -7, new float[]{ 1 });
        }
    }
}
