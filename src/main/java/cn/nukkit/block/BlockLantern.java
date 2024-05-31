package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.HANGING;


public class BlockLantern extends BlockFlowable {
    public static final BlockProperties $1 = new BlockProperties(LANTERN, HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLantern() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLantern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Lantern";
    }

    
    /**
     * @deprecated 
     */
    private boolean isBlockAboveValid() {
        Block $2 = up();
        switch (support.getId()) {
            case CHAIN, IRON_BARS, HOPPER -> {
                return true;
            }
            default -> {
                if (support instanceof BlockWallBase || support instanceof BlockFence) {
                    return true;
                }
                if (support instanceof BlockSlab && !((BlockSlab) support).isOnTop()) {
                    return true;
                }
                if (support instanceof BlockStairs && !((BlockStairs) support).isUpsideDown()) {
                    return true;
                }
                return BlockLever.isSupportValid(support, BlockFace.DOWN);
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private boolean isBlockUnderValid() {
        Block $3 = down();
        if (support.getId().equals(HOPPER)) {
            return true;
        }
        if (support instanceof BlockWallBase || support instanceof BlockFence) {
            return true;
        }
        return BlockLever.isSupportValid(support, BlockFace.UP);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean $4 = face != BlockFace.UP && isBlockAboveValid() && (!isBlockUnderValid() || face == BlockFace.DOWN);
        if (!isBlockUnderValid() && !hanging) {
            return false;
        }

        setHanging(hanging);

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isHanging()) {
                if (!isBlockUnderValid()) {
                    level.useBreakOn(this, ItemTool.getBestTool(getToolType()));
                }
            } else if (!isBlockAboveValid()) {
                level.useBreakOn(this, ItemTool.getBestTool(getToolType()));
            }
            return type;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3.5;
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
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return x + (5.0 / 16);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinY() {
        return y + (!isHanging() ? 0 : 1. / 16);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return z + (5.0 / 16);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return x + (11.0 / 16);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return y + (!isHanging() ? 7.0 / 16 : 8.0 / 16);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return z + (11.0 / 16);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
    /**
     * @deprecated 
     */
    

    public boolean isHanging() {
        return getPropertyValue(HANGING);
    }
    /**
     * @deprecated 
     */
    

    public void setHanging(boolean hanging) {
        setPropertyValue(HANGING, hanging);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }
}
