package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMossyCobblestoneStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mossy_cobblestone_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossyCobblestoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossyCobblestoneStairs(BlockState blockstate) {
        super(blockstate);
    }
}