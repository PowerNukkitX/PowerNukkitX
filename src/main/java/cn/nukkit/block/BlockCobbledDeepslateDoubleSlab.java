package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslateDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cobbled_deepslate_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobbledDeepslateDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobbledDeepslateDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}