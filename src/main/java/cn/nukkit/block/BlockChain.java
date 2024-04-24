package cn.nukkit.block;


import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class BlockChain extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHAIN, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChain() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockChain(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Chain";
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(CommonBlockProperties.PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(CommonBlockProperties.PILLAR_AXIS, axis);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setPillarAxis(face.getAxis());
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getMinX() {
        return x + 7/16.0;
    }

    @Override
    public double getMaxX() {
        return x + 9/16.0;
    }

    @Override
    public double getMinZ() {
        return z + 7/16.0;
    }

    @Override
    public double getMaxZ() {
        return z + 9/16.0;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
}
