package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDeepslateDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_deepslate_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDeepslateDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDeepslateDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}