package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedSlab extends BlockSlab {
    public static final BlockProperties $1 = new BlockProperties(WARPED_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedSlab(BlockState blockstate) {
        super(blockstate, WARPED_DOUBLE_SLAB);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Warped";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem()
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 0;
    }

}