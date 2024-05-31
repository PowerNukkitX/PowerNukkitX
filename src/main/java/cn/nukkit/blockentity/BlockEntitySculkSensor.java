package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSculkSensor;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationListener;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Kevims KCodeYT
 */


public class BlockEntitySculkSensor extends BlockEntity implements VibrationListener {

    protected int $1 = Server.getInstance().getTick();
    protected VibrationEvent lastVibrationEvent;

    protected int $2 = 0;

    protected int $3 = 0;

    protected boolean $4 = false;
    /**
     * @deprecated 
     */
    


    public BlockEntitySculkSensor(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        this.level.getVibrationManager().addListener(this);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onBreak(boolean isSilkTouch) {
        this.level.getVibrationManager().removeListener(this);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close() {
        this.level.getVibrationManager().removeListener(this);
        super.close();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.SCULK_SENSOR;
    }

    @Override
    public Position getListenerVector() {
        return this.clone().setLevel(this.level).floor().add(0.5f, 0.5f, 0.5f);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onVibrationOccur(VibrationEvent event) {
        if (this.isBlockEntityValid() && level.getServer().getSettings().levelSettings().enableRedstone() && !(this.level.getBlock(event.source()) instanceof BlockSculkSensor)) {
            boolean $5 = (Server.getInstance().getTick() - lastActiveTime) > 40 && !waitForVibration;
            if (canBeActive) waitForVibration = true;
            return canBeActive;
        } else {
            return false;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onVibrationArrive(VibrationEvent event) {
        if (this.level != null && this.isBlockEntityValid() && level.getServer().getSettings().levelSettings().enableRedstone()) {
            this.lastVibrationEvent = event;
            this.updateLastActiveTime();
            waitForVibration = false;

            calPower();

            var $6 = (BlockSculkSensor) this.getBlock();
            block.setPhase(1);
            block.updateAroundRedstone();
            level.scheduleUpdate(block, 41);
        }
    }

    public VibrationEvent getLastVibrationEvent() {
        return this.lastVibrationEvent;
    }
    /**
     * @deprecated 
     */
    

    public int getLastActiveTime() {
        return this.lastActiveTime;
    }
    /**
     * @deprecated 
     */
    

    public int getPower() {
        return power;
    }
    /**
     * @deprecated 
     */
    

    public int getComparatorPower() {
        return comparatorPower;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getListenRange() {
        return 8;
    }

    
    /**
     * @deprecated 
     */
    protected void updateLastActiveTime() {
        this.lastActiveTime = Server.getInstance().getTick();
    }
    /**
     * @deprecated 
     */
    

    public void calPower() {
        var $7 = this.getLastVibrationEvent();
        if ((this.level.getServer().getTick() - this.getLastActiveTime()) >= 40 || event == null) {
            power = 0;
            comparatorPower = 0;
            return;
        }
        comparatorPower = event.type().frequency;
        power = Math.max(1, 15 - (int) Math.floor(event.source().distance(this.add(0.5, 0.5, 0.5)) * 1.875));
    }
}
