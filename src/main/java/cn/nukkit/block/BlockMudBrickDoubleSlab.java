package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(MUD_BRICK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMudBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMudBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Double Mud Brick Slab";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockMudBrickSlab.PROPERTIES.getDefaultState();
    }

}