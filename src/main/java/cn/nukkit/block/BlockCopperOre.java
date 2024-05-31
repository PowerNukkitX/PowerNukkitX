package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCopperOre extends BlockOre {
    public static final BlockProperties $1 = new BlockProperties(COPPER_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCopperOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCopperOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Copper Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    protected @Nullable 
    /**
     * @deprecated 
     */
    String getRawMaterial() {
        return ItemID.RAW_COPPER;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getDropMultiplier() {
        return 3;
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
        return 3;
    }
}