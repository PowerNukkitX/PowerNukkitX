package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * An immutable view of everything a chunk needs in order to be written to disk.
 * <p>
 * Block entities and entities live on the main thread and are mutated by gameplay, so their NBT is captured
 * eagerly here while the main thread owns them. The serializer then only ever touches the captured tags,
 * which lets the actual write happen on a compute thread without racing gameplay.
 */
@Slf4j
public record ChunkSaveSnapshot(IChunk chunk, List<CompoundTag> blockEntities, List<CompoundTag> entities) {
    public static ChunkSaveSnapshot capture(IChunk chunk) {
        List<CompoundTag> blockEntityTags = new ObjectArrayList<>();
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (blockEntity.closed) {
                continue;
            }
            try {
                blockEntity.saveNBT();
                blockEntityTags.add(blockEntity.getNbt().copy());
            } catch (Exception e) {
                log.error("Failed to capture block entity {} at {},{},{} in chunk [{},{}]",
                        blockEntity.getSaveId(), (int) blockEntity.x, (int) blockEntity.y, (int) blockEntity.z,
                        chunk.getX(), chunk.getZ(), e);
            }
        }

        List<CompoundTag> entityTags = new ObjectArrayList<>();
        for (Entity entity : chunk.getEntities().values()) {
            if (entity instanceof Player || entity.closed || !entity.canBeSavedWithChunk()) {
                continue;
            }
            try {
                entity.saveNBT();
                entityTags.add(entity.getNbt().copy());
            } catch (Exception e) {
                log.error("Failed to capture entity {} at {},{},{} in chunk [{},{}]",
                        entity.getIdentifier(), (int) entity.x, (int) entity.y, (int) entity.z,
                        chunk.getX(), chunk.getZ(), e);
            }
        }

        return new ChunkSaveSnapshot(chunk, blockEntityTags, entityTags);
    }
}
