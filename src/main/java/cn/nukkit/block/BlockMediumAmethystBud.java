package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMediumAmethystBud extends BlockAmethystBud {
    public static final BlockProperties PROPERTIES = new BlockProperties(MEDIUM_AMETHYST_BUD, CommonBlockProperties.MINECRAFT_BLOCK_FACE);

    public BlockMediumAmethystBud() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMediumAmethystBud(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    protected String getNamePrefix() {
        return "Medium";
    }

    @Override
    public int getLightLevel() {
        return 2;
    }
}
