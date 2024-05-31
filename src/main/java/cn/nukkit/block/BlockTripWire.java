package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemShears;
import cn.nukkit.item.ItemString;
import cn.nukkit.level.Level;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.BlockTripwireHook.MAX_TRIPWIRE_CIRCUIT_LENGTH;
import static cn.nukkit.block.property.CommonBlockProperties.ATTACHED_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.DISARMED_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.POWERED_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.SUSPENDED_BIT;

public class BlockTripWire extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(TRIP_WIRE,
            POWERED_BIT, SUSPENDED_BIT, ATTACHED_BIT, DISARMED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTripWire() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTripWire(BlockState state) {
        super(state);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Tripwire";
    }

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
    
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public Item toItem() {
        return new ItemString();
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getItemId() {
        return ItemID.STRING;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPowered() {
        return this.getPropertyValue(POWERED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public boolean isAttached() {
        return this.getPropertyValue(ATTACHED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public boolean isSuspended() {
        return this.getPropertyValue(SUSPENDED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public boolean isDisarmed() {
        return this.getPropertyValue(DISARMED_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setPowered(boolean isPowered) {
        if (this.isPowered() == isPowered) {
            return;
        }
        this.setPropertyValue(POWERED_BIT, isPowered);
    }
    /**
     * @deprecated 
     */
    

    public void setAttached(boolean isAttached) {
        if (this.isAttached() == isAttached) {
            return;
        }
        this.setPropertyValue(ATTACHED_BIT, isAttached);
    }
    /**
     * @deprecated 
     */
    

    public void setDisarmed(boolean isDisarmed) {
        if (this.isDisarmed() == isDisarmed) {
            return;
        }
        this.setPropertyValue(DISARMED_BIT, isDisarmed);
    }
    /**
     * @deprecated 
     */
    

    public void setSuspended(boolean isSuspended) {
        if (this.isSuspended() == isSuspended) {
            return;
        }
        this.setPropertyValue(SUSPENDED_BIT, isSuspended);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return;
        }
        if (!entity.doesTriggerPressurePlate()) {
            return;
        }
        if (this.isPowered()) {
            return;
        }

        this.setPowered(true);
        this.level.setBlock(this, this, true, false);
        this.updateHook(false);

        this.level.scheduleUpdate(this, 10);
        this.level.updateComparatorOutputLevelSelective(this, true);
    }

    
    /**
     * @deprecated 
     */
    private void updateHook(boolean scheduleUpdate) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return;
        }

        for (BlockFace side : new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST}) {
            for ($2nt $1 = 1; i < MAX_TRIPWIRE_CIRCUIT_LENGTH; ++i) {
                Block $3 = this.getSide(side, i);

                if (block instanceof BlockTripwireHook hook) {
                    if (hook.getFacing() == side.getOpposite()) {
                        hook.updateLine(false, true, i, this);
                    }

                    /*if(scheduleUpdate) {
                        this.level.scheduleUpdate(hook, 10);
                    }*/
                    break;
                }

                if (!(block instanceof BlockTripWire)) {
                    break;
                }
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isPowered()) {
                return type;
            }

            for (Entity entity : this.level.getCollidingEntities(this.getCollisionBoundingBox())) {
                if (!entity.doesTriggerPressurePlate()) {
                    continue;
                }
                this.level.scheduleUpdate(this, 10);
                return type;
            }

            this.setPowered(false);
            this.level.setBlock(this, this, true, false);
            this.updateHook(false);

            this.level.updateComparatorOutputLevelSelective(this, true);

            return type;
        }

        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        this.getLevel().setBlock(this, this, true, true);
        this.updateHook(false);

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        if (item instanceof ItemShears) {
            this.setDisarmed(true);
            this.level.setBlock(this, this, true, false);
            this.updateHook(false);
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
            //todo: initiator should be a entity
            level.getVibrationManager().callVibrationEvent(new VibrationEvent(
                    this, this.add(0.5, 0.5, 0.5), VibrationType.SHEAR));
            return true;
        }

        this.setPowered(true);
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        this.updateHook(true);

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 0.5;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }
}
