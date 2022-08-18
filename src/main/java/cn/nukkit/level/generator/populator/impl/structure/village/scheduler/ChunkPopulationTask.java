package cn.nukkit.level.generator.populator.impl.structure.village.scheduler;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Collection;

public class ChunkPopulationTask extends AsyncTask {

    private final ChunkManager level;
    private final FullChunk chunk;
    private final Collection<Populator> populators;

    public ChunkPopulationTask(ChunkManager level, FullChunk chunk, Collection<Populator> populators) {
        this.level = level;
        this.chunk = chunk;
        this.populators = populators;
    }

    @Override
    public void onRun() {
        int chunkX = this.chunk.getX();
        int chunkZ = this.chunk.getZ();
        NukkitRandom random = new NukkitRandom(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());
        this.populators.forEach(populator -> populator.populate(this.level, chunkX, chunkZ, random, this.chunk));
    }
}
