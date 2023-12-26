package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleWoodenSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:double_wooden_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.WOOD_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleWoodenSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleWoodenSlab(BlockState blockstate) {
        super(blockstate);
    }
}