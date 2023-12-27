package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleStairs(BlockState blockstate) {
        super(blockstate);
    }
}