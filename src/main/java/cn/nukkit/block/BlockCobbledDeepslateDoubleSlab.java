package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslateDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLED_DEEPSLATE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobbledDeepslateDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobbledDeepslateDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Cobbled Deepslate";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockCobbledDeepslateSlab.PROPERTIES.getDefaultState();
    }
}