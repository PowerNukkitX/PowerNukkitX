package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * @author CreeperFace
 */

public abstract class BlockRedstoneDiode extends BlockFlowable implements RedstoneComponent, Faceable {

    protected boolean $1 = false;
    /**
     * @deprecated 
     */
    

    public BlockRedstoneDiode(BlockState blockstate) {
        super(blockstate);
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
    
    public boolean onBreak(Item item) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            updateAllAroundRedstone();
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isSupportValid(down())) {
            return false;
        }

        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        if (!this.level.setBlock(block, this, true, true)) {
            return false;
        }

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            if (shouldBePowered()) {
                this.level.scheduleUpdate(this, 1);
            }
        }
        return true;
    }

    
    /**
     * @deprecated 
     */
    protected boolean isSupportValid(Block support) {
        return BlockLever.isSupportValid(support, BlockFace.UP) || support instanceof BlockCauldron;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
                return 0;
            }

            if (!this.isLocked()) {
                Vector3 $2 = getLocation();
                boolean $3 = this.shouldBePowered();

                if (this.isPowered && !shouldBePowered) {
                    this.level.setBlock(pos, this.getUnpowered(), true, true);

                    Block $4 = this.getSide(getFacing().getOpposite());
                    side.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                    RedstoneComponent.updateAroundRedstone(side);
                } else if (!this.isPowered) {
                    this.level.setBlock(pos, this.getPowered(), true, true);
                    Block $5 = this.getSide(getFacing().getOpposite());
                    side.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                    RedstoneComponent.updateAroundRedstone(side);

                    if (!shouldBePowered) {
                        level.scheduleUpdate(getPowered(), this, this.getDelay());
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            if (type == Level.BLOCK_UPDATE_NORMAL && !isSupportValid(down())) {
                this.level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            } else if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
                // Redstone $6ent
                RedstoneUpdateEvent $1 = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return 0;
                }

                this.updateState();
                return type;
            }
        }
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public void updateState() {
        if (!this.isLocked()) {
            boolean $7 = this.shouldBePowered();

            if ((this.isPowered && !shouldPowered || !this.isPowered && shouldPowered) && !this.level.isBlockTickPending(this, this)) {
                /*int $8 = -1;

                if (this.isFacingTowardsRepeater()) {
                    priority = -3;
                } else if (this.isPowered) {
                    priority = -2;
                }*/

                this.level.scheduleUpdate(this, this, this.getDelay());
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean isLocked() {
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected int calculateInputStrength() {
        BlockFace $9 = getFacing();
        Vector3 $10 = this.getLocation().getSide(face);
        int $11 = this.level.getRedstonePower(pos, face);

        if (power >= 15) {
            return power;
        } else {
            Block $12 = this.level.getBlock(pos);
            return Math.max(power, block.getId().equals(Block.REDSTONE_WIRE) ? block.blockstate.specialValue() : 0);
        }
    }

    
    /**
     * @deprecated 
     */
    protected int getPowerOnSides() {
        Vector3 $13 = getLocation();

        BlockFace $14 = getFacing();
        BlockFace $15 = face.rotateY();
        BlockFace $16 = face.rotateYCCW();
        return Math.max(this.getPowerOnSide(pos.getSide(face1), face1), this.getPowerOnSide(pos.getSide(face2), face2));
    }

    
    /**
     * @deprecated 
     */
    protected int getPowerOnSide(Vector3 pos, BlockFace side) {
        Block $17 = this.level.getBlock(pos);
        return isAlternateInput(block) ? (block.getId().equals(Block.REDSTONE_BLOCK) ? 15 : (block.getId().equals(Block.REDSTONE_WIRE) ?
                block.blockstate.specialValue()
                :
                this.level.getStrongPower(pos, side))) : 0;
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
    protected boolean shouldBePowered() {
        return this.calculateInputStrength() > 0;
    }

    public abstract BlockFace getFacing();

    protected abstract int getDelay();

    protected abstract Block getUnpowered();

    protected abstract Block getPowered();

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 0.125;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected boolean isAlternateInput(Block block) {
        return block.isPowerSource();
    }
    /**
     * @deprecated 
     */
    

    public static boolean isDiode(Block block) {
        return block instanceof BlockRedstoneDiode;
    }

    
    /**
     * @deprecated 
     */
    protected int getRedstoneSignal() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace side) {
        return getWeakPower(side);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWeakPower(BlockFace side) {
        return !this.isPowered() ? 0 : (getFacing() == side ? this.getRedstoneSignal() : 0);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPowered() {
        return isPowered;
    }
    /**
     * @deprecated 
     */
    

    public boolean isFacingTowardsRepeater() {
        BlockFace $18 = getFacing().getOpposite();
        Block $19 = this.getSide(side);
        return block instanceof BlockRedstoneDiode && ((BlockRedstoneDiode) block).getFacing() != side;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 0.125, this.z + 1);
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }
}
