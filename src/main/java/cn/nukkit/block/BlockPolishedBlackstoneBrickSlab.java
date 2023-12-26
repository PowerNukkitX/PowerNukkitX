package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBrickSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_blackstone_brick_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneBrickSlab(BlockState blockstate) {
        super(blockstate);
    }
}