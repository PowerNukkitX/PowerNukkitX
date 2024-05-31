package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType4;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleStoneBlockSlab4 extends BlockDoubleSlabBase {
    public static final BlockProperties $1 = new BlockProperties(DOUBLE_STONE_BLOCK_SLAB4, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_4);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDoubleStoneBlockSlab4() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDoubleStoneBlockSlab4(BlockState blockstate) {
        super(blockstate);
    }

    public StoneSlabType4 getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4);
    }
    /**
     * @deprecated 
     */
    

    public void setSlabType(StoneSlabType4 type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4, type);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return getSlabType().name();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return getToolType() > ItemTool.TIER_WOODEN ? 30 : 15;
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
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSingleSlabId() {
        return STONE_BLOCK_SLAB4;
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
        Block $2 = Block.get(getSingleSlabId()).setPropertyValues(CommonBlockProperties.STONE_SLAB_TYPE_4.createValue(getSlabType()));
        return new ItemBlock(block);
    }
}