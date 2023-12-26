package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBlueConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueConcrete(BlockState blockstate) {
        super(blockstate);
    }
}