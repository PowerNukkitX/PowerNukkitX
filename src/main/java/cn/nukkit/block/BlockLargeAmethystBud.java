package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLargeAmethystBud extends BlockAmethystBud {
    public static final BlockProperties $1 = new BlockProperties(LARGE_AMETHYST_BUD, CommonBlockProperties.MINECRAFT_BLOCK_FACE);
    /**
     * @deprecated 
     */
    

    public BlockLargeAmethystBud() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLargeAmethystBud(BlockState blockState) {
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
        return "Large";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 4;
    }
}
