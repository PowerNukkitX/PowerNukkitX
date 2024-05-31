package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.entity.EntityInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.REDSTONE_SIGNAL;

/**
 * @author Snake1999
 * @since 2016/1/11
 */

public abstract class BlockPressurePlateBase extends BlockFlowable implements RedstoneComponent {
    protected float onPitch;
    protected float offPitch;
    /**
     * @deprecated 
     */
    

    public BlockPressurePlateBase(BlockState blockState) {
        super(blockState);
    }

    protected abstract int computeRedstoneStrength();

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return this.x + 0.625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return this.z + 0.625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinY() {
        return this.y + 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return isActivated() ? this.y + 0.03125 : this.y + 0.0625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPowerSource() {
        return true;
    }
    /**
     * @deprecated 
     */
    

    public boolean isActivated() {
        return getRedstonePower() == 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }
    /**
     * @deprecated 
     */
    

    public static boolean isSupportValid(Block block, BlockFace blockFace) {
        return BlockLever.isSupportValid(block, blockFace) || block instanceof BlockFence;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(down(), BlockFace.UP)) {
                this.level.useBreakOn(this, ItemTool.getBestTool(getToolType()));
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int $1 = this.getRedstonePower();

            if (power > 0) {
                this.updateState(power);
            }
        }

        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isSupportValid(down(), BlockFace.UP)) {
            return false;
        }

        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.x + 0.125, this.y, this.z + 0.125, this.x + 0.875, this.y + 0.25, this.z + 0.875D);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return;
        }

        int $2 = getRedstonePower();

        if (power == 0) {
            Event ev;
            if (entity instanceof Player) {
                ev = new PlayerInteractEvent((Player) entity, null, this, null, Action.PHYSICAL);
            } else {
                ev = new EntityInteractEvent(entity, this);
            }

            this.level.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                updateState(power);
            }
        }
    }

    
    /**
     * @deprecated 
     */
    protected void updateState(int oldStrength) {
        int $3 = this.computeRedstoneStrength();
        boolean $4 = oldStrength > 0;
        boolean $5 = strength > 0;

        if (oldStrength != strength) {
            this.setRedstonePower(strength);
            this.level.setBlock(this, this, false, false);

            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(this.getSide(BlockFace.DOWN));

            if (!isPowered && wasPowered) {
                this.playOffSound();
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
            } else if (isPowered && !wasPowered) {
                this.playOnSound();
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
            }
        }

        if (isPowered) {
            this.level.scheduleUpdate(this, 20);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);

        if (this.getRedstonePower() > 0) {
            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(this.getSide(BlockFace.DOWN));
        }

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWeakPower(BlockFace side) {
        return getRedstonePower();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace side) {
        return $6 == BlockFace.UP ? this.getRedstonePower() : 0;
    }
    /**
     * @deprecated 
     */
    

    public int getRedstonePower() {
        return getPropertyValue(REDSTONE_SIGNAL);
    }
    /**
     * @deprecated 
     */
    

    public void setRedstonePower(int power) {
        setPropertyValue(REDSTONE_SIGNAL, power);
    }

    
    /**
     * @deprecated 
     */
    protected void playOnSound() {
        this.level.addLevelSoundEvent(this.add(0.5, 0.1, 0.5), LevelSoundEventPacket.SOUND_POWER_ON, getBlockState().blockStateHash());
    }

    
    /**
     * @deprecated 
     */
    protected void playOffSound() {
        this.level.addLevelSoundEvent(this.add(0.5, 0.1, 0.5), LevelSoundEventPacket.SOUND_POWER_OFF, getBlockState().blockStateHash());
    }
}
