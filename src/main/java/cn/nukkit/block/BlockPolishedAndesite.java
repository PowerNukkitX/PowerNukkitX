package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedAndesite extends BlockStone {
    public static final BlockProperties $1 = new BlockProperties(POLISHED_ANDESITE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedAndesite() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedAndesite(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public StoneType stoneType() {
        return StoneType.ANDESITE_SMOOTH;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return false;
    }
}