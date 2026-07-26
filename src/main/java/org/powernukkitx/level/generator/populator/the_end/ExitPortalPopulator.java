package org.powernukkitx.level.generator.populator.the_end;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectExitPortal;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.math.Vector3;

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
            object.applySubChunkUpdate();
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
