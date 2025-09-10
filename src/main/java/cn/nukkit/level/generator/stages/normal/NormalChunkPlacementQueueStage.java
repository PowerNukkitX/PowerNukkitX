package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntArrayTag;

public class NormalChunkPlacementQueueStage extends GenerateStage {

    public static final String NAME = "normal_chunkplacementqueue";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        long chunkHash = Level.chunkHash(chunkX, chunkZ);
        String chunkKey = String.valueOf(chunkHash);
        BlockManager temp = new BlockManager(chunk.getLevel());
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                int targetX = chunkX + x;
                int targetZ = chunkZ + z;
                IChunk target = level.getChunk(targetX, targetZ);
                if(target != null && target != chunk) {
                    CompoundTag chunkExtra = target.getExtraData();
                    if(chunkExtra.containsCompound("outsideChunkStructureData")) {
                        CompoundTag outsideChunkStructureData = chunkExtra.getCompound("outsideChunkStructureData");
                        if(outsideChunkStructureData.containsList(chunkKey)) {
                            BlockManager.fromTag(outsideChunkStructureData.getList(chunkKey, IntArrayTag.class), temp);
                        }
                    }
                }
            }
        }
        for(Block b : temp.getBlocks()) {
            if((b.getChunkX() == chunkX) && (b.getChunkZ() == chunkZ)) {

            } else System.out.println(b);
        }
        temp.applySubChunkUpdate(temp.getBlocks());
    }

    @Override
    public String name() {
        return NAME;
    }
}
