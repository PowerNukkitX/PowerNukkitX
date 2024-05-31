package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockWarpedWartBlock extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(WARPED_WART_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedWartBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedWartBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Warped Wart Block";
    }

    // TODO Fix it in https://github.com/PowerNukkit/PowerNukkit/pull/370, the same for BlockNetherWartBlock
    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY; //TODO Correct type is hoe
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1;
    }

}
