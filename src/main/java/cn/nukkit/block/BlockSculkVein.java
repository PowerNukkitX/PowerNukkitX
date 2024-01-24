package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSculkVein extends BlockLichen {
    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK_VEIN, CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculkVein() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculkVein(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Sculk Vein";
    }
}
