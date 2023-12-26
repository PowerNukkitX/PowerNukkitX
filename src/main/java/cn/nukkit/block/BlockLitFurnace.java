package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLitFurnace extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lit_furnace", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitFurnace(BlockState blockstate) {
        super(blockstate);
    }
}