package cn.nukkit.block;

import cn.nukkit.block.property.enums.PrismarineBlockType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PRISMARINE_BLOCK_TYPE;


public class BlockPrismarine extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(PRISMARINE, PRISMARINE_BLOCK_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPrismarine() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPrismarine(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 30;
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
    
    public String getName() {
        return switch (getPrismarineBlockType()) {
            case DEFAULT -> "Prismarine";
            case DARK -> "Dark Prismarine";
            case BRICKS -> "Prismarine Bricks";
        };
    }
    /**
     * @deprecated 
     */
    

    public void setPrismarineBlockType(PrismarineBlockType prismarineBlockType) {
        setPropertyValue(PRISMARINE_BLOCK_TYPE, prismarineBlockType);
    }

    public PrismarineBlockType getPrismarineBlockType() {
        return getPropertyValue(PRISMARINE_BLOCK_TYPE);
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
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getBlockState(PRISMARINE_BLOCK_TYPE.createValue(getPropertyValue(PRISMARINE_BLOCK_TYPE))).toBlock());
    }
}
