package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType2;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.STONE_SLAB_TYPE_2;

public class BlockDoubleStoneBlockSlab2 extends BlockDoubleSlabBase {
    public static final BlockProperties $1 = new BlockProperties(DOUBLE_STONE_BLOCK_SLAB2,
            CommonBlockProperties.MINECRAFT_VERTICAL_HALF, STONE_SLAB_TYPE_2);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDoubleStoneBlockSlab2() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDoubleStoneBlockSlab2(BlockState blockstate) {
        super(blockstate);
    }

    public StoneSlabType2 getSlabType() {
        return getPropertyValue(STONE_SLAB_TYPE_2);
    }
    /**
     * @deprecated 
     */
    

    public void setSlabType(StoneSlabType2 type) {
        setPropertyValue(STONE_SLAB_TYPE_2, type);
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
        return 30;
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
        return STONE_BLOCK_SLAB2;
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
        Block $2 = Block.get(getSingleSlabId()).setPropertyValues(CommonBlockProperties.STONE_SLAB_TYPE_2.createValue(getSlabType()));
        return new ItemBlock(block);
    }
}