package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

public class BlockLightningRod extends BlockTransparent implements Faceable {
    public static final BlockProperties $1 = new BlockProperties(LIGHTNING_ROD, CommonBlockProperties.FACING_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockLightningRod() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightningRod(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "LightningRod";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        this.setBlockFace(face);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}
