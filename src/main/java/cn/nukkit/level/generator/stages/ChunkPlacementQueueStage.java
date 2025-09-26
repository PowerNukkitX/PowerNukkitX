package cn.nukkit.level.generator.stages;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntArrayTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.nbt.tag.Tag;

public class ChunkPlacementQueueStage extends GenerateStage {

    public static final String NAME = "chunkplacementqueue";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        long chunkHash = Level.chunkHash(chunkX, chunkZ);
        String chunkKey = String.valueOf(chunkHash);
        BlockManager temp = new BlockManager(chunk.getLevel());
        CompoundTag chunkExtra = chunk.getExtraData();
        if(chunkExtra.containsList("structureAnchor")) {
            var chunks = chunkExtra.getList("structureAnchor", LongTag.class);
            for (LongTag longTag : chunks.getAll()) {
                long hash = longTag.getData();
                IChunk target = level.getChunk(Level.getHashX(hash), Level.getHashZ(hash));
                if (target != null && target != chunk) {
                    CompoundTag targetExtra = target.getExtraData();
                    if (targetExtra.containsCompound("outsideChunkStructureData")) {
                        CompoundTag outsideChunkStructureData = targetExtra.getCompound("outsideChunkStructureData");
                        if (outsideChunkStructureData.containsList(chunkKey)) {
                            BlockManager.fromTag(outsideChunkStructureData.getList(chunkKey, IntArrayTag.class), temp);
                        }
                    }
                }
            }
        }
        temp.applySubChunkUpdate();
        chunk.setChunkState(ChunkState.POPULATED);
    }

    @Override
    public String name() {
        return NAME;
    }
}
