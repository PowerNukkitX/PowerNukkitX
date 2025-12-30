package cn.nukkit.level.generator.populator.the_end;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectExitPortal;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.Vector3;

public class ExitPortalPopulator extends Populator {

    public static final String NAME = "the_end_exit_portal";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();

        if(chunkX == 1 && chunkZ == 0) {
            BlockManager object = new BlockManager(level);
            ObjectExitPortal exitPortal = new ObjectExitPortal();
            exitPortal.generate(object, null, new Vector3(0, chunk.getHeightMap(0, 0), 0));
            object.generateChunks();
            object.applySubChunkUpdate();
            queueObject(chunk, object);
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
