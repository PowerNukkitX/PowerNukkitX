package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockBoneBlock extends BlockSolid{
    public static final BlockProperties $1 = new BlockProperties(BONE_BLOCK, CommonBlockProperties.DEPRECATED, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBoneBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBoneBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 10;
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
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bone Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPillarAxis(face.getAxis());
        this.getLevel().setBlock(block, this, true);
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
}