package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryWood extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_wood", CommonBlockProperties.PILLAR_AXIS, CommonBlockProperties.STRIPPED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryWood(BlockState blockstate) {
        super(blockstate);
    }
}