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
        LongOpenHashSet chunkHashes = new LongOpenHashSet();
        BlockManager manager = new BlockManager(chunk.getLevel());
        LevelProvider provider = chunk.getLevel().getProvider();
        for(Block block : object.getBlocks()) {
            IChunk target = provider.getLoadedChunk(block.getChunkX(), block.getChunkZ());
            if(block.getChunk() != chunk) {
                IChunk nextChunk = block.getChunk();
                long chunkHash = Level.chunkHash(nextChunk.getX(), nextChunk.getZ());
                getChunkPlacementQueue(chunkHash, chunk.getLevel()).setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                chunkHashes.add(chunkHash);
            }
            if(target != null && target.isGenerated()) {
                manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
            }
        }

        long origenHash = Level.chunkHash(chunk.getX(), chunk.getZ());
        for(Long hash : chunkHashes) {
            IChunk target = provider.getLoadedChunk(hash);
            if(target == null) {
                target = provider.getEmptyChunk(Level.getHashX(hash), Level.getHashZ(hash));
                ((LevelDBProvider) provider).putChunk(hash, target);
            }
            CompoundTag chunkExtra = target.getExtraData();
            if(!chunkExtra.containsList("structureAnchor")) {
                chunkExtra.putList("structureAnchor", new ListTag<>(Tag.TAG_Long));
            }
            var chunks = chunkExtra.getList("structureAnchor", LongTag.class);
            if(chunks.getAll().stream().noneMatch(longTag -> Objects.equals(longTag.getData(), origenHash))) {
                chunks.add(new LongTag(origenHash));
            }
        }
        writeOutsideChunkStructureData(chunk);
        if(chunk.getChunkState().canSend()) {
            manager.applySubChunkUpdate();
        } else manager.applyWithoutUpdate();
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
