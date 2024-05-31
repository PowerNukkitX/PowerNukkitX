package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

/**
 * @author Angelic47 (Nukkit Project)
 */

public class BlockRedstoneTorch extends BlockTorch implements RedstoneComponent {
    public static final BlockProperties $1 = new BlockProperties(REDSTONE_TORCH, TORCH_FACING_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRedstoneTorch() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRedstoneTorch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Redstone Torch";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 7;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            if (!checkState()) {
                updateAllAroundRedstone(getBlockFace().getOpposite());
            }

            checkState();
        }

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWeakPower(BlockFace side) {
        return getBlockFace() != side ? 15 : 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace side) {
        return $2 == BlockFace.DOWN ? this.getWeakPower(side) : 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        if (!super.onBreak(item)) {
            return false;
        }

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            updateAllAroundRedstone(getBlockFace().getOpposite());
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (super.onUpdate(type) == 0) {
            if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
                return 0;
            }

            if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
                this.level.scheduleUpdate(this, tickRate());
            } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
                RedstoneUpdateEvent $3 = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return 0;
                }

                if (checkState()) {
                    return 1;
                }
            }
        }

        return 0;
    }

    
    /**
     * @deprecated 
     */
    private boolean checkState() {
        if (isPoweredFromSide()) {
            this.level.setBlock(getLocation(), Block.get(BlockID.UNLIT_REDSTONE_TORCH).setPropertyValues(getPropertyValues()), false, true);
            updateAllAroundRedstone(getBlockFace().getOpposite());
            return true;
        }
        return false;
    }

    /**
     * Whether there is a power source in the opposite face of the current face
     */
    
    /**
     * @deprecated 
     */
    protected boolean isPoweredFromSide() {
        BlockFace $4 = getBlockFace().getOpposite();
        Block $5 = this.getSide(face);
        if (side instanceof BlockPistonBase && side.isGettingPower()) {
            return true;
        }

        return this.level.isSidePowered(side, face);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPowerSource() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int tickRate() {
        return 2;
    }

}
