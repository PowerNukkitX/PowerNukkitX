package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.copper.chain.AbstractBlockCopperChain;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;


public class BlockLantern extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(LANTERN, HANGING);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .hardness(3.5)
            .resistance(3.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .lightEmission(15)
            .canPassThrough(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLantern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLantern(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockLantern(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Lantern";
    }

    private boolean isBlockAboveValid() {
        Block support = up();
        switch (support.getId()) {
            case IRON_CHAIN, IRON_BARS, HOPPER -> {
                return true;
            }
            default -> {
                if (support instanceof AbstractBlockCopperChain) {
                    return true;
                }
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

    private boolean isBlockUnderValid() {
        Block support = down();
        if (support.getId().equals(HOPPER)) {
            return true;
        }
        if (support instanceof BlockWallBase || support instanceof BlockFence) {
            return true;
        }
        return BlockLever.isSupportValid(support, BlockFace.UP);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean hanging = face != BlockFace.UP && isBlockAboveValid() && (!isBlockUnderValid() || face == BlockFace.DOWN);
        if (!isBlockUnderValid() && !hanging) {
            return false;
        }

        setHanging(hanging);

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
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
    public boolean canHarvestWithHand() {
        return false;
    }

    
    @Override
    public double getMinX() {
        return x + (5.0 / 16);
    }

    @Override
    public double getMinY() {
        return y + (!isHanging() ? 0 : 1. / 16);
    }

    @Override
    public double getMinZ() {
        return z + (5.0 / 16);
    }

    @Override
    public double getMaxX() {
        return x + (11.0 / 16);
    }

    @Override
    public double getMaxY() {
        return y + (!isHanging() ? 7.0 / 16 : 8.0 / 16);
    }

    @Override
    public double getMaxZ() {
        return z + (11.0 / 16);
    }

    
    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    
    public boolean isHanging() {
        return getPropertyValue(HANGING);
    }

    public void setHanging(boolean hanging) {
        setPropertyValue(HANGING, hanging);
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}
