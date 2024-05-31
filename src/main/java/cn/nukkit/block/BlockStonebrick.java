package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneBrickType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.STONE_BRICK_TYPE;

public class BlockStonebrick extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(STONEBRICK, STONE_BRICK_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStonebrick() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStonebrick(BlockState blockstate) {
        super(blockstate);
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
    /**
     * @deprecated 
     */
    

    public void setBrickStoneType(StoneBrickType stoneBrickType) {
        setPropertyValue(STONE_BRICK_TYPE, stoneBrickType);
    }

    public StoneBrickType getStoneBrickType() {
        return getPropertyValue(STONE_BRICK_TYPE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return getStoneBrickType().name();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[] {
                    toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
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
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getBlockState(STONE_BRICK_TYPE.createValue(getPropertyValue(STONE_BRICK_TYPE))).toBlock());
    }
}