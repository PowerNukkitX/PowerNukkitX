package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntArrayTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.random.Xoroshiro128;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.HashMap;
import java.util.Objects;

public abstract class Populator {

    protected final Xoroshiro128 random = new Xoroshiro128();

    protected final HashMap<Long, BlockManager> PLACEMENT_QUEUE = new HashMap<>();

    public abstract String name();

    public abstract void apply(ChunkGenerateContext context);

    protected void queueObject(IChunk chunk, BlockManager object) {
        object.applySubChunkUpdate();
    }

    public BlockManager getChunkPlacementQueue(Long chunkHash, Level level) {
        if(!PLACEMENT_QUEUE.containsKey(chunkHash)) PLACEMENT_QUEUE.put(chunkHash, new BlockManager(level));
        return PLACEMENT_QUEUE.get(chunkHash);
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
