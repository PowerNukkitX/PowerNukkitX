package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

public class BlockUnlitRedstoneTorch extends BlockTorch implements RedstoneComponent {
    public static final BlockProperties $1 = new BlockProperties(UNLIT_REDSTONE_TORCH, CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockUnlitRedstoneTorch() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockUnlitRedstoneTorch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Unlit Redstone Torch";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWeakPower(BlockFace side) {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace side) {
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.REDSTONE_TORCH));
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
                RedstoneUpdateEvent $2 = new RedstoneUpdateEvent(this);
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
        if (!this.isPoweredFromSide()) {
            this.level.setBlock(getLocation(), Block.get(BlockID.REDSTONE_TORCH).setPropertyValues(getPropertyValues()), false, true);
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
        BlockFace $3 = getBlockFace().getOpposite();
        Block $4 = this.getSide(face);
        if (side instanceof BlockPistonBase && side.isGettingPower()) {
            return true;
        }

        return this.level.isSidePowered(side, face);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int tickRate() {
        return 2;
    }
}