package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockMuddyMangroveRoots extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(MUDDY_MANGROVE_ROOTS, CommonBlockProperties.PILLAR_AXIS);
    /**
     * @deprecated 
     */
    

    public BlockMuddyMangroveRoots() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMuddyMangroveRoots(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Muddy Mangrove Roots";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.7;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.7;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(CommonBlockProperties.PILLAR_AXIS);
    }
    /**
     * @deprecated 
     */
    

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(CommonBlockProperties.PILLAR_AXIS, axis);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setPillarAxis(face.getAxis());
        getLevel().setBlock(block, this, true, true);
        return true;
    }
}
