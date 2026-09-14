package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemMusicDisc;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;

import java.util.Objects;

/**
 * @author CreeperFace
 */
public class BlockEntityJukebox extends BlockEntitySpawnable {

    private Item recordItem;

    public BlockEntityJukebox(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (nbt.contains("RecordItem")) {
            this.recordItem = ItemHelper.read(getNbt().getCompound("RecordItem"));
        } else {
            this.recordItem = Item.AIR;
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


    public void play() {
        if (this.recordItem instanceof ItemMusicDisc itemRecord) {
            this.getLevel().addSound(this, itemRecord.getSoundId());
        }
    }

    public void stop() {
        if (this.recordItem instanceof ItemMusicDisc itemRecord) {
            this.getLevel().stopSound(this, itemRecord.getSoundId());
        }
    }

    public void dropItem() {
        if (!this.recordItem.isNull()) {
            stop();
            this.level.dropItem(this.up(), this.recordItem);
            this.recordItem = Item.AIR;
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putCompound("RecordItem", ItemHelper.write(this.recordItem, null));
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putCompound("RecordItem", ItemHelper.write(this.recordItem, null));
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        this.dropItem();
    }
}
