package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.item.Item;
import org.powernukkitx.entity.passive.EntityParrot;
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
    private int playbackEndsAt;

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
        int duration = itemRecord.getDurationTicks();
        playbackEndsAt = duration > 0 ? level.getTick() + duration : Integer.MAX_VALUE;
        scheduleUpdate();

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
        notifyParrots(true);
    }

    /** @return whether this jukebox has an active record playback */
    public boolean isPlaying() {
        return !closed && level != null && soundHandle != null && recordItem instanceof ItemMusicDisc
                && level.getTick() < playbackEndsAt;
    }

    public void stop() {
        final ServerSoundHandle handle = this.soundHandle;
        if (handle == null) {
            return;
        }
        this.soundHandle = null;
        notifyParrots(false);

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

    @Override
    public boolean onUpdate() {
        super.onUpdate();
        if (closed) return false;
        if (soundHandle == null) return false;
        if (!isPlaying()) {
            stop();
            return false;
        }
        if (level.getTick() % 20 == 0) notifyParrots(true);
        return true;
    }

    @Override
    public synchronized void close() {
        if (!closed) stop();
        super.close();
    }

    private void notifyParrots(boolean playing) {
        for (var entity : level.fastNearbyEntities(new org.powernukkitx.math.SimpleAxisAlignedBB(
                x - 3, y - 3, z - 3, x + 4, y + 4, z + 4))) {
            if (entity instanceof EntityParrot parrot) parrot.onRecordPlaying(this, playing);
        }
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

        if (this.recordItem == null || this.recordItem.isNull()) {
            this.nbt.remove("RecordItem");
        } else {
            this.nbt.putCompound("RecordItem", ItemHelper.write(this.recordItem));
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound().putCompound("RecordItem", ItemHelper.write(this.recordItem, null));
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        this.dropItem();
    }
}
