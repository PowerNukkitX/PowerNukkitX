package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleTrapdoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}