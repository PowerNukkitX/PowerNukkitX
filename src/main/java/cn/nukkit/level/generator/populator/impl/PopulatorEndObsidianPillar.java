package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public class PopulatorEndObsidianPillar extends Populator {

    private final ObsidianPillar obsidianPillar;

    public PopulatorEndObsidianPillar(ObsidianPillar obsidianPillar) {
        this.obsidianPillar = obsidianPillar;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int x = this.obsidianPillar.getCenterX();
        int z = this.obsidianPillar.getCenterZ();
        if (x >> 4 != chunkX || z >> 4 != chunkZ) {
            return;
        }

        this.place(level);

        var nbt = Entity.getDefaultNBT(new Vector3(x + 0.5,  this.obsidianPillar.getHeight() + 1, z + 0.5));
        var entity = Entity.createEntity("EndCrystal", chunk, nbt);
        ((EntityEndCrystal) entity).setShowBase(true);
        entity.spawnToAll();
    }

    public void place(ChunkManager level) {
        int x = this.obsidianPillar.getCenterX();
        int z = this.obsidianPillar.getCenterZ();
        int height = this.obsidianPillar.getHeight();
        int radius = this.obsidianPillar.getRadius();

        for (int i = 0; i < height; i++) {
            for (int j = -radius; j <= radius; j++) {
                for (int k = -radius; k <= radius; k++) {
                    if (j * j + k * k <= radius * radius + 1) {
                        level.setBlockAt(x + j, i, z + k, OBSIDIAN);
                    }
                }
            }
        }

        if (this.obsidianPillar.isGuarded()) {
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    if (Math.abs(i) == 2 || Math.abs(j) == 2) {
                        for (int k = 0; k < 3; ++k) {
                            level.setBlockAt(x + i, height + k, z + j, IRON_BARS);
                        }
                    }
                    level.setBlockAt(x + i, height + 3, z + j, IRON_BARS);
                }
            }
        }

        level.setBlockAt(x, height, z, BEDROCK);
        level.setBlockAt(x, height + 1, z, FIRE);
    }

    public static class ObsidianPillar {

        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;

        public ObsidianPillar(int centerX, int centerZ, int radius, int height, boolean guarded) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.radius = radius;
            this.height = height;
            this.guarded = guarded;
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        private static final LoadingCache<Long, ObsidianPillar[]> CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(5L, TimeUnit.MINUTES)
                .build(new ObsidianPillarCacheLoader());

        public static ObsidianPillar[] getObsidianPillars(long seed) {
            return CACHE.getUnchecked(new Random(seed).nextLong() & 0xffffL);
        }

        private static class ObsidianPillarCacheLoader extends CacheLoader<Long, ObsidianPillar[]> {

            @Override
            public ObsidianPillar[] load(Long key) {
                List<Integer> pillars = IntStream.range(0, 10).boxed().collect(Collectors.toList());
                Collections.shuffle(pillars, new Random(key));
                ObsidianPillar[] obsidianPillars = new ObsidianPillar[10];

                for (int i = 0; i < 10; ++i) {
                    int pillar = pillars.get(i);
                    obsidianPillars[i] = new ObsidianPillar((int) (42d * Math.cos(2d * (-Math.PI + (Math.PI / 10d) * i))),
                            (int) (42d * Math.sin(2d * (-Math.PI + (Math.PI / 10d) * i))),
                            2 + pillar / 3, 76 + pillar * 3, pillar == 1 || pillar == 2);
                }

                return obsidianPillars;
            }
        }
    }
}
