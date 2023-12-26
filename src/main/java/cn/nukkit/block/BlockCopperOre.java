package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCopperOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:copper_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperOre(BlockState blockstate) {
        super(blockstate);
    }
}