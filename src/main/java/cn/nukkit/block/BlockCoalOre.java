package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoalOre extends BlockOre {
    public static final BlockProperties $1 = new BlockProperties(COAL_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCoalOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCoalOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Coal Ore";
    }

    @Override
    protected @Nullable 
    /**
     * @deprecated 
     */
    String getRawMaterial() {
        return ItemID.COAL;
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
    
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(3);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3;
    }
}