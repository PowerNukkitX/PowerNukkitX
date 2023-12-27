package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}