package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
public class BlockRedSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_SANDSTONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedSandstone(BlockState blockstate) {
        super(blockstate);
    }

}
