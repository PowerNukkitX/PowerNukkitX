package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRecord;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.StopSoundPacket;

import java.util.Objects;

/**
 * @author CreeperFace
 */
public class BlockEntityJukebox extends BlockEntitySpawnable {

    private Item recordItem;

    public BlockEntityJukebox(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        if (namedTag.contains("RecordItem")) {
            this.recordItem = NBTIO.getItemHelper(namedTag.getCompound("RecordItem"));
        } else {
            this.recordItem = Item.get(0);
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == Block.JUKEBOX;
    }

    public void setRecordItem(Item recordItem) {
        Objects.requireNonNull(recordItem, "Record item cannot be null");
        this.recordItem = recordItem;
    }

    public Item getRecordItem() {
        return recordItem;
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    public void play() {
        if (this.recordItem instanceof ItemRecord itemRecord) {
            PlaySoundPacket packet = new PlaySoundPacket();
            packet.name = itemRecord.getSoundId();
            packet.volume = 1;
            packet.pitch = 1;
            packet.x = this.getFloorX();
            packet.y = this.getFloorY();
            packet.z = this.getFloorZ();
            this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, packet);
        }
    }

    //TODO: Transfer the stop sound to the new sound method
    public void stop() {
        if (this.recordItem instanceof ItemRecord itemRecord) {
            StopSoundPacket packet = new StopSoundPacket();
            packet.name = itemRecord.getSoundId();
            packet.stopAll = false;
            this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, packet);
        }
    }

    public void dropItem() {
        if (this.recordItem.getId() != 0) {
            stop();
            this.level.dropItem(this.up(), this.recordItem);
            this.recordItem = Item.get(0);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, JUKEBOX)
                .putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
    }

    @Override
    public void onBreak() {
        this.dropItem();
    }
}
