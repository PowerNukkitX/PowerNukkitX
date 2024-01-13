package cn.nukkit.level.generator.task;

import cn.nukkit.Server;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.scheduler.AsyncTask;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PopulationTask extends AsyncTask {
    public final IChunk[] chunks = new IChunk[9];
    private final long seed;
    private final Level level;
    private boolean state;
    private IChunk centerChunk;
    private boolean isPopulated;

    public PopulationTask(Level level, IChunk chunk) {
        this.state = true;
        this.level = level;
        this.centerChunk = chunk;
        this.seed = level.getSeed();

        chunks[4] = chunk;

        int i = 0;
        for (int z = -1; z <= 1; z++) {
            for (int x = -1; x <= 1; x++, i++) {
                if (i == 4) continue;
                IChunk ck = level.getChunk(chunk.getX() + x, chunk.getZ() + z, true);
                this.chunks[i] = ck;
            }
        }
    }


    @Override
    public void onRun() {
        syncGen(0);
    }

    private void syncGen(int i) {
        if (i == chunks.length) {
            generationTask();
        } else {
            IChunk chunk = chunks[i];
            if (chunk != null) {
                synchronized (chunk) {
                    syncGen(i + 1);
                }
            }
        }
    }

    private void generationTask() {
        this.state = false;
        Generator generator = level.getGenerator();
        if (generator == null) {
            return;
        }

        ChunkManager manager = generator.getChunkManager();

        if (manager == null) {
            this.state = false;
            return;
        }

        synchronized (manager) {
            try {
                manager.cleanChunks(this.seed);
                IChunk centerChunk = this.centerChunk;

                if (centerChunk == null) {
                    return;
                }

                int index = 0;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++, index++) {
                        IChunk ck = this.chunks[index];
                        if (ck == centerChunk) continue;
                        if (ck == null) {
                            try {
                                this.chunks[index] = Chunk.builder().levelProvider(level.getProvider()).emptyChunk(centerChunk.getX() + x, centerChunk.getZ() + z);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            this.chunks[index] = ck;
                        }

                    }
                }

                for (IChunk chunk : this.chunks) {
                    manager.setChunk(chunk.getX(), chunk.getZ(), chunk);
                    if (!chunk.isGenerated()) {
                        generator.generateChunk(chunk.getX(), chunk.getZ());
                        IChunk newChunk = manager.getChunk(chunk.getX(), chunk.getZ());
                        newChunk.setGenerated();
                        if (newChunk != chunk) manager.setChunk(chunk.getX(), chunk.getZ(), newChunk);
                    }
                }

                isPopulated = centerChunk.isPopulated();
                if (!isPopulated) {
                    generator.populateChunk(centerChunk.getX(), centerChunk.getZ());
                    centerChunk = manager.getChunk(centerChunk.getX(), centerChunk.getZ());
                    centerChunk.setPopulated();
                    centerChunk.recalculateHeightMap();
                    centerChunk.populateSkyLight();
                    centerChunk.setLightPopulated();
                    this.centerChunk = centerChunk;
                }

                manager.setChunk(centerChunk.getX(), centerChunk.getZ());

                index = 0;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++, index++) {
                        chunks[index] = null;
                        IChunk newChunk = manager.getChunk(centerChunk.getX() + x, centerChunk.getZ() + z);
                        if (newChunk != null) {
                            if (newChunk.hasChanged()) {
                                chunks[index] = newChunk;
                            }
                        }

                    }
                }
                this.state = true;
            } finally {
                manager.cleanChunks(this.seed);
            }
        }
    }

    @Override
    public void onCompletion(Server server) {
        if (level != null) {
            if (!this.state) {
                return;
            }

            IChunk centerChunk = this.centerChunk;

            if (centerChunk == null) {
                return;
            }

            for (IChunk chunk : this.chunks) {
                if (chunk != null) {
                    level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
                }
            }

            level.generateChunkCallback(centerChunk.getX(), centerChunk.getZ(), centerChunk, isPopulated);

            //需要在全部地形生成完毕后再尝试生成结构
            //todo: 不应该写在这里，往前放更合理，但是会有NPE:(
            var generator = level.getGenerator();
           /* if (generator.shouldGenerateStructures()) {
                generator.populateStructure(centerChunk.getX(), centerChunk.getZ());
            }*/

            // 如果区块有修改就重新计算高度图
            for (IChunk chunk : this.chunks) {
                if (chunk != null && chunk.hasChanged()) {
                    chunk.recalculateHeightMap();
                }
            }
        }
    }
}
