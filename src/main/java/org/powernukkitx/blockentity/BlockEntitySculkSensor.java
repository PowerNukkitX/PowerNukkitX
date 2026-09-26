package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockSculkSensor;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationListener;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author Kevims KCodeYT
 */
public class BlockEntitySculkSensor extends BlockEntity implements VibrationListener {
    protected int lastActiveTime = getLevel().getTick();
    protected VibrationEvent lastVibrationEvent;
    protected int power = 0;
    protected int comparatorPower = 0;

    public BlockEntitySculkSensor(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.level.getVibrationManager().addListener(this);
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        if (!isSilkTouch) {
            this.level.getVibrationManager().removeListener(this);
        } else {
            calPower();
        }
    }

    @Override
    public void close() {
        this.level.getVibrationManager().removeListener(this);
        super.close();
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_SENSOR;
    }

    @Override
    public Position getListenerVector() {
        return this.getPosition().setLevel(this.level).floor().add(0.5f, 0.5f, 0.5f);
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        if (this.isBlockEntityValid() && level.getServer().getSettings().gameplaySettings().enableRedstone() && event.type().isVibration() && !(this.level.getBlock(event.source()) instanceof BlockSculkSensor)) {
            return ((BlockSculkSensor) getBlock()).isInactive();
        } else {
            return false;
        }
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        if (this.level != null && this.isBlockEntityValid() && level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            this.lastVibrationEvent = event;
            this.updateLastActiveTime();

            calPower();

            var block = (BlockSculkSensor) this.getBlock();
            block.setPhase(1);
            block.updateAroundRedstone();
            level.scheduleUpdate(block, 30);
            level.getVibrationManager().callVibrationEvent(new VibrationEvent(
                    event.initiator(), getListenerVector(), VibrationType.SCULK_SENSOR_TENDRILS_CLICKING, event.sourceUniqueId(), event.projectileOwnerUniqueId()));
        }
    }

    public VibrationEvent getLastVibrationEvent() {
        return this.lastVibrationEvent;
    }

    public int getLastActiveTime() {
        return this.lastActiveTime;
    }

    public int getPower() {
        return power;
    }

    public int getComparatorPower() {
        return comparatorPower;
    }

    /**
     * Clears both direct and comparator redstone power.
     */
    public void clearPower() {
        power = 0;
        comparatorPower = 0;
    }

    @Override
    public double getListenRange() {
        return 8;
    }

    @Override
    public boolean canReceiveOnlyIfAdjacentChunksAreTicking() {
        return true;
    }

    protected void updateLastActiveTime() {
        this.lastActiveTime = getLevel().getTick();
    }

    public void calPower() {
        var event = this.getLastVibrationEvent();
        if ((this.level.getTick() - this.getLastActiveTime()) >= 40 || event == null) {
            power = 0;
            comparatorPower = 0;
            return;
        }
        comparatorPower = event.type().frequency;
        power = Math.max(1, 15 - (int) Math.floor(event.source().distance(getListenerVector()) / getListenRange() * 15));
    }
}
