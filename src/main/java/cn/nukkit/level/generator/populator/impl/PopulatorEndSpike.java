package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.TheEnd;
import cn.nukkit.level.generator.object.end.ObjectEndSpike;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class PopulatorEndSpike extends Populator {

    private final TheEnd theEnd;

    public PopulatorEndSpike(TheEnd theEnd) {
        this.theEnd = theEnd;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {

        return;
        /*
         * final int r = 3 + random.nextBoundedInt(4);
         * final int h = 49 + 9 * r;
         * final int randH = random.nextBoundedInt(3);
         * ObjectEndSpike objectEndSpike = new ObjectEndSpike(new Vector3(chunkX << 4,
         * chunkZ << 4), r, h + randH * 3,
         * r == 3 && randH != 0);
         * objectEndSpike.generate(level, random);
         */
    }

}