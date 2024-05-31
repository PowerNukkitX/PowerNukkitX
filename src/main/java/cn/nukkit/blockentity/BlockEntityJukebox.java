package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMusicDisc;
import cn.nukkit.level.format.IChunk;
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
    /**
     * @deprecated 
     */
    

    public BlockEntityJukebox(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        if (namedTag.contains("RecordItem")) {
            this.recordItem = NBTIO.getItemHelper(namedTag.getCompound("RecordItem"));
        } else {
            this.recordItem = Item.AIR;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == Block.JUKEBOX;
    }
    /**
     * @deprecated 
     */
    

    public void setRecordItem(Item recordItem) {
        Objects.requireNonNull(recordItem, "Record item cannot be null");
        this.recordItem = recordItem;
    }

    public Item getRecordItem() {
        return recordItem;
    }
    /**
     * @deprecated 
     */
    

    
    public void play() {
        if (this.recordItem instanceof ItemMusicDisc itemRecord) {
            PlaySoundPacket $1 = new PlaySoundPacket();
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
    /**
     * @deprecated 
     */
    
    public void stop() {
        if (this.recordItem instanceof ItemMusicDisc itemRecord) {
            StopSoundPacket $2 = new StopSoundPacket();
            packet.name = itemRecord.getSoundId();
            packet.stopAll = false;
            this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, packet);
        }
    }
    /**
     * @deprecated 
     */
    

    public void dropItem() {
        if (!this.recordItem.isNull()) {
            stop();
            this.level.dropItem(this.up(), this.recordItem);
            this.recordItem = Item.AIR;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putCompound("RecordItem", NBTIO.putItemHelper(this.recordItem));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onBreak(boolean isSilkTouch) {
        this.dropItem();
    }
}
