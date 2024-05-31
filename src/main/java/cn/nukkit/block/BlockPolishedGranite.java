package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedGranite extends BlockStone {
    public static final BlockProperties $1 = new BlockProperties(POLISHED_GRANITE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedGranite() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedGranite(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public StoneType stoneType() {
        return StoneType.GRANITE_SMOOTH;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return false;
    }
}