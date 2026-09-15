package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemMusicDisc;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.ServerSoundHandle;
import org.cloudburstmc.protocol.bedrock.data.payload.sound.Stop;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundUpdateSoundDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlaySoundPacket;
import org.cloudburstmc.protocol.bedrock.packet.RecordStartedPacket;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author CreeperFace
 */
public class BlockEntityJukebox extends BlockEntitySpawnable {

    private static final AtomicLong SOUND_HANDLES = new AtomicLong(1);

    private Item recordItem;

    private ServerSoundHandle soundHandle;

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
        return Objects.equals(this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()), Block.JUKEBOX);
    }

    public void setRecordItem(Item recordItem) {
        Objects.requireNonNull(recordItem, "Record item cannot be null");
        this.recordItem = recordItem;
    }

    public Item getRecordItem() {
        return recordItem;
    }


    public void play() {
        if (!(this.recordItem instanceof ItemMusicDisc itemRecord)) {
            return;
        }
        stop();

        final ServerSoundHandle handle = new ServerSoundHandle(SOUND_HANDLES.getAndIncrement());
        this.soundHandle = handle;

        final PlaySoundPacket playSound = new PlaySoundPacket();
        playSound.setName(itemRecord.getSoundId());
        playSound.setPosition(Vector3f.from(this.getFloorX() + 0.5f, this.getFloorY() + 0.5f, this.getFloorZ() + 0.5f));
        playSound.setVolume(1f);
        playSound.setPitch(1f);
        playSound.setLoopCount(0);
        playSound.setBypassListenerRangeCheck(true);
        playSound.setServerSoundHandle(handle);

        final RecordStartedPacket recordStarted = new RecordStartedPacket();
        recordStarted.setBlockPosition(Vector3i.from(this.getFloorX(), this.getFloorY(), this.getFloorZ()));
        recordStarted.setServerSoundHandle(handle);

        this.broadcast(playSound);
        this.broadcast(recordStarted);
    }

    public void stop() {
        final ServerSoundHandle handle = this.soundHandle;
        if (handle == null) {
            return;
        }
        this.soundHandle = null;

        final Stop stop = new Stop();
        final ClientboundUpdateSoundDataPacket packet = new ClientboundUpdateSoundDataPacket();
        packet.setServerSoundHandle(handle);
        packet.setStop(stop);
        packet.setSetVolume(stop);
        packet.setSetPitch(stop);
        packet.setFade(stop);
        packet.setSeekTo(stop);
        packet.setPause(stop);
        packet.setResume(stop);

        this.broadcast(packet);
    }

    private void broadcast(BedrockPacket packet) {
        this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, packet);
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
