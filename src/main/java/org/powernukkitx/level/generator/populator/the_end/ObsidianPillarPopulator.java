package org.powernukkitx.level.generator.populator.the_end;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectObsidianPillar;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.math.Vector3;

public class ObsidianPillarPopulator extends Populator {
    public static final String NAME = "the_end_obsidian_pillar";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();

        for(int i = 0; i < 10; i++) {
            ObjectObsidianPillar pillar = ObjectObsidianPillar.fromSeed(level.getSeed(), i);
            int x = pillar.getX();
            int z = pillar.getZ();
            if(x >> 4 == chunkX && z >> 4 == chunkZ) {
                BlockManager object = new BlockManager(level);
                pillar.generate(object, null, new Vector3(x, level.getHeightMap(x, z), z));
                queueObject(chunk, object);
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
