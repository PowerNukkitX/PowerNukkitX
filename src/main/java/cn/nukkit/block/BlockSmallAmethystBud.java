package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmallAmethystBud extends BlockAmethystBud {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMALL_AMETHYST_BUD, CommonBlockProperties.MINECRAFT_BLOCK_FACE);

    public BlockSmallAmethystBud() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmallAmethystBud(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    protected String getNamePrefix() {
        return "Small";
    }

    @Override
    public int getLightLevel() {
        return 1;
    }
}
