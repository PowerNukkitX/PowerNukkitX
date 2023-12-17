package cn.nukkit.level.generator.task;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.scheduler.AsyncTask;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LightPopulationTask extends AsyncTask {

    public final int levelId;
    public IChunk chunk;

    public LightPopulationTask(Level level, IChunk chunk) {
        this.levelId = level.getId();
        this.chunk = chunk;
    }

    @Override
    public void onRun() {
        final IChunk chunk = this.chunk;
        if (chunk == null) {
            return;
        }

        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();
    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);

        IChunk chunk = this.chunk;
        if (level != null) {
            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}
