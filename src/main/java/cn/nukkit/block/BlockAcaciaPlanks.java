package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaPlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:acacia_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaPlanks(BlockState blockstate) {
        super(blockstate);
    }
}