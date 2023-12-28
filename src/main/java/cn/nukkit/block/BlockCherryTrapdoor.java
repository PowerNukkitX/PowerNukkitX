package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryTrapdoor extends BlockTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}