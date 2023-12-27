package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateBrickStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_brick_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateBrickStairs(BlockState blockstate) {
        super(blockstate);
    }
}