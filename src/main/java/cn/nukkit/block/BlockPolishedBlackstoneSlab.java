package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_blackstone_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneSlab(BlockState blockstate) {
        super(blockstate);
    }
}