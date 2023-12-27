package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlackstoneSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blackstone_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackstoneSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackstoneSlab(BlockState blockstate) {
        super(blockstate);
    }
}