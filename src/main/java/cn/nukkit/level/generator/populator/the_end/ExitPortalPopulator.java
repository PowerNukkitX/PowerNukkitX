package cn.nukkit.level.generator.populator.the_end;

import cn.nukkit.block.Block;
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

        if(chunkX == 0 && chunkZ == 0) {
            BlockManager manager = new BlockManager(level);
            BlockManager object = new BlockManager(level);
            ObjectExitPortal exitPortal = new ObjectExitPortal();
            exitPortal.generate(object, null, new Vector3(0, chunk.getHeightMap(0, 0), 0));
            for(Block block : object.getBlocks()) {
                if(block.getChunk() != chunk) {
                    IChunk nextChunk = block.getChunk();
                    long chunkHash = Level.chunkHash(nextChunk.getX(), nextChunk.getZ());
                    getChunkPlacementQueue(chunkHash, level).setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                }
                if(block.getChunk().isGenerated()) {
                    manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                }
            }
            writeOutsideChunkStructureData(chunk);
            manager.applySubChunkUpdate(manager.getBlocks());
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
