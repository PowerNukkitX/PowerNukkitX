package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSculkSensor;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationListener;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.RedstoneComponent;

/**
 * @author Kevims KCodeYT
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockEntitySculkSensor extends BlockEntity implements VibrationListener {

    protected int lastActiveTime = Server.getInstance().getTick();
    protected VibrationEvent lastVibrationEvent;

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
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_SENSOR;
    }

    @Override
    public Position getListenerPosition() {
        return this.clone().setLevel(this.level).floor().add(0.5f, 0.5f, 0.5f);
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        boolean canBeActive = (Server.getInstance().getTick() - lastActiveTime) > 40 && event.source().distanceSquared(this.add(0.5, 0.5, 0.5)) <= 64;//8*8
        if (canBeActive) updateLastActiveTime();
        return canBeActive;
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        this.lastVibrationEvent = event;
        ((BlockSculkSensor) this.getBlock()).onVibrationArrive();
    }

    public VibrationEvent getLastVibrationEvent() {
        return this.lastVibrationEvent;
    }

    public int getLastActiveTime() {
        return this.lastActiveTime;
    }

    protected void updateLastActiveTime() {
        this.lastActiveTime = Server.getInstance().getTick();
    }
}
