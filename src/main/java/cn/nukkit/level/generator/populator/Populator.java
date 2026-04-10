package cn.nukkit.level.generator.populator;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.Xoroshiro128;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.HashMap;

public abstract class Populator {

    protected final Xoroshiro128 random = new Xoroshiro128();

    protected final HashMap<Long, BlockManager> PLACEMENT_QUEUE = new HashMap<>();

    public abstract String name();

    public abstract void apply(ChunkGenerateContext context);

    protected void queueObject(IChunk chunk, BlockManager object) {
        object.applySubChunkUpdate();
    }

    public BlockManager getChunkPlacementQueue(Long chunkHash, Level level) {
        if (!PLACEMENT_QUEUE.containsKey(chunkHash)) PLACEMENT_QUEUE.put(chunkHash, new BlockManager(level));
        return PLACEMENT_QUEUE.get(chunkHash);
    }

    protected void writeOutsideChunkStructureData(IChunk current) {
        NbtMap chunkExtra = current.getExtraData();
        if (!chunkExtra.containsKey("outsideChunkStructureData")) {
            chunkExtra = chunkExtra.toBuilder().putCompound("outsideChunkStructureData", NbtMap.EMPTY).build();
        }
        NbtMap outsideChunkStructureData = chunkExtra.getCompound("outsideChunkStructureData");
        for (long chunkIdx : PLACEMENT_QUEUE.keySet()) {
            String targetChunkKey = String.valueOf(chunkIdx);
            BlockManager temp = new BlockManager(current.getLevel());
            if (outsideChunkStructureData.containsKey(targetChunkKey, NbtType.LIST)) {
                temp = BlockManager.fromTag(outsideChunkStructureData.getList(targetChunkKey, NbtType.INT_ARRAY), temp);
            }
            temp.merge(getChunkPlacementQueue(chunkIdx, current.getLevel()));
            outsideChunkStructureData = outsideChunkStructureData.toBuilder().putList(targetChunkKey, NbtType.INT_ARRAY, temp.toTag()).build();
        }
        current.setExtraData(chunkExtra.toBuilder().putCompound("outsideChunkStructureData", outsideChunkStructureData).build());
    }

}
