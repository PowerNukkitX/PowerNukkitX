package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.BlockCalibratedSculkSensor;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSculkSensor;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationListener;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityCalibratedSculkSensor extends BlockEntity implements VibrationListener {

    protected int lastActiveTime = getLevel().getTick();
    protected VibrationEvent lastVibrationEvent;

    protected int power = 0;

    protected int comparatorPower = 0;

    protected boolean waitForVibration = false;


    public BlockEntityCalibratedSculkSensor(IChunk chunk, CompoundTag nbt) {
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
        return getLevelBlock().getId() == BlockID.CALIBRATED_SCULK_SENSOR;
    }

    @Override
    public Position getListenerVector() {
        return this.getPosition().setLevel(this.level).floor().add(0.5f, 0.5f, 0.5f);
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        if (this.isBlockEntityValid() && level.getServer().getSettings().gameplaySettings().enableRedstone() && !(this.level.getBlock(event.source()) instanceof BlockCalibratedSculkSensor)) {
            boolean canBeActive = (getLevel().getTick() - lastActiveTime) > 40 && !waitForVibration;
            if (canBeActive) waitForVibration = true;
            return canBeActive;
        } else {
            return false;
        }
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        if (this.level != null && this.isBlockEntityValid() && level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            this.lastVibrationEvent = event;
            this.updateLastActiveTime();
            waitForVibration = false;

            calPower();

            var block = (BlockCalibratedSculkSensor) this.getBlock();
            block.setPhase(1);
            block.updateAroundRedstone();
            level.scheduleUpdate(block, 41);
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

    @Override
    public double getListenRange() {
        return 8;
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
        power = Math.max(1, 15 - (int) Math.floor(event.source().distance(this.add(0.5, 0.5, 0.5)) * 1.875));
    }
}

