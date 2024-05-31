package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType3;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleStoneBlockSlab3 extends BlockDoubleSlabBase {
    public static final BlockProperties $1 = new BlockProperties(DOUBLE_STONE_BLOCK_SLAB3, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_3);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDoubleStoneBlockSlab3() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDoubleStoneBlockSlab3(BlockState blockstate) {
        super(blockstate);
    }

    public StoneSlabType3 getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_3);
    }
    /**
     * @deprecated 
     */
    

    public void setSlabType(StoneSlabType3 type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_3, type);
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
        return STONE_BLOCK_SLAB3;
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
        Block $2 = Block.get(getSingleSlabId()).setPropertyValues(CommonBlockProperties.STONE_SLAB_TYPE_3.createValue(getSlabType()));
        return new ItemBlock(block);
    }

}