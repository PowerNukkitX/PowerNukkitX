package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSculkSensor;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationListener;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Kevims KCodeYT
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockEntitySculkSensor extends BlockEntity implements VibrationListener {

    protected int lastActiveTime = Server.getInstance().getTick();
    protected VibrationEvent lastVibrationEvent;

    protected int power = 0;

    protected int comparatorPower = 0;

    protected boolean waitForVibration = false;

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public BlockEntitySculkSensor(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.level.getVibrationManager().addListener(this);
    }

    @Override
    public void onBreak() {
        this.level.getVibrationManager().removeListener(this);
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
        return this.clone().setLevel(this.level).floor().add(0.5f, 0.5f, 0.5f);
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        if (this.isBlockEntityValid() && level.getServer().isRedstoneEnabled() && !(this.level.getBlock(event.source()) instanceof BlockSculkSensor)) {
            boolean canBeActive = (Server.getInstance().getTick() - lastActiveTime) > 40 && !waitForVibration;
            if (canBeActive) waitForVibration = true;
            return canBeActive;
        } else {
            return false;
        }
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        if (this.level != null && this.isBlockEntityValid() && level.getServer().isRedstoneEnabled()) {
            this.lastVibrationEvent = event;
            this.updateLastActiveTime();
            waitForVibration = false;

            calPower();

            var block = (BlockSculkSensor) this.getBlock();
            block.setPowered(true);
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
        this.lastActiveTime = Server.getInstance().getTick();
    }

    public void calPower() {
        var event = this.getLastVibrationEvent();
        if ((this.level.getServer().getTick() - this.getLastActiveTime()) >= 40 || event == null) {
            power = 0;
            comparatorPower = 0;
            return;
        }
        comparatorPower = event.type().frequency;
        power = Math.max(1, 15 - (int) Math.floor(event.source().distance(this.add(0.5, 0.5, 0.5)) * 1.875));
    }
}
