package org.powernukkitx.level.generator.populator.the_end;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectObsidianPillar;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.math.Vector2;
import org.powernukkitx.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ObsidianPillarPopulator extends Populator {

    public static final String NAME = "the_end_obsidian_pillar";

    private EndSpike[] spikes;
    private long spikeSeed = Long.MIN_VALUE;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        if (spikes == null || spikeSeed != level.getSeed()) {
            spikeSeed = level.getSeed();
            spikes = createSpikes(spikeSeed);
        }
        for (EndSpike spike : spikes) {
            Vector2 p = spike.position();
            if(p.getFloorX() >> 4 == chunkX && p.getFloorY() >> 4 == chunkZ) {
                BlockManager object = new BlockManager(level);
                ObjectObsidianPillar pillar = new ObjectObsidianPillar(spike.radius(), spike.height(), spike.guarded());
                pillar.generate(object, null, new Vector3(p.x, level.getHeightMap(p.getFloorX(), p.getFloorY()), p.y));
                queueObject(chunk, object);
            }
        }
    }

    private EndSpike[] createSpikes(long seed) {
        Random random = new Random(seed);
        random.setSeed(random.nextLong() & 65535L);
        List<Integer> values = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            values.add(i);
        }
        Collections.shuffle(values, random);

        EndSpike[] spikes = new EndSpike[10];
        for (int i = 0; i < spikes.length; i++) {
            int value = values.get(i);
            int x = (int) Math.floor(42d * Math.cos(2d * (-Math.PI + Math.PI / 10d * i)));
            int z = (int) Math.floor(42d * Math.sin(2d * (-Math.PI + Math.PI / 10d * i)));
            spikes[i] = new EndSpike(new Vector2(x, z), 2 + value / 3, 76 + value * 3, value == 1 || value == 2);
        }
        return spikes;
    }

    private record EndSpike(Vector2 position, int radius, int height, boolean guarded) {
    }

    @Override
    public String name() {
        return NAME;
    }
}
