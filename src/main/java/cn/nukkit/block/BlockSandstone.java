package cn.nukkit.block;

import cn.nukkit.block.property.enums.SandStoneType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.SAND_STONE_TYPE;

public class BlockSandstone extends BlockSolid {

    public static final BlockProperties $1 = new BlockProperties(SANDSTONE, SAND_STONE_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSandstone() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSandstone(BlockState state) {
        super(state);
    }
    /**
     * @deprecated 
     */
    

    public void setSandstoneType(SandStoneType sandStoneType) {
        setPropertyValue(SAND_STONE_TYPE, sandStoneType);
    }

    public SandStoneType getSandstoneType() {
        return getPropertyValue(SAND_STONE_TYPE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return SandStoneType.SMOOTH.equals(getSandstoneType()) ? 2 : 0.8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return SandStoneType.SMOOTH.equals(getSandstoneType()) ? 6 : 0.8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return switch(getSandstoneType()) {
            case CUT -> "Cut Sandstone";
            case DEFAULT -> "Sandstone";
            case HEIROGLYPHS -> "Chiseled Sandstone";
            case SMOOTH -> "Smooth Sandstone";
        };
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
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, getSandstoneType().ordinal());
    }
}
