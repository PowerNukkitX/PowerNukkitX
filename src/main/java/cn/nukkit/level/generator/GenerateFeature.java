package cn.nukkit.level.generator;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntArrayTag;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.HashMap;

public abstract class GenerateFeature {

    protected final HashMap<Long, BlockManager> PLACEMENT_QUEUE = new HashMap<>();

    public BlockManager getChunkPlacementQueue(Long chunkHash, Level level) {
        if(!PLACEMENT_QUEUE.containsKey(chunkHash)) PLACEMENT_QUEUE.put(chunkHash, new BlockManager(level));
        return PLACEMENT_QUEUE.get(chunkHash);
    }

    public abstract String name();

    public String identifier() {
        return name();
    }

    public abstract void apply(ChunkGenerateContext context);

    protected NukkitRandom getChunkLocalRandom(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        return new NukkitRandom(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
    }

    protected void writeOutsideChunkStructureData(IChunk current) {
        CompoundTag chunkExtra = current.getExtraData();
        if(!chunkExtra.containsCompound("outsideChunkStructureData")) {
            chunkExtra.putCompound("outsideChunkStructureData", new CompoundTag());
        }
        CompoundTag outsideChunkStructureData = chunkExtra.getCompound("outsideChunkStructureData");
        for(long chunkIdx : PLACEMENT_QUEUE.keySet()) {
            String targetChunkKey = String.valueOf(chunkIdx);
            BlockManager temp = new BlockManager(current.getLevel());
            if(outsideChunkStructureData.containsList(targetChunkKey)) {
                temp = BlockManager.fromTag(outsideChunkStructureData.getList(targetChunkKey, IntArrayTag.class), temp);
            }
            temp.merge(getChunkPlacementQueue(chunkIdx, current.getLevel()));
            outsideChunkStructureData.put(targetChunkKey, temp.toTag());
        }

    }

}
