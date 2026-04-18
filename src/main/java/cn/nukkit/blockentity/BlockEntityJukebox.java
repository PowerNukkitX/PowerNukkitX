package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMusicDisc;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.packet.PlaySoundPacket;
import org.cloudburstmc.protocol.bedrock.packet.StopSoundPacket;

import java.util.Objects;

/**
 * @author CreeperFace
 */
public class BlockEntityJukebox extends BlockEntitySpawnable {

    private Item recordItem;

    public BlockEntityJukebox(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (namedTag.containsKey("RecordItem")) {
            this.recordItem = ItemHelper.read(namedTag.getCompound("RecordItem"));
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
            final PlaySoundPacket packet = new PlaySoundPacket();
            packet.setName(itemRecord.getSoundId());
            packet.setPosition(Vector3f.from(this.getFloorX(), this.getFloorY(), this.getFloorZ()));
            packet.setVolume(1f);
            packet.setPitch(1f);
            this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, packet);
        }
    }

    //TODO: Transfer the stop sound to the new sound method
    public void stop() {
        if (this.recordItem instanceof ItemMusicDisc itemRecord) {
            final StopSoundPacket packet = new StopSoundPacket();
            packet.setSoundName(itemRecord.getSoundId());
            this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, packet);
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
        this.namedTag = this.namedTag.toBuilder().putCompound("RecordItem", ItemHelper.write(this.recordItem, null)).build();
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putCompound("RecordItem", ItemHelper.write(this.recordItem, null)).build();
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        this.dropItem();
    }
}
