package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDeepslateSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_deepslate_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDeepslateSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDeepslateSlab(BlockState blockstate) {
        super(blockstate);
    }
}