package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
public class BlockRedSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_SANDSTONE, CommonBlockProperties.SAND_STONE_TYPE);

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

    @Override
    public String getName() {
        return switch (getSandstoneType()) {
            case CUT -> "Cut Red Sandstone";
            case DEFAULT -> "Red Sandstone";
            case HEIROGLYPHS -> "Chiseled Red Sandstone";
            case SMOOTH -> "Smooth Red Sandstone";
        };
    }
}
