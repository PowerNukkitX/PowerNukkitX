package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmallAmethystBud extends BlockAmethystBud {
    public static final BlockProperties $1 = new BlockProperties(SMALL_AMETHYST_BUD, CommonBlockProperties.MINECRAFT_BLOCK_FACE);
    /**
     * @deprecated 
     */
    

    public BlockSmallAmethystBud() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSmallAmethystBud(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected String getNamePrefix() {
        return "Small";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 1;
    }
}
