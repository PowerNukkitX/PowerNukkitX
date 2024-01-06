package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLargeAmethystBud extends BlockAmethystBud {
    public static final BlockProperties PROPERTIES = new BlockProperties(LARGE_AMETHYST_BUD, CommonBlockProperties.MINECRAFT_BLOCK_FACE);

    public BlockLargeAmethystBud() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLargeAmethystBud(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    protected String getNamePrefix() {
        return "Large";
    }

    @Override
    public int getLightLevel() {
        return 4;
    }
}
